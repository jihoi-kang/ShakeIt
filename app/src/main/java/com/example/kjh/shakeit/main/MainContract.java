package com.example.kjh.shakeit.main;

import android.graphics.Point;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.User;

import java.util.ArrayList;


public interface MainContract {

    interface View {

        User getUser();
        Point getPoint();
        ArrayList<User> getFriends();

    }

    interface Presenter {

        void onDestroy();
        void onStart();
        void onCreate();

    }

    interface Model {

        void getChatLogList(int userId, Point size, ResultCallback callback);

    }

}
