package com.example.kjh.shakeit.cash.contract;

import android.content.Context;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;

public interface WireCashContract {

    interface View {

        void showMessageForLackOfPoint();
        int getAmount();
        User getUser();
        User getOtherUser();
        Context getContext();
        void showMessageForSuccess();
        ChatRoom getChatRoom();
        void setChatRoom(String body);

    }

    interface Presenter {

        void onClickWire();
        void getChatRoom();

    }

    interface Model {

        void wire(int userId, int friendId, int amount, ResultCallback callback);
        void sendMessage(String body);
        void getChatRoom(int userId, int friendId, ResultCallback callback);

    }

}
