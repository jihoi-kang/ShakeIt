package com.example.kjh.shakeit.data;

/**
 * Netty 주고 받을 때 데이터를 담는 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 8. PM 6:43
 **/
public class MessageHolder {

    private byte sign;
    private byte type;
    private String body;

    public byte getSign() {
        return sign;
    }

    public void setSign(byte sign) {
        this.sign = sign;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "MessageHolder{" +
                "sign=" + sign +
                ", type=" + type +
                ", body='" + body + '\'' +
                '}';
    }
}
