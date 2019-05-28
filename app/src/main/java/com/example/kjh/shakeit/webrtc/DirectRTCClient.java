package com.example.kjh.shakeit.webrtc;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 직접 TCP 연결을 신호 채널로 사용하는 AppRTCClient 구현
 * 따로 외부 서버가 필요하지 않음
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 21. PM 3:47
 **/
public class DirectRTCClient implements AppRTCClient, TCPChannelClient.TCPChannelEvents {

    private static final String TAG = "DirectRTCClient";
    private static final int DEFAULT_PORT = 8888;

    /** 방의 ID가 IP처럼 보이는지 확인하는 정규식 */
    public static final Pattern IP_PATTERN = Pattern.compile("("
            // IPv4
            + "((\\d+\\.){3}\\d+)|"
            // IPv6
            + "\\[((([0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{1,4})?::"
            + "(([0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{1,4})?)\\]|"
            + "\\[(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4})\\]|"
            // IPv6 without []
            + "((([0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{1,4})?::(([0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{1,4})?)|"
            + "(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4})|"
            // Literals
            + "localhost"
            + ")"
            // Optional port number
            + "(:(\\d+))?");

    private final ExecutorService executor;
    private final SignalingEvents events;
    private TCPChannelClient tcpClient;
    private RoomConnectionParameters connectionParameters;

    private enum ConnectionState {
        NEW,
        CONNECTED,
        CLOSED,
        ERROR
    }

    /** 방 상태의 모든 변경은 스레드를 통해 이루어 짐 */
    private ConnectionState roomState;

    public DirectRTCClient(AppRTCClient.SignalingEvents events) {
        this.events = events;

        executor = Executors.newSingleThreadExecutor();
        roomState = ConnectionState.NEW;
    }

    /** 방에 연결, 방 ID 파라미터 필수 */
    @Override
    public void connectToRoom(RoomConnectionParameters connectionParameters) {
        this.connectionParameters = connectionParameters;

        if(connectionParameters.loopback)
            reportError("Loopback connections aren't supported by DirectRTCClient.");

        executor.execute(this::connectToRoomInternal);
    }

    @Override
    public void disconnectFromRoom() {
        executor.execute(this::disconnectFromRoomInternal);
    }


    /** 방에 연결 */
    private void connectToRoomInternal() {
        this.roomState = ConnectionState.NEW;

        String endpoint = connectionParameters.roomId;

        Matcher matcher = IP_PATTERN.matcher(endpoint);
        if(!matcher.matches()) {
            reportError("roomId must match IP_PATTERN for DirectRTCClient.");
            return;
        }

        String ip = matcher.group(1);
        String portStr = matcher.group(matcher.groupCount());
        int port;

        if(portStr != null) {
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e){
                reportError("Invalid port number: " + portStr);
                return;
            }
        } else
            port = DEFAULT_PORT;

        tcpClient = new TCPChannelClient(executor, this, ip, port);
    }

    /** 방 연결 끊음 */
    private void disconnectFromRoomInternal() {
        roomState = ConnectionState.CLOSED;

        if(tcpClient != null) {
            tcpClient.disconnect();
            tcpClient = null;
        }
        executor.shutdown();
    }

    @Override
    public void sendOfferSdp(final SessionDescription sdp) {
        executor.execute(() -> {
            if(roomState != ConnectionState.CONNECTED) {
                reportError("Sending offer SDP in non connected state.");
                return;
            }
            JSONObject json = new JSONObject();
            jsonPut(json, "sdp", sdp.description);
            jsonPut(json, "type", "offer");
            sendMessage(json.toString());
        });
    }

    @Override
    public void sendAnswerSdp(final SessionDescription sdp) {
        executor.execute(() -> {
            JSONObject json = new JSONObject();
            jsonPut(json, "sdp", sdp.description);
            jsonPut(json, "type", "answer");
            sendMessage(json.toString());
        });
    }

    @Override
    public void sendLocalIceCandidate(final IceCandidate candidate) {
        executor.execute(() -> {
            JSONObject json = new JSONObject();
            jsonPut(json, "type", "candidate");
            jsonPut(json, "label", candidate.sdpMLineIndex);
            jsonPut(json, "id", candidate.sdpMid);
            jsonPut(json, "candidate", candidate.sdp);

            if (roomState != ConnectionState.CONNECTED) {
                reportError("Sending ICE candidate in non connected state.");
                return;
            }
            sendMessage(json.toString());
        });
    }

    /** 제거된 IceCandidates(후보자)를 다른 참여자에게 알림 */
    @Override
    public void sendLocalIceCandidateRemovals(final IceCandidate[] candidates) {
        executor.execute(() -> {
            JSONObject json = new JSONObject();
            jsonPut(json, "type", "remove-candidates");
            JSONArray jsonArray = new JSONArray();
            for (final IceCandidate candidate : candidates) {
                jsonArray.put(toJsonCandidate(candidate));
            }
            jsonPut(json, "candidates", jsonArray);

            if (roomState != ConnectionState.CONNECTED) {
                reportError("Sending ICE candidate removals in non connected state.");
                return;
            }
            sendMessage(json.toString());
        });
    }

    /**------------------------------------------------------------------
     TCPChannelClient Event handlers
     ------------------------------------------------------------------*/

    /** 사용자가 서버측이면 onConnectedToRoom 트리거 */
    @Override
    public void onTCPConnected(boolean isServer) {
        if(isServer) {
            roomState = ConnectionState.CONNECTED;

            SignalingParameters parameters = new SignalingParameters(
                    // 직접 연결에는 Ice Server가 필요하지 않음
                    new LinkedList<>(),
                    isServer,          // Server side acts as the initiator on direct connections.
                    null,       // clientId
                    null,       // wssUrl
                    null,    // wwsPostUrl
                    null,      // offerSdp
                    null   // iceCandidates
            );
            events.onConnectedToRoom(parameters);
        }
    }

    @Override
    public void onTCPMessage(String msg) {
        try {
            JSONObject json = new JSONObject(msg);
            String type = json.optString("type");
            if (type.equals("candidate")) {
                events.onRemoteIceCandidate(toJavaCandidate(json));
            } else if (type.equals("remove-candidates")) {
                JSONArray candidateArray = json.getJSONArray("candidates");
                IceCandidate[] candidates = new IceCandidate[candidateArray.length()];
                for (int i = 0; i < candidateArray.length(); ++i) {
                    candidates[i] = toJavaCandidate(candidateArray.getJSONObject(i));
                }
                events.onRemoteIceCandidatesRemoved(candidates);
            } else if (type.equals("answer")) {
                SessionDescription sdp = new SessionDescription(
                        SessionDescription.Type.fromCanonicalForm(type), json.getString("sdp"));
                events.onRemoteDescription(sdp);
            } else if (type.equals("offer")) {
                SessionDescription sdp = new SessionDescription(
                        SessionDescription.Type.fromCanonicalForm(type), json.getString("sdp"));

                SignalingParameters parameters = new SignalingParameters(
                        // 직접 연결에는 Ice Server가 필요하지 않음
                        new LinkedList<>(),
                        false,      // This code will only be run on the client side. So, we are not the initiator.
                        null,       // clientId
                        null,       // wssUrl
                        null,    // wssPostUrl
                        sdp,               // offerSdp
                        null   // iceCandidates
                );
                roomState = ConnectionState.CONNECTED;
                events.onConnectedToRoom(parameters);
            } else {
                reportError("Unexpected TCP message: " + msg);
            }
        } catch (JSONException e) {
            reportError("TCP message JSON parsing error: " + e.toString());
        }
    }

    @Override
    public void onTCPError(String description) {
        reportError("TCP connection error: " + description);
    }

    @Override
    public void onTCPClose() {
        events.onChannelClose();
    }

    /**------------------------------------------------------------------
     Helper functions
     ------------------------------------------------------------------*/

    private void reportError(final String errorMessage) {
        Log.e(TAG, errorMessage);
        executor.execute(() -> {
            if (roomState != ConnectionState.ERROR) {
                roomState = ConnectionState.ERROR;
                events.onChannelError(errorMessage);
            }
        });
    }

    private void sendMessage(final String message) {
        executor.execute(() -> tcpClient.send(message));
    }

    /** 키 -> 값 json 맵핑 */
    private static void jsonPut(JSONObject json, String key, Object value) {
        try {
            json.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /** JSONObject로 변환 */
    private static JSONObject toJsonCandidate(final IceCandidate candidate) {
        JSONObject json = new JSONObject();
        jsonPut(json, "label", candidate.sdpMLineIndex);
        jsonPut(json, "id", candidate.sdpMid);
        jsonPut(json, "candidate", candidate.sdp);
        return json;
    }

    /** JSON을 자바 객체로 변환 */
    private static IceCandidate toJavaCandidate(JSONObject json) throws JSONException {
        return new IceCandidate(
                json.getString("id"), json.getInt("label"), json.getString("candidate"));
    }

}
