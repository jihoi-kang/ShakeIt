package com.example.kjh.shakeit.webrtc;

import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

import java.util.List;

/**
 * AppRTC 사용자들의 행동 인터페이스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 21. AM 2:57
 **/
public interface AppRTCClient {

    /** AppRTC 방의 연결 파라미터 구조체 */
    class RoomConnectionParameters {
        public final String roomUrl;
        public final String roomId;
        public final boolean loopback;
        public final String urlParameters;

        public RoomConnectionParameters(String roomUrl, String roomId, boolean loopback, String urlParameters) {
            this.roomUrl = roomUrl;
            this.roomId = roomId;
            this.loopback = loopback;
            this.urlParameters = urlParameters;
        }

        public RoomConnectionParameters(String roomUrl, String roomId, boolean loopback) {
            this(roomUrl, roomId, loopback, null /* urlParameters */);
        }
    }

    /** AppRTC방 URL에 비동기적 연결 */
    void connectToRoom(RoomConnectionParameters connectionParameters);

    /** 다른 참여자에게 offerSDP를 보냄 */
    void sendOfferSdp(final SessionDescription sdp);

    /** 다른 참여자에게 answerSDP를 보냄 */
    void sendAnswerSdp(final SessionDescription sdp);

    /** 다른 참여자에게 IceCandidate(후보자) 보냄 */
    void sendLocalIceCandidate(final IceCandidate candidate);

    /** 다른 참여자에게 제거된 IceCandidates(후보자) 보냄 */
    void sendLocalIceCandidateRemovals(final IceCandidate[] candidates);

    /** 연결 끊기 */
    void disconnectFromRoom();

    /** AppRTC 방의 신호 매개변수를 가진 구조체 */
    class SignalingParameters {
        public final List<PeerConnection.IceServer> iceServers;
        public final boolean initiator;
        public final String clientId;
        public final String wssUrl;
        public final String wssPostUrl;
        public final SessionDescription offerSdp;
        public final List<IceCandidate> iceCandidates;

        public SignalingParameters(List<PeerConnection.IceServer> iceServers, boolean initiator,
                                   String clientId, String wssUrl, String wssPostUrl, SessionDescription offerSdp,
                                   List<IceCandidate> iceCandidates) {
            this.iceServers = iceServers;
            this.initiator = initiator;
            this.clientId = clientId;
            this.wssUrl = wssUrl;
            this.wssPostUrl = wssPostUrl;
            this.offerSdp = offerSdp;
            this.iceCandidates = iceCandidates;
        }
    }

    /** 신호 채널에 전달되는 메시지 콜백 인터페이스 */
    interface SignalingEvents {

        /** 방의 신호 매개 변수가 추출되면 콜백 */
        void onConnectedToRoom(final SignalingParameters params);

        /** 원격 SDP가 수신되면 콜백 */
        void onRemoteDescription(final SessionDescription sdp);

        /** 원격 IceCandidates(후보자)가 수신되면 콜백 */
        void onRemoteIceCandidate(final IceCandidate candidate);

        /** 원격 IceCandidates(후보자)의 제거가 수신되면 콜백 */
        void onRemoteIceCandidatesRemoved(final IceCandidate[] candidates);

        /** 채널이 닫히면 콜백 */
        void onChannelClose();

        /** 채널에 에러 발생시 콜백 */
        void onChannelError(final String description);
    }
}
