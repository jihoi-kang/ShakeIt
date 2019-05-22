package com.example.kjh.shakeit.webrtc;

import android.util.Log;

import com.example.kjh.shakeit.webrtc.AppRTCClient.SignalingParameters;
import com.example.kjh.shakeit.webrtc.utils.AsyncHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * AppRTC 방 URL을 방에서 사용할 매개 변수로 변환하는 AsyncTask
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 21. PM 5:11
 **/
public class RoomParametersFetcher {

    private static final String TAG = "RoomRTCClient";
    private static final int TURN_HTTP_TIMEOUT_MS = 5000;
    private final RoomParametersFetcherEvents events;
    private final String roomUrl;
    private final String roomMessage;

    /** 방의 매개 변수 가져올 때 호출하는 콜백 */
    public interface RoomParametersFetcherEvents {

        /** 방의 신호 매개변수에 SignalingParameters가 추출되면 콜백 */
        void onSignalingParametersReady(final AppRTCClient.SignalingParameters params);

        /** 방 매개변수 오류 콜백 */
        void onSignalingParametersError(final String description);

    }

    public RoomParametersFetcher(String roomUrl, String roomMessage, final RoomParametersFetcherEvents events) {
        this.roomUrl = roomUrl;
        this.roomMessage = roomMessage;
        this.events = events;
    }

    public void makeRequest() {
        Log.d(TAG, "Connecting to room: " + roomUrl);
        AsyncHttpURLConnection httpConnection =
                new AsyncHttpURLConnection("POST", roomUrl, roomMessage, new AsyncHttpURLConnection.AsyncHttpEvents() {
                    @Override
                    public void onHttpError(String errorMessage) {
                        Log.e(TAG, "Room connection error: " + errorMessage);
                        events.onSignalingParametersError(errorMessage);
                    }

                    @Override
                    public void onHttpComplete(String response) {
                        roomHttpResponseParse(response);
                    }
                });
        httpConnection.send();
    }

    private void roomHttpResponseParse(String response) {
        Log.d(TAG, "Room response: " + response);
        try {
            LinkedList<IceCandidate> iceCandidates = null;
            SessionDescription offerSdp = null;
            JSONObject roomJson = new JSONObject(response);

            String result = roomJson.getString("result");
            if (!result.equals("SUCCESS")) {
                events.onSignalingParametersError("Room response error: " + result);
                return;
            }
            response = roomJson.getString("params");
            roomJson = new JSONObject(response);
            String roomId = roomJson.getString("room_id");
            String clientId = roomJson.getString("client_id");
            String wssUrl = roomJson.getString("wss_url");
            String wssPostUrl = roomJson.getString("wss_post_url");
            boolean initiator = (roomJson.getBoolean("is_initiator"));
            if (!initiator) {
                iceCandidates = new LinkedList<>();
                String messagesString = roomJson.getString("messages");
                JSONArray messages = new JSONArray(messagesString);
                for (int i = 0; i < messages.length(); ++i) {
                    String messageString = messages.getString(i);
                    JSONObject message = new JSONObject(messageString);
                    String messageType = message.getString("type");
                    Log.d(TAG, "GAE->C #" + i + " : " + messageString);
                    if (messageType.equals("offer")) {
                        offerSdp = new SessionDescription(
                                SessionDescription.Type.fromCanonicalForm(messageType), message.getString("sdp"));
                    } else if (messageType.equals("candidate")) {
                        IceCandidate candidate = new IceCandidate(
                                message.getString("id"), message.getInt("label"), message.getString("candidate"));
                        iceCandidates.add(candidate);
                    } else {
                        Log.e(TAG, "Unknown message: " + messageString);
                    }
                }
            }
            Log.d(TAG, "RoomId: " + roomId + ". ClientId: " + clientId);
            Log.d(TAG, "Initiator: " + initiator);
            Log.d(TAG, "WSS url: " + wssUrl);
            Log.d(TAG, "WSS POST url: " + wssPostUrl);

            LinkedList<PeerConnection.IceServer> iceServers =
                    iceServersFromPCConfigJSON(roomJson.getString("pc_config"));
            boolean isTurnPresent = false;
            for (PeerConnection.IceServer server : iceServers) {
                Log.d(TAG, "IceServer: " + server);
                if (server.uri.startsWith("turn:")) {
                    isTurnPresent = true;
                    break;
                }
            }
            // TURN 서버 요청
            if (!isTurnPresent && !roomJson.optString("ice_server_url").isEmpty()) {
                LinkedList<PeerConnection.IceServer> turnServers =
                        requestTurnServers(roomJson.getString("ice_server_url"));
                for (PeerConnection.IceServer turnServer : turnServers) {
                    Log.d(TAG, "TurnServer: " + turnServer);
                    iceServers.add(turnServer);
                }
            }

            SignalingParameters params = new SignalingParameters(
                    iceServers, initiator, clientId, wssUrl, wssPostUrl, offerSdp, iceCandidates);
            events.onSignalingParametersReady(params);
        } catch (JSONException e) {
            events.onSignalingParametersError("Room JSON parsing error: " + e.toString());
        } catch (IOException e) {
            events.onSignalingParametersError("Room IO error: " + e.toString());
        }
    }

    /** 요청 URL을 기반으로 TURN ICE 서버 요청 및 반환 */
    private LinkedList<PeerConnection.IceServer> requestTurnServers(String url) throws IOException, JSONException {
        LinkedList<PeerConnection.IceServer> turnServers = new LinkedList<>();
        Log.d(TAG, "Request TURN from: " + url);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("REFERER", "https://appr.tc");
        connection.setConnectTimeout(TURN_HTTP_TIMEOUT_MS);
        connection.setReadTimeout(TURN_HTTP_TIMEOUT_MS);
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Non-200 response when requesting TURN server from " + url + " : "
                    + connection.getHeaderField(null));
        }
        InputStream responseStream = connection.getInputStream();
        String response = drainStream(responseStream);
        connection.disconnect();
        Log.d(TAG, "TURN response: " + response);
        JSONObject responseJSON = new JSONObject(response);
        JSONArray iceServers = responseJSON.getJSONArray("iceServers");
        for (int i = 0; i < iceServers.length(); ++i) {
            JSONObject server = iceServers.getJSONObject(i);
            JSONArray turnUrls = server.getJSONArray("urls");
            String username = server.has("username") ? server.getString("username") : "";
            String credential = server.has("credential") ? server.getString("credential") : "";
            for (int j = 0; j < turnUrls.length(); j++) {
                String turnUrl = turnUrls.getString(j);
                turnServers.add(new PeerConnection.IceServer(turnUrl, username, credential));
            }
        }
        return turnServers;
    }

    /** WebRTCPeerConnection 구성 문자열에 의해 기술된 ICE 서버 목록 반환 */
    private LinkedList<PeerConnection.IceServer> iceServersFromPCConfigJSON(String pcConfig) throws JSONException {
        JSONObject json = new JSONObject(pcConfig);
        JSONArray servers = json.getJSONArray("iceServers");
        LinkedList<PeerConnection.IceServer> ret = new LinkedList<>();
        for (int i = 0; i < servers.length(); ++i) {
            JSONObject server = servers.getJSONObject(i);
            String url = server.getString("urls");
            String credential = server.has("credential") ? server.getString("credential") : "";
            ret.add(new PeerConnection.IceServer(url, "", credential));
        }
        return ret;
    }

    /** InputStream 내용을 문자열 반환 */
    private static String drainStream(InputStream in) {
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}
