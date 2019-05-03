package com.example.kjh.shakeit.netty;

import com.example.kjh.shakeit.data.MessageHolder;

public interface NettyListener {

    int STATUS_CONNECT_ERROR = 0;
    int STATUS_CONNECT_SUCCESS = 1;
    int STATUS_CONNECT_CLOSED = 2;

    /** 소켓 연결 상태 변경 이벤트 */
    void connectStatusChanged(int statusCode);

    /** 소켓 메시지 받았을 때 이벤트 */
    void onMessageResponse(MessageHolder holder);
}
