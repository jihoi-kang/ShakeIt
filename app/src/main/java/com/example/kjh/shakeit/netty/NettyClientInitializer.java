package com.example.kjh.shakeit.netty;

import com.example.kjh.shakeit.netty.handler.NettyHandler;
import com.example.kjh.shakeit.netty.protocol.ProtocolDecoder;
import com.example.kjh.shakeit.netty.protocol.ProtocolEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    private NettyListener listener;

    public NettyClientInitializer(NettyListener listener) {
        this.listener = listener;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        /** In / OUT bound => 데이터 입출력 순서 */
        pipeline.addLast("IdleStateHandler", new IdleStateHandler(6,0, 0));
        pipeline.addLast("ProtocolDecoder", new ProtocolDecoder());
        pipeline.addLast("ProtocolEncoder", new ProtocolEncoder());
        pipeline.addLast("NettyHandler", new NettyHandler(listener));
    }
}
