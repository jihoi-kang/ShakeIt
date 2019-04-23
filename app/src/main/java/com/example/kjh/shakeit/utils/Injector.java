package com.example.kjh.shakeit.utils;

import com.example.kjh.shakeit.beforelogin.contract.EmailLoginContract;
import com.example.kjh.shakeit.beforelogin.contract.MainContract;
import com.example.kjh.shakeit.beforelogin.contract.SignUpContract;
import com.example.kjh.shakeit.beforelogin.model.EmailLoginModel;
import com.example.kjh.shakeit.beforelogin.model.MainModel;
import com.example.kjh.shakeit.beforelogin.model.SignUpModel;

public class Injector {

    public static EmailLoginContract.Model provideEmailLoginModel() {
        return new EmailLoginModel();
    }
    public static MainContract.Model provideBeforeLoginMainModel() {
        return new MainModel();
    }

    public static SignUpContract.Model provideSignUpModel() {
        return new SignUpModel();
    }
}
