package com.example.kjh.shakeit.netty.handler;

import android.util.Log;

import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.netty.NettyClient;
import com.example.kjh.shakeit.netty.NettyListener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyHandler extends ChannelInboundHandlerAdapter {

    private NettyListener listener;

    public NettyHandler(NettyListener listener) {
        this.listener = listener;
    }

    /**------------------------------------------------------------------
     메서드 ==> channelActivity()
     ------------------------------------------------------------------*/
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        NettyClient.getInstance().setConnect(true);
        listener.connectStatusChanged(NettyListener.STATUS_CONNECT_SUCCESS);
    }

    /**------------------------------------------------------------------
     메서드 ==> channelRead() --> 소켓 서버로 부터 수신 했을 때
     ------------------------------------------------------------------*/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if(msg instanceof MessageHolder) {
            if(listener != null) {
                MessageHolder holder = (MessageHolder) msg;
                listener.onMessageResponse(holder);
            }
        }
    }

    /**------------------------------------------------------------------
     메서드 ==> channelReadComplete()
     ------------------------------------------------------------------*/
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**------------------------------------------------------------------
     메서드 ==> channelInactive()
     ------------------------------------------------------------------*/
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        NettyClient.getInstance().setConnect(false);
        listener.connectStatusChanged(NettyListener.STATUS_CONNECT_CLOSED);
        NettyClient.getInstance().reconnect();
    }

    /**------------------------------------------------------------------
     메서드 ==> exceptionCaught()
     ------------------------------------------------------------------*/
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        Log.e("NettyHandler", cause.getMessage());
    }
}
