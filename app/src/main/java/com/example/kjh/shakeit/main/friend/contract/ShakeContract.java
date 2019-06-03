package com.example.kjh.shakeit.main.friend.contract;

import android.content.Context;

import com.example.kjh.shakeit.data.User;

public interface ShakeContract {

    interface View {

        boolean getIsShaking();
        void executeVibrate();
        void setIsShaking(boolean isShaking);
        void executeThread();
        User getUser();
        void hideUserInfo();
        void showMessageForNotFound();
        void showMessageForFound();
        void showUserInfo(User userInfo);
        String getNameTxt();
        Context getContext();
        User getTargetUser();

    }

    interface Presenter {

        void onCreate();
        void onDestroy();
        void onSensorChanged(float gForce);
        void offShake();
        void onClickProfile();

    }

    interface Model {

        void sendMessageToServer(User user, byte type);

    }

}
