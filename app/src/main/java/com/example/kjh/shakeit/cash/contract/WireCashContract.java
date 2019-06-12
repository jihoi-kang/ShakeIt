package com.example.kjh.shakeit.cash.contract;

import android.content.Context;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.User;

public interface WireCashContract {

    interface View {

        void showMessageForLackOfPoint();
        int getAmount();
        User getUser();
        User getOtherUser();
        Context getContext();
        void showMessageForSucces();

    }

    interface Presenter {

        void onClickWire();

    }

    interface Model {

        void wire(int userId, int friendId, int amount, ResultCallback callback);
        void getUser(int userId, ResultCallback callback);
        void sendMessage(String body);

    }

}
