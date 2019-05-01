package com.example.kjh.shakeit.otto;

import com.example.kjh.shakeit.data.User;

public class Events {

    public static class updateProfileEvent {
        private User user;

        public updateProfileEvent(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }

}
