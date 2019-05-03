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

    /** 무엇인가 다시 업데이트 할때 알림 */
    public static class refreshEvent {
        private String message;

        public refreshEvent(String message) {
            this.message =message;
        }

        public String getMessage(){
            return message;
        }
    }

}
