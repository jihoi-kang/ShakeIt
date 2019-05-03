package com.example.kjh.shakeit.login.model;

import com.example.kjh.shakeit.api.ApiClient;
import com.example.kjh.shakeit.callback.ResultCallback;
import com.example.kjh.shakeit.login.contract.MainContract;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.kjh.shakeit.Statics.ERROR_BAD_REQUEST;
import static com.example.kjh.shakeit.Statics.ERROR_SERVICE_UNAVAILABLE;
import static com.example.kjh.shakeit.Statics.SUCCESS_OK;

public class MainModel implements MainContract.Model {

    /**------------------------------------------------------------------
     메서드 ==> 소셜 로그인 요청
     ------------------------------------------------------------------*/
    @Override
    public void socialLogin(String email, String uid, String displayName, String photoUrl, String login_type, final ResultCallback callback) {
        Call<ResponseBody> result = ApiClient.create().socialLogin(email, uid, displayName, photoUrl, login_type);

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

    /**------------------------------------------------------------------
     메서드 ==> 사용자 정보 받아오기
     ------------------------------------------------------------------*/
    @Override
    public void getUser(int id, final ResultCallback callback) {
        Call<ResponseBody> result = ApiClient.create().getUserById(id);

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
