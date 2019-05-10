package com.example.kjh.shakeit.main;

import android.content.Intent;

import com.example.kjh.shakeit.app.App;
import com.example.kjh.shakeit.netty.NettyService;

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;
    private MainContract.Model model;

    public MainPresenter(MainContract.View view, MainContract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void onStart() {
        App.getApplication().startService(new Intent(App.getApplication(), NettyService.class));
    }

    @Override
    public void onDestroy() {
        App.getApplication().stopService(new Intent(App.getApplication(), NettyService.class));
    }
}
