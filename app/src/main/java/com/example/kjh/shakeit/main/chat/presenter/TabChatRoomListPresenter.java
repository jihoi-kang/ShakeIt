package com.example.kjh.shakeit.main.chat.presenter;

import android.util.Log;

import com.example.kjh.shakeit.callback.ResultCallback;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.chat.contract.TabChatRoomListContract;
import com.example.kjh.shakeit.utils.Serializer;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class TabChatRoomListPresenter implements TabChatRoomListContract.Presenter {

    private final String TAG = TabChatRoomListPresenter.class.getSimpleName();

    private TabChatRoomListContract.View view;
    private TabChatRoomListContract.Model model;

    private ArrayList<ChatRoom> roomList;

    public TabChatRoomListPresenter(TabChatRoomListContract.View view, TabChatRoomListContract.Model model) {
        this.view = view;
        this.model = model;

        roomList = new ArrayList<>();
    }

    /**------------------------------------------------------------------
     메서드 ==> 채팅방 목록 가져오기
     ------------------------------------------------------------------*/
    @Override
    public void getChatRoomList() {
        roomList.clear();

        User user = view.getUser();

        model.getChatRoomList(user.getUserId(), new ResultCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONArray jsonArray = new JSONArray(body);
                    for(int i = 0; i < jsonArray.length(); i++){
                        Log.d("" + i, jsonArray.getJSONObject(i).toString());
                        ChatRoom chatRoom = Serializer.deserialize(jsonArray.getJSONObject(i).toString(), ChatRoom.class);

                        ChatHolder chatHolder = Serializer.deserialize(jsonArray.getJSONObject(i).getJSONObject("chatHolder").toString(), ChatHolder.class);
                        chatRoom.setChatHolder(chatHolder);

                        roomList.add(chatRoom);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setChatRoomList();
            }

            @Override
            public void onFailure(String errorMsg) {
                Log.d(TAG, "onFailure => " + errorMsg);
            }
        });
    }

    /**------------------------------------------------------------------
     메서드 ==> 가져온 채팅방 목록 셋팅
     ------------------------------------------------------------------*/
    @Override
    public void setChatRoomList() {
        view.showChatRoomList(roomList);
    }

}
