package com.example.kjh.shakeit.main.chat.presenter;

import android.util.Log;

import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.chat.contract.ChatContract;
import com.example.kjh.shakeit.netty.FutureListener;
import com.example.kjh.shakeit.netty.protocol.ProtocolHeader;

public class ChatPresenter implements ChatContract.Presenter {

    private final String TAG = ChatPresenter.class.getSimpleName();

    private ChatContract.View view;
    private ChatContract.Model model;

    public ChatPresenter(ChatContract.View view, ChatContract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void onClickSend() {
//        String content = view.getInputContent();
    }

    /**------------------------------------------------------------------
     메서드 ==> 채팅방 입장
     ------------------------------------------------------------------*/
    @Override
    public void onCreate() {
        MessageHolder holder = makeMessageHolder("enter");

        model.sendMessage(holder, new FutureListener() {
                    @Override
                    public void success() {
                        Log.d(TAG, "suceess => enter chatroom");
                    }

                    @Override
                    public void error() {
                        Log.d(TAG, "error => enter chatroom");
                    }
                }
        );
    }

    /**------------------------------------------------------------------
     메서드 ==> 채팅방 나가기
     ------------------------------------------------------------------*/
    @Override
    public void onDestroy() {
        MessageHolder holder = makeMessageHolder("out");

        model.sendMessage(holder, new FutureListener() {
                    @Override
                    public void success() {
                        Log.d(TAG, "suceess => out chatroom");
                    }

                    @Override
                    public void error() {
                        Log.d(TAG, "error => out chatroom");
                    }
                }
        );
    }

    /**------------------------------------------------------------------
     메서드 ==> 채팅방 들어가거나 나갈 때의 메시지 홀더 생성
     ------------------------------------------------------------------*/
    private MessageHolder makeMessageHolder(String type) {
        User user = view.getUser();
        ChatRoom room = view.getChatRoom();

        MessageHolder holder = new MessageHolder();
        holder.setSign(ProtocolHeader.REQUEST);
        if(type.equals("enter"))
            holder.setType(ProtocolHeader.CHATROOM_ENTER);
        else if(type.equals("out"))
            holder.setType(ProtocolHeader.CHATROOM_OUT);
        String body = "{\"userId\":" + user.getUserId() + ",\"roomId\":" + room.getRoomId() + "}";
        holder.setBody(body);

        return holder;
    }
}
