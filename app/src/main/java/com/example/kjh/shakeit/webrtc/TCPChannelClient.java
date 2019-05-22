package com.example.kjh.shakeit.webrtc;

import android.util.Log;

import org.webrtc.ThreadUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;

/**
 * 두 IP 주소간에 직접적으로 통신하기 위한 WebSocketChannelClient 대체
 * TCP 연결을 사용하여 신호 처리
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 21. PM 4:14
 **/
public class TCPChannelClient {
    private static final String TAG = "TCPChannelClient";

    private final ExecutorService executor;
    private final ThreadUtils.ThreadChecker executorThreadCheck;
    private final TCPChannelEvents eventListener;
    private TCPSocket socket;

    /** TCP로 부터 전달 받은 메시지 콜백 */
    public interface  TCPChannelEvents {

        void onTCPConnected(boolean server);
        void onTCPMessage(String message);
        void onTCPError(String description);
        void onTCPClose();

    }

    /** IP가 로컬인 경우 수신 서버 시작, 로컬 IP가 아닐 경우 해당 IP에 연결 */
    public TCPChannelClient(
            ExecutorService executor, TCPChannelEvents eventListener, String ip, int port) {
        this.executor = executor;
        executorThreadCheck = new ThreadUtils.ThreadChecker();
        executorThreadCheck.detachThread();
        this.eventListener = eventListener;

        InetAddress address;
        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            reportError("Invalid IP address.");
            return;
        }

        if(address.isAnyLocalAddress())
            socket = new TCPSocketServer(address, port);
        else
            socket = new TCPSocketClient(address, port);

        socket.start();
    }

    /** 아직 연결이 끊어지지 않은 경우 연결 끊음 */
    public void disconnect() {
        executorThreadCheck.checkIsOnValidThread();

        socket.disconnect();
    }

    /** 소켓으로 메시지 보내는 메시지 */
    public void send(String message) {
        executorThreadCheck.checkIsOnValidThread();

        socket.send(message);
    }

    /** onTCPError 이벤트 발생시키는 헬퍼 */
    private void reportError(final String message) {
        Log.e(TAG, "TCP Error: " + message);
        executor.execute(() -> eventListener.onTCPError(message));
    }

    /** 서버 및 사용자 소켓의 기본 클래스 */
    private abstract class TCPSocket extends Thread {
        // Lock
        protected final Object rawSocketLock;
        private PrintWriter out;
        private Socket rawSocket;

        /** 피어에 연결, 잠재적 느린 작업이 될 수 있음 */
        public abstract Socket connect();

        /** 소켓이 서버일 경우 true 반환 */
        public abstract boolean isServer();

        TCPSocket() {
            rawSocketLock = new Object();
        }

        /** Listening Thread */
        @Override
        public void run() {
            Log.d(TAG, "Listening thread started...");

            // Receive connection to temporary variable first, so we don't block.
            Socket tempSocket = connect();
            BufferedReader in;

            Log.d(TAG, "TCP connection established.");

            synchronized (rawSocketLock) {
                if (rawSocket != null) {
                    Log.e(TAG, "Socket already existed and will be replaced.");
                }

                rawSocket = tempSocket;

                // 연결에 실패. 이미 오류에 대한 메시지 보고.
                if (rawSocket == null) {
                    return;
                }

                try {
                    out = new PrintWriter(
                            new OutputStreamWriter(rawSocket.getOutputStream(), Charset.forName("UTF-8")), true);
                    in = new BufferedReader(
                            new InputStreamReader(rawSocket.getInputStream(), Charset.forName("UTF-8")));
                } catch (IOException e) {
                    reportError("Failed to open IO on rawSocket: " + e.getMessage());
                    return;
                }
            }

            Log.v(TAG, "Execute onTCPConnected");
            executor.execute(() -> {
                Log.v(TAG, "Run onTCPConnected");
                eventListener.onTCPConnected(isServer());
            });

            while (true) {
                final String message;
                try {
                    message = in.readLine();
                } catch (IOException e) {
                    synchronized (rawSocketLock) {
                        // 소켓이 닫힐 시
                        if (rawSocket == null) {
                            break;
                        }
                    }

                    reportError("Failed to read from rawSocket: " + e.getMessage());
                    break;
                }

                // 데이터가 수신 되지 않았기 때문에 아마 rawSocket이 종료 되어 있을 수 있음
                if (message == null) {
                    break;
                }

                executor.execute(() -> {
                    Log.v(TAG, "Receive: " + message);
                    eventListener.onTCPMessage(message);
                });
            }

            Log.d(TAG, "Receiving thread exiting...");

            // rawSocket이 연결되어 있으면 종료
            disconnect();
        }

        /** rawSocket이 연결되어 있으면 종료하고 onTCPClose 이벤트 발생 */
        public void disconnect() {
            try {
                synchronized (rawSocketLock) {
                    if(rawSocket != null) {
                        rawSocket.close();
                        rawSocket = null;
                        out = null;

                        executor.execute(() -> eventListener.onTCPClose());
                    }
                }
            } catch (IOException e) {
                reportError("Failed to close rawSocket: " + e.getMessage());
            }
        }

        /** 소켓에 메시지를 보내는 메서드 */
        public void send(String message) {
            Log.v(TAG, "Send: " + message);

            synchronized (rawSocketLock) {
                if (out == null) {
                    reportError("Sending data on closed socket.");
                    return;
                }

                out.write(message + "\n");
                out.flush();
            }
        }

    }

    private class TCPSocketServer extends TCPSocket {
        // rawSocketLock으로 소켓 서버 보호
        private ServerSocket serverSocket;

        final private InetAddress address;
        final private int port;

        public TCPSocketServer(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        /** 소켓을 열고 기다림 */
        @Override
        public Socket connect() {
            Log.d(TAG, "Listening on [" + address.getHostAddress() + "]:" + Integer.toString(port));

            final ServerSocket tempSocket;
            try {
                tempSocket = new ServerSocket(port, 0, address);
            } catch (IOException e) {
                reportError("Failed to create server socket: " + e.getMessage());
                return null;
            }

            synchronized (rawSocketLock) {
                if (serverSocket != null) {
                    Log.e(TAG, "Server rawSocket was already listening and new will be opened.");
                }

                serverSocket = tempSocket;
            }

            try {
                return tempSocket.accept();
            } catch (IOException e) {
                reportError("Failed to receive connection: " + e.getMessage());
                return null;
            }
        }

        /** 연결 종료 메서드 */
        @Override
        public void disconnect() {
            try {
                synchronized (rawSocketLock) {
                    if (serverSocket != null) {
                        serverSocket.close();
                        serverSocket = null;
                    }
                }
            } catch (IOException e) {
                reportError("Failed to close server socket: " + e.getMessage());
            }

            super.disconnect();
        }

        @Override
        public boolean isServer() {
            return true;
        }
    }

    private class TCPSocketClient extends TCPSocket {

        final private InetAddress address;
        final private int port;

        public TCPSocketClient(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        /** 피어에 연결 */
        @Override
        public Socket connect() {
            Log.d(TAG, "Connecting to [" + address.getHostAddress() + "]:" + Integer.toString(port));

            try {
                return new Socket(address, port);
            } catch (IOException e) {
                reportError("Failed to connect: " + e.getMessage());
                return null;
            }
        }

        @Override
        public boolean isServer() {
            return false;
        }
    }

}
