package com.example.kjh.shakeit.beforelogin.model;

import com.example.kjh.shakeit.api.ApiClient;
import com.example.kjh.shakeit.beforelogin.activity.EmailLoginActivity;
import com.example.kjh.shakeit.beforelogin.callback.ResultCallback;
import com.example.kjh.shakeit.beforelogin.contract.EmailLoginContract;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.kjh.shakeit.Statics.ERROR_BAD_REQUEST;
import static com.example.kjh.shakeit.Statics.ERROR_SERVICE_UNAVAILABLE;
import static com.example.kjh.shakeit.Statics.SUCCESS_OK;


public class EmailLoginModel implements EmailLoginContract.Model {

    private String TAG = EmailLoginActivity.class.getSimpleName();

    @Override
    public void login(String email, String password, final ResultCallback callback) {
        Call<ResponseBody> result = ApiClient.create().login(email,password,"e");

        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    switch (response.code()){
                        case SUCCESS_OK:
                            callback.onSuccess(response.body().string());
                            break;
                        case ERROR_SERVICE_UNAVAILABLE:
                            callback.onFailure("SERVICE_UNAVAILABLE");
                            break;
                        case ERROR_BAD_REQUEST:
                            callback.onFailure("SERVER_ERROR");
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });

    }
}
