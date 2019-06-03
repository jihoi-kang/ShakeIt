package com.example.kjh.shakeit.main.friend.contract;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.User;

public interface ProfileDetailContract {

    interface View {

        User getUser();
        User getFriend();
        void setIsFriend(boolean condition);
        void showAfterAddFriend();

    }

    interface Presenter {

        void checkIsFriend();
        void addFriend();

    }

    interface Model {

        void isFriend(int userId, int friendId, ResultCallback callback);
        void addFriend(int _id, int friendId, ResultCallback callback);

    }

}
