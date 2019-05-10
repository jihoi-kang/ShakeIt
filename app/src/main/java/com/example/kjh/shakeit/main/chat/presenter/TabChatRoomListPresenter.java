package com.example.kjh.shakeit.main.chat.presenter;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.example.kjh.shakeit.callback.ResultCallback;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.chat.contract.TabChatRoomListContract;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.utils.Serializer;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static com.example.kjh.shakeit.main.chat.TabChatRoomListFragment.chatRoomFragHandler;

public class TabChatRoomListPresenter implements TabChatRoomListContract.Presenter {

    private final String TAG = TabChatRoomListPresenter.class.getSimpleName();

    private TabChatRoomListContract.View view;
    private TabChatRoomListContract.Model model;

    private ArrayList<ChatRoom> rooms;

    public TabChatRoomListPresenter(TabChatRoomListContract.View view, TabChatRoomListContract.Model model) {
        this.view = view;
        this.model = model;

        rooms = new ArrayList<>();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    public void onCreate() {
        BusProvider.getInstance().register(this);

        getChatRoomList();
    }

    /**------------------------------------------------------------------
     메서드 ==> 채팅방 목록 가져오기
     ------------------------------------------------------------------*/
    private void getChatRoomList() {
        rooms.clear();

        User user = view.getUser();

        model.getChatRoomList(user.getUserId(), new ResultCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONArray jsonArray = new JSONArray(body);
                    for(int i = 0; i < jsonArray.length(); i++){
                        ChatRoom chatRoom = Serializer.deserialize(jsonArray.getJSONObject(i).toString(), ChatRoom.class);

                        ChatHolder chatHolder = Serializer.deserialize(jsonArray.getJSONObject(i).getJSONObject("chatHolder").toString(), ChatHolder.class);
                        chatRoom.setChatHolder(chatHolder);

                        rooms.add(chatRoom);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                view.showChatRoomList(rooms);
            }

            @Override
            public void onFailure(String errorMsg) {
                Log.e(TAG, "onFailure => " + errorMsg);
            }
        });
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
    }


    /**------------------------------------------------------------------
     구독이벤트 ==> Netty에서 이벤트 왔을 때 ==> 메시지 받거나 콜백
     ------------------------------------------------------------------*/
    @Subscribe
    public void nettyEvent (Events.nettyEvent event) {
        MessageHolder holder = event.getMessageHolder();

        Message msg = chatRoomFragHandler.obtainMessage();

        Bundle bundle = new Bundle();
        bundle.putString("result", Serializer.serialize(holder));

        msg.setData(bundle);

        chatRoomFragHandler.sendMessage(msg);
    }
}
