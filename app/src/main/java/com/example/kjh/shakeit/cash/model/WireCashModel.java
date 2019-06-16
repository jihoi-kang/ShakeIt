package com.example.kjh.shakeit.cash.model;

import com.example.kjh.shakeit.api.ApiClient;
import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.cash.contract.WireCashContract;
import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.netty.NettyClient;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.kjh.shakeit.app.Constant.ERROR_BAD_REQUEST;
import static com.example.kjh.shakeit.app.Constant.ERROR_SERVICE_UNAVAILABLE;
import static com.example.kjh.shakeit.app.Constant.SUCCESS_CREATED;
import static com.example.kjh.shakeit.app.Constant.SUCCESS_OK;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.REQUEST;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.WIRE;

public class WireCashModel implements WireCashContract.Model {

    /**------------------------------------------------------------------
     메서드 ==> 채팅방 정보 가져오기
     ------------------------------------------------------------------*/
    @Override
    public void getChatRoom(int userId, int friendId, ResultCallback callback) {
        Call<ResponseBody> result = ApiClient.create().getChatRoom(userId, friendId);

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

    /**------------------------------------------------------------------
     메서드 ==> 송금
     ------------------------------------------------------------------*/
    @Override
    public void wire(int userId, int friendId, int amount, ResultCallback callback) {
        Call<ResponseBody> result = ApiClient.create().wire(userId, friendId, amount);

        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    switch (response.code()){
                        case SUCCESS_CREATED:
                            callback.onSuccess(response.body().string());
                            break;
                        case ERROR_SERVICE_UNAVAILABLE: callback.onFailure("SERVICE_UNAVAILABLE"); break;
                        case ERROR_BAD_REQUEST: callback.onFailure("SERVER_ERROR"); break;
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
     메서드 ==> Netty Server에 요청(송금했음을 알리기 위해)
     ------------------------------------------------------------------*/
    @Override
    public void sendMessage(String body) {
        MessageHolder holder = new MessageHolder();
        holder.setSign(REQUEST);
        holder.setType(WIRE);
        holder.setBody(body);
        NettyClient.getInstance().sendMsgToServer(holder, null);
    }
}
