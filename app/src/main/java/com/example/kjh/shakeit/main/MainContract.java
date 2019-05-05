package com.example.kjh.shakeit.main;

import com.example.kjh.shakeit.data.User;

public interface MainContract {

    interface View {
        User getUser();
    }

    interface Presenter {

        void onDestroy();

        void onStart();
    }

    interface Model {
    }

}
