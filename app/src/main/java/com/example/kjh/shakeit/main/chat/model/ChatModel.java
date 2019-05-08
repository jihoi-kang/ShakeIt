package com.example.kjh.shakeit.main.chat.model;

import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.main.chat.contract.ChatContract;
import com.example.kjh.shakeit.netty.FutureListener;
import com.example.kjh.shakeit.netty.NettyClient;

public class ChatModel implements ChatContract.Model {

    @Override
    public void sendMessage(MessageHolder holder, FutureListener listener) {
        NettyClient.getInstance().sendMsgToServer(holder, listener);
    }
}
