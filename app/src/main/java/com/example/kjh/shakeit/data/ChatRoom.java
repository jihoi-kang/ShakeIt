package com.example.kjh.shakeit.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 채팅방 데이터를 담는 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 8. PM 6:42
 **/
public class ChatRoom implements Serializable, Cloneable {

    private int roomId;
    private ArrayList<User> participants;
    private int memberCount;
    private ChatHolder chatHolder;
    private int unreadCount;

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public ArrayList<User> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<User> participants) {
        this.participants = participants;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public ChatHolder getChatHolder() {
        return chatHolder;
    }

    public void setChatHolder(ChatHolder chatHolder) {
        this.chatHolder = chatHolder;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    /** 얕은 복사 */
    public ChatRoom copy() throws CloneNotSupportedException {
        ChatRoom room = (ChatRoom) clone();
        room.setParticipants((ArrayList<User>) this.participants.clone());
        room.setChatHolder(this.chatHolder.copy());
        return room;
    }

}
