package com.example.kjh.shakeit.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public abstract class FutureListener implements ChannelFutureListener {

    @Override
    public void operationComplete(ChannelFuture future) {
        if(future.isSuccess()) {
            success();
        } else {
            error();
        }
    }

    public abstract void success();

    public abstract void error();
}
