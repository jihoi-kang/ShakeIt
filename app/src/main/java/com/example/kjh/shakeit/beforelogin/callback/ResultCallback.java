package com.example.kjh.shakeit.beforelogin.callback;

public interface ResultCallback {
    void onSuccess(String body);
    void onFailure(String errorMsg);
}
