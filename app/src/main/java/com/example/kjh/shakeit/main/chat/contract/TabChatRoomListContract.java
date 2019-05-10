package com.example.kjh.shakeit.main.chat.contract;

import com.example.kjh.shakeit.callback.ResultCallback;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;

import java.util.ArrayList;

public interface TabChatRoomListContract {

    interface View {

        User getUser();
        void showChatRoomList(ArrayList<ChatRoom> roomList);
        void notifyChatRoomList(String body);

    }

    interface Presenter {

        void onCreate();
        void onDestroy();

    }

    interface Model {

        void getChatRoomList(int _id, ResultCallback callback);

    }

}
