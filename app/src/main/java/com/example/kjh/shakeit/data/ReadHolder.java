package com.example.kjh.shakeit.data;

import java.util.ArrayList;

/**
 * 채팅 메시지 읽을 때 사용되는 데이터 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 6. 12. PM 5:22
 **/
public class ReadHolder {

    private int userId;
    private int roomId;
    private ArrayList<Integer> participants;
    private ArrayList<Integer> chatIds;

    public ReadHolder(int userId, int roomId, ArrayList<Integer> participants, ArrayList<Integer> chatIds) {
        this.userId = userId;
        this.roomId = roomId;
        this.participants = participants;
        this.chatIds = chatIds;
    }

    public ReadHolder() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public ArrayList<Integer> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Integer> participants) {
        this.participants = participants;
    }

    public ArrayList<Integer> getChatIds() {
        return chatIds;
    }

    public void setChatIds(ArrayList<Integer> chatIds) {
        this.chatIds = chatIds;
    }
}
