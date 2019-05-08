package com.example.kjh.shakeit.main.friend.contract;

import com.example.kjh.shakeit.callback.ResultCallback;
import com.example.kjh.shakeit.data.User;

import java.util.ArrayList;

public interface TabFriendListContract {

    interface View {

        User getUser();
        void showFriendList(ArrayList<User> friendList);

    }

    interface Presenter {

        void getFriendList();
        void setFriendList();

    }

    interface Model {

        void getFriendList(int userId, ResultCallback callback);

    }

}
