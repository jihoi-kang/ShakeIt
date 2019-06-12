package com.example.kjh.shakeit.main.chat.contract;

import android.content.Context;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;

import java.util.ArrayList;

public interface TabChatRoomListContract {

    interface View {

        User getUser();
        void showChatRoomList(ArrayList<ChatRoom> roomList);
        void setUser(User user);
        Context getContext();

    }

    interface Presenter {

        void onCreate();
        void onDestroy();

    }

    interface Model {

        void getChatRoomList(int _id, ResultCallback callback);
        int getUnreadCount(int roomId);

    }

}
