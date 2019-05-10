package com.example.kjh.shakeit.otto;

import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.User;

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
            this.message =message;
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

}
