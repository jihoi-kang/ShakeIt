package com.example.kjh.shakeit.cash.contract;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.User;

public interface ChargeContract {

    interface View {

        int getAmount();
        void loadUrl(String url);
        User getUser();

    }

    interface Presenter {

        void onCreate();

    }

    interface Model {

        void chargeReady(int userId, int amount, ResultCallback callback);

    }

}
