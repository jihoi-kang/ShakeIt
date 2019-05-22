package com.example.kjh.shakeit.webrtc;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.example.kjh.shakeit.webrtc.RoomParametersFetcher.RoomParametersFetcherEvents;
import com.example.kjh.shakeit.webrtc.WebSocketChannelClient.WebSocketChannelEvents;
import com.example.kjh.shakeit.webrtc.WebSocketChannelClient.WebSocketConnectionState;
import com.example.kjh.shakeit.webrtc.utils.AsyncHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * https://appr.tc 방들을 이용하여 채팅 신호 협상
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 21. PM 6:16
 **/
public class WebSocketRTCClient implements AppRTCClient, WebSocketChannelEvents {

    private static final String TAG = "WSRTCClient";
    private static final String ROOM_JOIN = "join";
    private static final String ROOM_MESSAGE = "message";
    private static final String ROOM_LEAVE = "leave";

    private enum ConnectionState {
        NEW,
        CONNECTED,
        CLOSED,
        ERROR
    }

    private enum MessageType {
        MESSAGE,
        LEAVE
    }

    private final Handler handler;
    private boolean initiator;
    private SignalingEvents events;
    private WebSocketChannelClient wsClient;
    private ConnectionState roomState;
    private RoomConnectionParameters connectionParameters;
    private String messageUrl;
    private String leaveUrl;

    public WebSocketRTCClient(SignalingEvents events) {
        this.events = events;
        roomState = ConnectionState.NEW;
        final HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    /** AppRTC 방 URL에 비동기적 연결하고 방 매개변수 검색 & WebSocket 서베에 연결 */
    @Override
    public void connectToRoom(RoomConnectionParameters connectionParameters) {
        this.connectionParameters = connectionParameters;
        handler.post(() -> connectToRoomInternal());
    }

    @Override
    public void disconnectFromRoom() {
        handler.post(() -> {
            disconnectFromRoomInternal();
            handler.getLooper().quit();
        });
    }

    /** 방에 연결 */
    private void connectToRoomInternal() {
        String connectionUrl = getConnectionUrl(connectionParameters);
        Log.d(TAG, "Connect to room: " + connectionUrl);
        roomState = ConnectionState.NEW;
        wsClient = new WebSocketChannelClient(handler, this);

        RoomParametersFetcherEvents callbacks = new RoomParametersFetcherEvents() {
            @Override
            public void onSignalingParametersReady(final SignalingParameters params) {
                WebSocketRTCClient.this.handler.post(() -> {
                    WebSocketRTCClient.this.signalingParametersReady(params);
                });
            }

            @Override
            public void onSignalingParametersError(String description) {
                WebSocketRTCClient.this.reportError(description);
            }
        };

        new RoomParametersFetcher(connectionUrl, null, callbacks).makeRequest();
    }

    /** 방 연결을 끊고 "bye" 메시지를 보냄 */
    private void disconnectFromRoomInternal() {
        Log.d(TAG, "Disconnect. Room state: " + roomState);
        if (roomState == ConnectionState.CONNECTED) {
            Log.d(TAG, "Closing room.");
            sendPostMessage(MessageType.LEAVE, leaveUrl, null);
        }
        roomState = ConnectionState.CLOSED;
        if (wsClient != null) {
            wsClient.disconnect(true);
        }
    }

    /** 연결을 가져오고 메시지를 게시하고 남기기 위한 헬퍼 */
    private String getConnectionUrl(RoomConnectionParameters connectionParameters) {
        return connectionParameters.roomUrl + "/" + ROOM_JOIN + "/" + connectionParameters.roomId
                + getQueryString(connectionParameters);
    }

    private String getMessageUrl(
            RoomConnectionParameters connectionParameters, SignalingParameters signalingParameters) {
        return connectionParameters.roomUrl + "/" + ROOM_MESSAGE + "/" + connectionParameters.roomId
                + "/" + signalingParameters.clientId + getQueryString(connectionParameters);
    }

    private String getLeaveUrl(
            RoomConnectionParameters connectionParameters, SignalingParameters signalingParameters) {
        return connectionParameters.roomUrl + "/" + ROOM_LEAVE + "/" + connectionParameters.roomId + "/"
                + signalingParameters.clientId + getQueryString(connectionParameters);
    }

    private String getQueryString(RoomConnectionParameters connectionParameters) {
        if (connectionParameters.urlParameters != null) {
            return "?" + connectionParameters.urlParameters;
        } else {
            return "";
        }
    }

    /** 룸 매개 변수 추출시 발생하는 콜백 */
    private void signalingParametersReady(final SignalingParameters signalingParameters) {
        Log.d(TAG, "Room connection completed.");
        if (connectionParameters.loopback
                && (!signalingParameters.initiator || signalingParameters.offerSdp != null)) {
            reportError("Loopback room is busy.");
            return;
        }
        if (!connectionParameters.loopback && !signalingParameters.initiator
                && signalingParameters.offerSdp == null) {
            Log.w(TAG, "No offer SDP in room response.");
        }
        initiator = signalingParameters.initiator;
        messageUrl = getMessageUrl(connectionParameters, signalingParameters);
        leaveUrl = getLeaveUrl(connectionParameters, signalingParameters);
        Log.d(TAG, "Message URL: " + messageUrl);
        Log.d(TAG, "Leave URL: " + leaveUrl);
        roomState = ConnectionState.CONNECTED;

        // 연결을 주고 신호 매개 변수 이벤트 발생
        events.onConnectedToRoom(signalingParameters);

        // 웹 소켓 연결 및 등록
        wsClient.connect(signalingParameters.wssUrl, signalingParameters.wssPostUrl);
        wsClient.register(connectionParameters.roomId, signalingParameters.clientId);
    }

    /** local offer SDP를 다른 참여자에게 보냄 */
    @Override
    public void sendOfferSdp(SessionDescription sdp) {
        handler.post(() -> {
            if (roomState != ConnectionState.CONNECTED) {
                reportError("Sending offer SDP in non connected state.");
                return;
            }
            JSONObject json = new JSONObject();
            jsonPut(json, "sdp", sdp.description);
            jsonPut(json, "type", "offer");
            sendPostMessage(MessageType.MESSAGE, messageUrl, json.toString());
            if (connectionParameters.loopback) {
                // In loopback mode rename this offer to answer and route it back.
                SessionDescription sdpAnswer = new SessionDescription(
                        SessionDescription.Type.fromCanonicalForm("answer"), sdp.description);
                events.onRemoteDescription(sdpAnswer);
            }
        });
    }

    /** local answer SDP를 다른 참여자에게 보냄 */
    @Override
    public void sendAnswerSdp(SessionDescription sdp) {
        handler.post(() -> {
            if (connectionParameters.loopback) {
                Log.e(TAG, "Sending answer in loopback mode.");
                return;
            }
            JSONObject json = new JSONObject();
            jsonPut(json, "sdp", sdp.description);
            jsonPut(json, "type", "answer");
            wsClient.send(json.toString());
        });
    }

    /** Ice candidate(후보자)를 다른 참여자에게 보냄 */
    @Override
    public void sendLocalIceCandidate(final IceCandidate candidate) {
        handler.post(() -> {
            JSONObject json = new JSONObject();
            jsonPut(json, "type", "candidate");
            jsonPut(json, "label", candidate.sdpMLineIndex);
            jsonPut(json, "id", candidate.sdpMid);
            jsonPut(json, "candidate", candidate.sdp);
            if (initiator) {
                // 통화 개시자는 Ice Candidate를 GAE 서버에 보냄
                if (roomState != ConnectionState.CONNECTED) {
                    reportError("Sending ICE candidate in non connected state.");
                    return;
                }
                sendPostMessage(MessageType.MESSAGE, messageUrl, json.toString());
                if (connectionParameters.loopback) {
                    events.onRemoteIceCandidate(candidate);
                }
            } else {
                // 호출 수신자는 Ice Candidate를 웹소켓 서버에 보냄
                wsClient.send(json.toString());
            }
        });
    }

    /** 제거된 Ice Candidates를 다른 참여자에게 보냄 */
    @Override
    public void sendLocalIceCandidateRemovals(final IceCandidate[] candidates) {
        handler.post(() -> {
            JSONObject json = new JSONObject();
            jsonPut(json, "type", "remove-candidates");
            JSONArray jsonArray = new JSONArray();
            for (final IceCandidate candidate : candidates) {
                jsonArray.put(toJsonCandidate(candidate));
            }
            jsonPut(json, "candidates", jsonArray);
            if (initiator) {
                // 통화 개시자는 Ice Candidate를 GAE 서버에 보냄
                if (roomState != ConnectionState.CONNECTED) {
                    reportError("Sending ICE candidate removals in non connected state.");
                    return;
                }
                sendPostMessage(MessageType.MESSAGE, messageUrl, json.toString());
                if (connectionParameters.loopback) {
                    events.onRemoteIceCandidatesRemoved(candidates);
                }
            } else {
                // 호출 수신자는 Ice Candidate를 웹소켓 서버에 보냄
                wsClient.send(json.toString());
            }
        });
    }

    /** WebSocketChannelClient에 의해 호출 */
    @Override
    public void onWebSocketMessage(final String msg) {
        if (wsClient.getState() != WebSocketConnectionState.REGISTERED) {
            Log.e(TAG, "Got WebSocket message in non registered state.");
            return;
        }
        try {
            JSONObject json = new JSONObject(msg);
            String msgText = json.getString("msg");
            String errorText = json.optString("error");
            if (msgText.length() > 0) {
                json = new JSONObject(msgText);
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
                    if (initiator) {
                        SessionDescription sdp = new SessionDescription(
                                SessionDescription.Type.fromCanonicalForm(type), json.getString("sdp"));
                        events.onRemoteDescription(sdp);
                    } else {
                        reportError("Received answer for call initiator: " + msg);
                    }
                } else if (type.equals("offer")) {
                    if (!initiator) {
                        SessionDescription sdp = new SessionDescription(
                                SessionDescription.Type.fromCanonicalForm(type), json.getString("sdp"));
                        events.onRemoteDescription(sdp);
                    } else {
                        reportError("Received offer for call receiver: " + msg);
                    }
                } else if (type.equals("bye")) {
                    events.onChannelClose();
                } else {
                    reportError("Unexpected WebSocket message: " + msg);
                }
            } else {
                if (errorText != null && errorText.length() > 0) {
                    reportError("WebSocket error message: " + errorText);
                } else {
                    reportError("Unexpected WebSocket message: " + msg);
                }
            }
        } catch (JSONException e) {
            reportError("WebSocket message JSON parsing error: " + e.toString());
        }
    }

    @Override
    public void onWebSocketClose() {
        events.onChannelClose();
    }

    @Override
    public void onWebSocketError(String description) {
        reportError("WebSocket error: " + description);
    }

    /**------------------------------------------------------------------
     Helper functions
     ------------------------------------------------------------------*/

    private void reportError(final String errorMessage) {
        Log.e(TAG, errorMessage);
        handler.post(() -> {
            if (roomState != ConnectionState.ERROR) {
                roomState = ConnectionState.ERROR;
                events.onChannelError(errorMessage);
            }
        });
    }

    /** 키 -> 값 json 맵핑 */
    private static void jsonPut(JSONObject json, String key, Object value) {
        try {
            json.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /** SDP or ICE candidate를 룸 서버에 보냄 */
    private void sendPostMessage(
            final MessageType messageType, final String url, final String message) {
        String logInfo = url;
        if (message != null) {
            logInfo += ". Message: " + message;
        }
        Log.d(TAG, "C->GAE: " + logInfo);
        AsyncHttpURLConnection httpConnection =
                new AsyncHttpURLConnection("POST", url, message, new AsyncHttpURLConnection.AsyncHttpEvents() {
                    @Override
                    public void onHttpError(String errorMessage) {
                        reportError("GAE POST error: " + errorMessage);
                    }

                    @Override
                    public void onHttpComplete(String response) {
                        if (messageType == MessageType.MESSAGE) {
                            try {
                                JSONObject roomJson = new JSONObject(response);
                                String result = roomJson.getString("result");
                                if (!result.equals("SUCCESS")) {
                                    reportError("GAE POST error: " + result);
                                }
                            } catch (JSONException e) {
                                reportError("GAE POST JSON error: " + e.toString());
                            }
                        }
                    }
                });
        httpConnection.send();
    }

    /** JSONObject로 변환 */
    private JSONObject toJsonCandidate(final IceCandidate candidate) {
        JSONObject json = new JSONObject();
        jsonPut(json, "label", candidate.sdpMLineIndex);
        jsonPut(json, "id", candidate.sdpMid);
        jsonPut(json, "candidate", candidate.sdp);
        return json;
    }

    /** JSON을 자바 객체로 변환 */
    IceCandidate toJavaCandidate(JSONObject json) throws JSONException {
        return new IceCandidate(
                json.getString("id"), json.getInt("label"), json.getString("candidate"));
    }

}
