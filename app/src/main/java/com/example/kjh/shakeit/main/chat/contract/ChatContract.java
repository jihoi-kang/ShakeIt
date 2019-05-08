package com.example.kjh.shakeit.main.chat.contract;

import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.netty.FutureListener;

public interface ChatContract {

    interface View {

        String getInputContent();
        User getUser();
        ChatRoom getChatRoom();

    }

    interface Presenter {

        void onClickSend();
        void onCreate();
        void onDestroy();

    }

    interface Model {

        void sendMessage(MessageHolder holder, FutureListener listener);

    }

}
