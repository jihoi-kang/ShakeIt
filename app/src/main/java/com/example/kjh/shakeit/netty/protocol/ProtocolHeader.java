package com.example.kjh.shakeit.netty.protocol;

public class ProtocolHeader {

    /** 헤더 길이 */
    public static final int HEADER_LENGTH = 8;

    /** 스타트 확인 */
    public static final short START = (short) 0xabcd;

    /** sign 0x01 ~ 0x03 */
    public static final byte REQUEST = 0x01;
    public static final byte RESPONSE = 0x02;
    public static final byte CALLBACK = 0x03;

    /** type 0x11 ~ */
    public static final byte CONN = 0x11;

}
