package com.example.kjh.shakeit.data;

import java.io.Serializable;

/**
 * 채팅 메시지 데이터를 담는 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 8. PM 6:42
 **/
public class ChatHolder implements Serializable {

    private int chatId;
    private int roomId;
    private int userId;
    private String messageType;
    private String messageContent;
    private String sended_at;

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getSended_at() {
        return sended_at;
    }

    public void setSended_at(String sended_at) {
        this.sended_at = sended_at;
    }
}
