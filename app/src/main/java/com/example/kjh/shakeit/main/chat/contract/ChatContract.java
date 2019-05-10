package com.example.kjh.shakeit.main.chat.contract;

import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.netty.FutureListener;

public interface ChatContract {

    interface View {

        String getInputContent();
        User getUser();
        ChatRoom getChatRoom();
        void notifyChat(String body);
        void clearInputContent();
        void showSelectType();
        void showMessageForFailure();

    }

    interface Presenter {

        void onClickSend();
        void onCreate();
        void onDestroy();
        void onClickAttach();

    }

    interface Model {

        void sendMessage(String body, FutureListener listener);

    }

}
