package com.example.kjh.shakeit.main.chat.model;

import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.main.chat.contract.ChatContract;
import com.example.kjh.shakeit.netty.FutureListener;
import com.example.kjh.shakeit.netty.NettyClient;
import com.example.kjh.shakeit.netty.protocol.ProtocolHeader;

public class ChatModel implements ChatContract.Model {

    /**------------------------------------------------------------------
     메서드 ==> 소켓서버로 메시지 전송
     ------------------------------------------------------------------*/
    @Override
    public void sendMessage(String body, FutureListener listener) {
        MessageHolder holder = new MessageHolder();
        holder.setSign(ProtocolHeader.REQUEST);
        holder.setType(ProtocolHeader.MESSAGE);
        holder.setBody(body);
        NettyClient.getInstance().sendMsgToServer(holder, listener);
    }
}
