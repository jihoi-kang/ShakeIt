package com.example.kjh.shakeit.netty;

import com.example.kjh.shakeit.app.Constant;
import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.netty.protocol.ProtocolHeader;

import java.io.UnsupportedEncodingException;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    private final String TAG = NettyClient.class.getSimpleName();

    private static NettyClient instance = new NettyClient();
    private EventLoopGroup group;
    private NettyListener listener;
    private Channel channel;

    /** 현재 연결 상태 여부 */
    private boolean isConnect = false;
    private int reconnectNum = Integer.MAX_VALUE;
    private long recoonectIntervalTime = 5000;

    public static NettyClient getInstance() {
        return instance;
    }

    /**------------------------------------------------------------------
     메서드 ==> 서버와 연결
     ------------------------------------------------------------------*/
    public synchronized NettyClient connect() {
        if(!isConnect) {
            group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap().group(group)
                                        .option(ChannelOption.SO_KEEPALIVE, true)
                                        // Non-blocking
                                        .channel(NioSocketChannel.class)
                                        .handler(new NettyClientInitializer(listener));

            try {
                ChannelFuture future = bootstrap.connect(Constant.SOCKET_HOST, Constant.SOCKET_PORT).sync();
                if(future != null && future.isSuccess()){
                    channel = future.channel();
                    isConnect = true;
                } else {
                    isConnect = false;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                listener.connectStatusChanged(NettyListener.STATUS_CONNECT_ERROR);
                reconnect();
            }
        }

        return this;
    }

    /**------------------------------------------------------------------
     메서드 ==> 서버와 연결을 끊음
     ------------------------------------------------------------------*/
    public void disconnect() {
        group.shutdownGracefully();
    }

    /**------------------------------------------------------------------
     메서드 ==> 서버와 재 연결
     ------------------------------------------------------------------*/
    public void reconnect() {
        if(reconnectNum > 0 && !isConnect) {
            reconnectNum--;
            try {
                Thread.sleep(recoonectIntervalTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            disconnect();
            connect();
        } else {
            disconnect();
        }
    }

    /**------------------------------------------------------------------
     메서드 ==> 소켓 서버에 메시지를 보내는 기능
     ------------------------------------------------------------------*/
    public boolean sendMsgToServer(MessageHolder holder, FutureListener listener) {
        boolean flag = channel != null && isConnect;

        if(flag) {
            String body = holder.getBody();

            if(body == null)
                throw new NullPointerException("Body is null");

            byte[] bytes = new byte[0];
            try {
                bytes = body.getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            ByteBuf buf = Unpooled.buffer();

            buf.writeShort(ProtocolHeader.START)
                    .writeByte(holder.getSign())
                    .writeByte(holder.getType())
                    .writeInt(bytes.length)
                    .writeBytes(bytes);

            if(listener != null) {
                channel.writeAndFlush(buf).addListener(listener);
            } else {
                channel.writeAndFlush(buf).addListener(new FutureListener() {
                    @Override
                    public void success() {

                    }

                    @Override
                    public void error() {

                    }
                });
            }

        }

        return flag;
    }

    /** Setter */
    public void setListener(NettyListener listener) {
        this.listener = listener;
    }
    public void setConnect(boolean connect) {
        isConnect = connect;
    }
    public void setReconnectNum(int reconnectNum) {
        this.reconnectNum = reconnectNum;
    }

    /** Getter */
    public boolean isConnect() {
        return isConnect;
    }
}
