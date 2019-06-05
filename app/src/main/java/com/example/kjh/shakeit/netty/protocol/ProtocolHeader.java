package com.example.kjh.shakeit.netty.protocol;

public class ProtocolHeader {

    /** 헤더 길이 */
    public static final int HEADER_LENGTH       = 8;

    /** 스타트 확인 */
    public static final short START             = (short) 0xabcd;

    /** sign 0x01 ~ 0x03 */
    public static final byte REQUEST            = 0x01; // Client -> Server
    public static final byte DELIVERY           = 0x02; // Server -> Client
    public static final byte CALLBACK           = 0x03; // Server -> Client

    /** type 0x11 ~ */
    public static final byte CONN               = 0x11; // Netty Connect 후 정보와 함께
    public static final byte MESSAGE            = 0x12; // 채팅 메시지 보낼 때
    public static final byte UPDATE_UNREAD      = 0x13; // 읽지 않은 메시지 읽을 때
    public static final byte CONN_WEBRTC        = 0x14; // 영상 통화 걸 때
    public static final byte DISCONN_WEBRTC     = 0x15; // 영상 통화 끊을 때
    public static final byte SHAKE_ON           = 0x16; // 흔들기 기능 사용할 때
    public static final byte SHAKE_OFF          = 0x17; // 시간 초과 되어 흔들기 종료 할 때
    public static final byte IMAGE              = 0x18; // 채팅 이미지 보낼 때

}
