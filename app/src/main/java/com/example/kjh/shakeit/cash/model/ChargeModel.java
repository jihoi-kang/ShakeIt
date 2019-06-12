package com.example.kjh.shakeit.cash.model;

import com.example.kjh.shakeit.api.ApiClient;
import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.cash.contract.ChargeContract;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.kjh.shakeit.app.Constant.ERROR_BAD_REQUEST;
import static com.example.kjh.shakeit.app.Constant.SUCCESS_OK;

public class ChargeModel implements ChargeContract.Model {

    /**------------------------------------------------------------------
     메서드 ==> 카카오페이 충전 ==> 결제 준비 URL 반환
     ------------------------------------------------------------------*/
    @Override
    public void chargeReady(int userId, int amount, ResultCallback callback) {
        Call<ResponseBody> result = ApiClient.create().chargeReady(userId, amount);
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                switch (response.code()){
                    case SUCCESS_OK:
                        try {
                            callback.onSuccess(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case ERROR_BAD_REQUEST: callback.onFailure("SERVER_ERROR"); break;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }
}
