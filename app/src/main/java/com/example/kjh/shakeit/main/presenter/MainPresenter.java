package com.example.kjh.shakeit.main.presenter;

import com.example.kjh.shakeit.main.contract.MainContract;

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;
    private MainContract.Model model;

    public MainPresenter(MainContract.View view, MainContract.Model model) {
        this.view = view;
        this.model = model;
    }


}
