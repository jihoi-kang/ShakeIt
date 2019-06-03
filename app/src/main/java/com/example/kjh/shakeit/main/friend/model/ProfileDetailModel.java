package com.example.kjh.shakeit.main.friend.model;

import com.example.kjh.shakeit.api.ApiClient;
import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.main.friend.contract.ProfileDetailContract;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.kjh.shakeit.app.Constant.ERROR_BAD_REQUEST;
import static com.example.kjh.shakeit.app.Constant.ERROR_SERVICE_UNAVAILABLE;
import static com.example.kjh.shakeit.app.Constant.SUCCESS_CREATED;
import static com.example.kjh.shakeit.app.Constant.SUCCESS_OK;

public class ProfileDetailModel implements ProfileDetailContract.Model {

    /**------------------------------------------------------------------
     메서드 ==> userId가 friendId와 친구추가가 되어 있는지 확인
     ------------------------------------------------------------------*/
    @Override
    public void isFriend(int userId, int friendId, ResultCallback callback) {
        Call<ResponseBody> result = ApiClient.create().isFriend(userId, friendId);

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

    /**------------------------------------------------------------------
     메서드 ==> 친구 추가 요청
     ------------------------------------------------------------------*/
    @Override
    public void addFriend(int _id, int friendId, ResultCallback callback) {
        Call<ResponseBody> result = ApiClient.create().addFriend(_id, friendId);

        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                switch (response.code()){
                    case SUCCESS_CREATED: callback.onSuccess(""); break;
                    case ERROR_SERVICE_UNAVAILABLE: callback.onFailure("SERVICE_UNAVAILABLE"); break;
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
