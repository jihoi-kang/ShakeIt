package com.example.kjh.shakeit.main;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.User;

public interface MainContract {

    interface View {

        User getUser();

    }

    interface Presenter {

        void onDestroy();
        void onStart();
        void onCreate();

    }

    interface Model {

        void getChatLogList(int userId, ResultCallback callback);

    }

}
