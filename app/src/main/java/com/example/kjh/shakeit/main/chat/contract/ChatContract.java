package com.example.kjh.shakeit.main.chat.contract;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;

import java.util.ArrayList;

public interface ChatContract {

    interface View {

        String getInputContent();
        User getUser();
        ChatRoom getChatRoom();
        void clearInputContent();
        void showSelectType();
        void showMessageForFailure();
        void showChatList(ArrayList<ChatHolder> holders);
        void setChatRoomId(int roomId);
        void goCallWaitActivity(String roomID);

    }

    interface Presenter {

        void onClickSend();
        void onCreate();
        void onDestroy();
        void onClickAttach();

    }

    interface Model {

        void sendMessage(String body);
        void getChatList(int roomId, ResultCallback callback);
        void updateUnreadChat(int userId, ChatRoom room);

    }

}
