package com.example.kjh.shakeit.otto;

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
    public static class noticeEvent {
        private String message;

        public noticeEvent(String message) {
            this.message =message;
        }

        public String getMessage(){
            return message;
        }
    }

}
