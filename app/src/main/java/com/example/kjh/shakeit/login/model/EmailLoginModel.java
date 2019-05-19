package com.example.kjh.shakeit.login.model;

import com.example.kjh.shakeit.api.ApiClient;
import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.login.contract.EmailLoginContract;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.kjh.shakeit.app.Constant.ERROR_BAD_REQUEST;
import static com.example.kjh.shakeit.app.Constant.ERROR_SERVICE_UNAVAILABLE;
import static com.example.kjh.shakeit.app.Constant.SUCCESS_OK;

public class EmailLoginModel implements EmailLoginContract.Model {

    /**------------------------------------------------------------------
     메서드 ==> 이메일 로그인 요청
     ------------------------------------------------------------------*/
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
