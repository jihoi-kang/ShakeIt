package com.example.kjh.shakeit.data;

public class MessageHolder {

    private byte sign;
    private byte type;
    private ChatLog chatLog;

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

    public ChatLog getChatLog() {
        return chatLog;
    }

    public void setChatLog(ChatLog chatLog) {
        this.chatLog = chatLog;
    }

    @Override
    public String toString() {
        return "MessageHolder{" +
                "sign=" + sign +
                ", type=" + type +
                ", chatLog=" + chatLog +
                '}';
    }
}
