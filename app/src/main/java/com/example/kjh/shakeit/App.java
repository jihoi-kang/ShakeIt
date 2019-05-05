package com.example.kjh.shakeit;

import android.app.Application;

import com.example.kjh.shakeit.utils.ShareUtil;

public class App extends Application {

    private static App application;

    @Override
    public void onCreate() {
        super.onCreate();

        ShareUtil.init(this);
        application = this;
    }

    public static App getApplication() {
        return application;
    }
}
