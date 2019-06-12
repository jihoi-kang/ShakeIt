package com.example.kjh.shakeit.data;

/**
 * 송금 관련 데이터 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 6. 12. PM 5:22
 **/
public class WireHolder {

    private int userId;
    private int friendId;
    private int amount;

    public WireHolder() {
    }

    public WireHolder(int userId, int friendId, int amount) {
        this.userId = userId;
        this.friendId = friendId;
        this.amount = amount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
