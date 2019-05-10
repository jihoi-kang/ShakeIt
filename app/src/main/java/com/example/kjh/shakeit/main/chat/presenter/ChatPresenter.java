package com.example.kjh.shakeit.main.chat.presenter;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.chat.contract.ChatContract;
import com.example.kjh.shakeit.netty.FutureListener;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.utils.Serializer;
import com.example.kjh.shakeit.utils.TimeManager;
import com.squareup.otto.Subscribe;

import static com.example.kjh.shakeit.main.chat.ChatActivity.chatActHandler;

public class ChatPresenter implements ChatContract.Presenter {

    private final String TAG = ChatPresenter.class.getSimpleName();

    private ChatContract.View view;
    private ChatContract.Model model;

    public ChatPresenter(ChatContract.View view, ChatContract.Model model) {
        this.view = view;
        this.model = model;
    }

    /**------------------------------------------------------------------
     메서드 ==> 메시지 전송 로직
     ------------------------------------------------------------------*/
    @Override
    public void onClickSend() {
        String content = view.getInputContent();
        User user = view.getUser();
        ChatRoom room = view.getChatRoom();
        String time = TimeManager.nowTime();

        String body =
                Serializer.serialize(new ChatHolder(0, room.getRoomId(), user.getUserId(), "text", content, time));

        model.sendMessage(body, new FutureListener() {
            @Override
            public void success() {
                view.clearInputContent();
                Log.d(TAG, "success => send message");
            }

            @Override
            public void error() {
                view.showMessageForFailure();
                Log.d(TAG, "error => send message");
            }
        });

    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    public void onCreate() {
        BusProvider.getInstance().register(this);
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onClickAttach() {
        view.showSelectType();
    }

    /**------------------------------------------------------------------
     구독이벤트 ==> Netty에서 이벤트 왔을 때 ==> 메시지 받거나 콜백
     ------------------------------------------------------------------*/
    @Subscribe
    public void nettyEvent (Events.nettyEvent event) {
        MessageHolder holder = event.getMessageHolder();
        ChatHolder chatHolder = Serializer.deserialize(holder.getBody(), ChatHolder.class);

        if(view.getChatRoom().getRoomId() != chatHolder.getRoomId()){
            return;
        }

        Message msg = chatActHandler.obtainMessage();

        Bundle bundle = new Bundle();
        bundle.putString("result", Serializer.serialize(holder));

        msg.setData(bundle);

        chatActHandler.sendMessage(msg);
    }
}
