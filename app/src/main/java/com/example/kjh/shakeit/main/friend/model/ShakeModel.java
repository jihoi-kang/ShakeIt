package com.example.kjh.shakeit.main.friend.model;

import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.friend.contract.ShakeContract;
import com.example.kjh.shakeit.netty.NettyClient;
import com.example.kjh.shakeit.utils.Serializer;

import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.REQUEST;

public class ShakeModel implements ShakeContract.Model {

    /**------------------------------------------------------------------
     메서드 ==> Netty Server에 흔들었다고 알림
     ------------------------------------------------------------------*/
    @Override
    public void sendMessageToServer(User user, byte type) {
        MessageHolder holder = new MessageHolder();
        holder.setSign(REQUEST);
        holder.setType(type);
        holder.setBody(Serializer.serialize(user));

        NettyClient.getInstance().sendMsgToServer(holder, null);
    }
}
