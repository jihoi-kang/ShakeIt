package com.example.kjh.shakeit.webrtc;

import android.os.Handler;
import android.util.Log;

import com.example.kjh.shakeit.webrtc.utils.AsyncHttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;

/**
 * WebSocket Client 구현
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 21. PM 5:35
 **/
public class WebSocketChannelClient {

    private static final String TAG = "WSChannelRTCClient";
    private static final int CLOSE_TIMEOUT = 1000;
    private final WebSocketChannelEvents events;
    private final Handler handler;
    private WebSocketConnection ws;
    private String wsServerUrl;
    private String postServerUrl;
    private String roomID;
    private String clientID;
    private WebSocketConnectionState state;

    private WebSocketObserver wsObserver;
    private final Object closeEventLock = new Object();
    private boolean closeEvent;

    /** WebSocket의 Send 대기열. register() 호출되면 메시지가 대기열에 추가 */
    private final LinkedList<String> wsSendQueue;

    /** 가능한 WebSocket 연결 상태들 */
    public enum WebSocketConnectionState {
        NEW,
        CONNECTED,
        REGISTERED,
        CLOSED,
        ERROR
    }

    /** WebSocket에서 전달되는 메시지에 대한 콜백 */
    public interface WebSocketChannelEvents {

        void onWebSocketMessage(final String message);
        void onWebSocketClose();
        void onWebSocketError(final String description);

    }

    public WebSocketChannelClient(Handler handler, WebSocketChannelEvents events) {
        this.handler = handler;
        this.events = events;
        roomID = null;
        clientID = null;
        wsSendQueue = new LinkedList<>();
        state = WebSocketConnectionState.NEW;
    }

    public WebSocketConnectionState getState() {
        return state;
    }

    public void connect(final String wsUrl, final String postUrl) {
        checkIfCalledOnValidThread();
        if (state != WebSocketConnectionState.NEW) {
            Log.e(TAG, "WebSocket is already connected.");
            return;
        }
        wsServerUrl = wsUrl;
        postServerUrl = postUrl;
        closeEvent = false;

        Log.d(TAG, "Connecting WebSocket to: " + wsUrl + ". Post URL: " + postUrl);
        ws = new WebSocketConnection();
        wsObserver = new WebSocketObserver();
        try {
            ws.connect(new URI(wsServerUrl), wsObserver);
        } catch (URISyntaxException e) {
            reportError("URI error: " + e.getMessage());
        } catch (WebSocketException e) {
            reportError("WebSocket connection error: " + e.getMessage());
        }
    }

    public void register(final String roomID, final String clientID) {
        checkIfCalledOnValidThread();
        this.roomID = roomID;
        this.clientID = clientID;
        if (state != WebSocketConnectionState.CONNECTED) {
            Log.w(TAG, "WebSocket register() in state " + state);
            return;
        }
        Log.d(TAG, "Registering WebSocket for room " + roomID + ". ClientID: " + clientID);
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", "register");
            json.put("roomid", roomID);
            json.put("clientid", clientID);
            Log.d(TAG, "C->WSS: " + json.toString());
            ws.sendTextMessage(json.toString());
            state = WebSocketConnectionState.REGISTERED;
            // 이전 누적된 메시지를 보낸다
            for (String sendMessage : wsSendQueue) {
                send(sendMessage);
            }
            wsSendQueue.clear();
        } catch (JSONException e) {
            reportError("WebSocket register JSON error: " + e.getMessage());
        }
    }

    public void send(String message) {
        checkIfCalledOnValidThread();
        switch (state) {
            case NEW:
            case CONNECTED:
                // 나가는 메시지를 저장하고 websocket 클라이언트가 등록 된 후에 보내십시오.
                Log.d(TAG, "WS ACC: " + message);
                wsSendQueue.add(message);
                return;
            case ERROR:
            case CLOSED:
                Log.e(TAG, "WebSocket send() in error or closed state : " + message);
                return;
            case REGISTERED:
                JSONObject json = new JSONObject();
                try {
                    json.put("cmd", "send");
                    json.put("msg", message);
                    message = json.toString();
                    Log.d(TAG, "C->WSS: " + message);
                    ws.sendTextMessage(message);
                } catch (JSONException e) {
                    reportError("WebSocket send JSON error: " + e.getMessage());
                }
                break;
        }
    }

    /** WebSocket을 열기전 WebSocket 메시지를 보내는데 사용 될수 있음 */
    public void post(String message) {
        checkIfCalledOnValidThread();
        sendWSSMessage("POST", message);
    }

    public void disconnect(boolean waitForComplete) {
        checkIfCalledOnValidThread();
        Log.d(TAG, "Disconnect WebSocket. State: " + state);
        if (state == WebSocketConnectionState.REGISTERED) {
            // 웹 소켓 서버에 "bye" 보내기
            send("{\"type\": \"bye\"}");
            state = WebSocketConnectionState.CONNECTED;
            // 웹 소켓 서버에 DELETE HTTP 보내기
            sendWSSMessage("DELETE", "");
        }
        // CONNECTED or ERROR상태에서만 웹소켓 닫을 수 있음
        if (state == WebSocketConnectionState.CONNECTED || state == WebSocketConnectionState.ERROR) {
            ws.disconnect();
            state = WebSocketConnectionState.CLOSED;

            /** WebSocket close 이벤트 발생시 WebSocket 라이브러리가 삭제 된 루퍼 스레드에 보류중인 메시지를 보내지 않도록 기다립니다. */
            if (waitForComplete) {
                synchronized (closeEventLock) {
                    while (!closeEvent) {
                        try {
                            closeEventLock.wait(CLOSE_TIMEOUT);
                            break;
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Wait error: " + e.toString());
                        }
                    }
                }
            }
        }
        Log.d(TAG, "Disconnecting WebSocket done.");
    }

    private void reportError(final String errorMessage) {
        Log.e(TAG, errorMessage);
        handler.post(() -> {
            if (state != WebSocketConnectionState.ERROR) {
                state = WebSocketConnectionState.ERROR;
                events.onWebSocketError(errorMessage);
            }
        });
    }

    /** 비동기적으로 WebSocket 서버에게 POST / DELETE 를 보내는 메서드 */
    private void sendWSSMessage(final String method, final String message) {
        String postUrl = postServerUrl + "/" + roomID + "/" + clientID;
        Log.d(TAG, "WS " + method + " : " + postUrl + " : " + message);
        AsyncHttpURLConnection httpConnection =
                new AsyncHttpURLConnection(method, postUrl, message, new AsyncHttpURLConnection.AsyncHttpEvents() {
                    @Override
                    public void onHttpError(String errorMessage) {
                        reportError("WS " + method + " error: " + errorMessage);
                    }

                    @Override
                    public void onHttpComplete(String response) {
                    }
                });
        httpConnection.send();
    }

    /** 루퍼 스레드에서 호출 되었는지 확인하는 메서드 */
    private void checkIfCalledOnValidThread() {
        if(Thread.currentThread() != handler.getLooper().getThread()) {
            throw new IllegalStateException("WebSocket method is not called on valid thread");
        }
    }

    private class WebSocketObserver implements WebSocket.WebSocketConnectionObserver {
        @Override
        public void onOpen() {
            Log.d(TAG, "WebSocket connection opened to: " + wsServerUrl);
            handler.post(() -> {
                state = WebSocketConnectionState.CONNECTED;
                // 보류중인 등록 요청 있는지 확인
                if (roomID != null && clientID != null) {
                    register(roomID, clientID);
                }
            });
        }

        @Override
        public void onClose(WebSocketCloseNotification code, String reason) {
            Log.d(TAG, "WebSocket connection closed. Code: " + code + ". Reason: " + reason + ". State: "
                    + state);
            synchronized (closeEventLock) {
                closeEvent = true;
                closeEventLock.notify();
            }
            handler.post(() -> {
                if (state != WebSocketConnectionState.CLOSED) {
                    state = WebSocketConnectionState.CLOSED;
                    events.onWebSocketClose();
                }
            });
        }

        @Override
        public void onTextMessage(String payload) {
            Log.d(TAG, "WSS->C: " + payload);
            final String message = payload;
            handler.post(() -> {
                if (state == WebSocketConnectionState.CONNECTED
                        || state == WebSocketConnectionState.REGISTERED) {
                    events.onWebSocketMessage(message);
                }
            });
        }

        @Override
        public void onRawTextMessage(byte[] bytes) { }

        @Override
        public void onBinaryMessage(byte[] bytes) { }
    }

}
