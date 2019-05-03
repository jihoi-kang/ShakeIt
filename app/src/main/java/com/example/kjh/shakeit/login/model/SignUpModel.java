package com.example.kjh.shakeit.login.model;

import com.example.kjh.shakeit.api.ApiClient;
import com.example.kjh.shakeit.callback.ResultCallback;
import com.example.kjh.shakeit.login.contract.SignUpContract;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.example.kjh.shakeit.Statics.ERROR_BAD_REQUEST;
import static com.example.kjh.shakeit.Statics.ERROR_SERVICE_UNAVAILABLE;
import static com.example.kjh.shakeit.Statics.SUCCESS_CREATED;

public class SignUpModel implements SignUpContract.Model {

    /**------------------------------------------------------------------
     메서드 ==> 회원가입
     ------------------------------------------------------------------*/
    @Override
    public void signUp(String email, String password, String name, final ResultCallback callback) {
        Call<ResponseBody> result = ApiClient.create().signUp(email, password, name, "e");

        result.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    switch (response.code()){
                        case SUCCESS_CREATED:
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
