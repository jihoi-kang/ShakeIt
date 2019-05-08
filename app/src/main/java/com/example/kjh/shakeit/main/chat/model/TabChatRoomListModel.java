package com.example.kjh.shakeit.main.chat.model;

import com.example.kjh.shakeit.api.ApiClient;
import com.example.kjh.shakeit.callback.ResultCallback;
import com.example.kjh.shakeit.main.chat.contract.TabChatRoomListContract;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.kjh.shakeit.Statics.ERROR_BAD_REQUEST;
import static com.example.kjh.shakeit.Statics.ERROR_SERVICE_UNAVAILABLE;
import static com.example.kjh.shakeit.Statics.SUCCESS_OK;

public class TabChatRoomListModel implements TabChatRoomListContract.Model {

    /**------------------------------------------------------------------
     메서드 ==> 채팅방 목록 데이터 가져오기
     ------------------------------------------------------------------*/
    @Override
    public void getChatRoomList(int _id, ResultCallback callback) {
        Call<ResponseBody> result = ApiClient.create().getChatRoomList(_id);

        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                switch (response.code()) {
                    case SUCCESS_OK:
                        try {
                            callback.onSuccess(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
                callback.onFailure("" + t.getMessage());
            }
        });
    }
}
