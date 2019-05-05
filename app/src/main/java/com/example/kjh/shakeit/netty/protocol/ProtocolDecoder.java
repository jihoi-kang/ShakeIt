package com.example.kjh.shakeit.netty.protocol;

import com.example.kjh.shakeit.data.MessageHolder;

import java.io.UnsupportedEncodingException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 프로토콜 헤더 디코딩 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 3. PM 8:20
 **/
public class ProtocolDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws UnsupportedEncodingException {

        /** 패킷 길이가 헤더보다 짧을 경우 */
        if(in.readableBytes() < ProtocolHeader.HEADER_LENGTH) {
            return;
        }

        /** 버퍼의 현재 readerIndex를 마킹한다
            resetReaderIndex를 호출하면 마킹한 readerIndex로 위치 변경이 가능 */
        in.markReaderIndex();

        /** 스타트 확인 값 일치하지 않을 경우 */
        if(in.readShort() != ProtocolHeader.START) {
            in.resetReaderIndex();
            return;
        }

        byte sign = in.readByte();
        byte type = in.readByte();

        int bodyLength = in.readInt();

        /** 프로토콜 헤더에 적혀있는 body 길이 값 확인해서 다를 경우 */
        if(in.readableBytes() != bodyLength) {
            in.resetReaderIndex();
            return;
        }

        byte[] bytes = new byte[bodyLength];
        in.readBytes(bytes);

        MessageHolder holder = new MessageHolder();
        holder.setSign(sign);
        holder.setType(type);
        holder.setBody(new String(bytes, "utf-8"));

        out.add(holder);
    }

}
