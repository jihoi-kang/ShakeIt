package com.example.kjh.shakeit.otto;

import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.User;

import java.util.ArrayList;

public class Events {

    /** 프로필 업데이트 */
    public static class updateProfileEvent {
        private User user;

        public updateProfileEvent(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }

    /** 알림 */
    public static class noticeEventStr {
        private String message;

        public noticeEventStr(String message) {
            this.message = message;
        }

        public String getMessage(){
            return message;
        }
    }

    /** Netty Message */
    public static class nettyEvent {
        private MessageHolder holder;

        public nettyEvent(MessageHolder holder) {
            this.holder = holder;
        }

        public MessageHolder getMessageHolder(){
            return holder;
        }
    }

    /** 친구목록 셋팅 될때마다 Main에 보내줌. Because, 채팅방 만들때 대상 선택하기 위해 필요 */
    public static class friendEvent {
        private ArrayList<User> friends;

        public friendEvent(ArrayList<User> friends) {
            this.friends = friends;
        }

        public ArrayList<User> getFriends(){
            return friends;
        }
    }

    /** WebRTC Message */
    public static class webRTCEvent {
        private String message;

        public webRTCEvent(String message) {
            this.message = message;
        }

        public String getMessage(){
            return message;
        }
    }

}
