package com.example.kjh.shakeit.netty.protocol;

import com.example.kjh.shakeit.data.MessageHolder;

import java.io.UnsupportedEncodingException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 프로토콜 헤더 인코딩 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 3. PM 8:17
 **/
public class ProtocolEncoder extends MessageToByteEncoder<MessageHolder> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageHolder holder, ByteBuf out) throws UnsupportedEncodingException {
        String body = holder.getBody();

        if(body == null)
            throw new NullPointerException("Body is null");

        byte[] bytes = body.getBytes("utf-8");

        /** 프로토콜 헤더 생성 */
        out.writeShort(ProtocolHeader.START)
                .writeByte(holder.getSign())
                .writeByte(holder.getType())
                .writeInt(bytes.length)
                .writeBytes(bytes);
    }
}
