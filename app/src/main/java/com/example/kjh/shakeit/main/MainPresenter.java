package com.example.kjh.shakeit.main;

import com.example.kjh.shakeit.main.MainContract;

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;
    private MainContract.Model model;

    public MainPresenter(MainContract.View view, MainContract.Model model) {
        this.view = view;
        this.model = model;
    }


}
