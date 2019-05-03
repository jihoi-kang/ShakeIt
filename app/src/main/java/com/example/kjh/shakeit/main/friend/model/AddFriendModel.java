package com.example.kjh.shakeit.main.friend.model;

import com.example.kjh.shakeit.api.ApiClient;
import com.example.kjh.shakeit.callback.ResultCallback;
import com.example.kjh.shakeit.main.friend.contract.AddFriendContract;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.kjh.shakeit.Statics.ERROR_BAD_REQUEST;
import static com.example.kjh.shakeit.Statics.ERROR_NOT_FOUND;
import static com.example.kjh.shakeit.Statics.ERROR_SERVICE_UNAVAILABLE;
import static com.example.kjh.shakeit.Statics.SUCCESS_CREATED;
import static com.example.kjh.shakeit.Statics.SUCCESS_OK;

public class AddFriendModel implements AddFriendContract.Model {

    /**------------------------------------------------------------------
     메서드 ==> 이메일을 통해 유저 정보 얻어오기(고유 번호, 프로필 이미지, 이름, 친구추가 상태)
     ------------------------------------------------------------------*/
    @Override
    public void getUserByEmail(int _id, String email, ResultCallback callback) {

        Call<ResponseBody> result = ApiClient.create().getUserByEmail(_id, email);

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
                    case ERROR_NOT_FOUND:
                        callback.onSuccess(null);
                        break;
                    case ERROR_SERVICE_UNAVAILABLE:
                        callback.onFailure("SERVICE_UNAVAILABLE");
                        break;
                    case ERROR_BAD_REQUEST:
                        callback.onFailure("SERVER_ERROR");
                        break;
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
                    case SUCCESS_CREATED:
                        callback.onSuccess("");
                        break;
                    case ERROR_SERVICE_UNAVAILABLE:
                        callback.onFailure("SERVICE_UNAVAILABLE");
                        break;
                    case ERROR_BAD_REQUEST:
                        callback.onFailure("SERVER_ERROR");
                        break;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }
}
