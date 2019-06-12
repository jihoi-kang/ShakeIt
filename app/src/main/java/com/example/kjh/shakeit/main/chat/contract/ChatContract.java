package com.example.kjh.shakeit.main.chat.contract;

import android.content.Context;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;

import java.io.File;
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
        Context getContext();
        void connectToRoom(String roomId, boolean commandLineRun, boolean loopback,
                           boolean useValuesFromIntent, int runTimeMs, String type);
        File getFile();
        void setUser(User user);

    }

    interface Presenter {

        void onClickSend();
        void onCreate();
        void onDestroy();
        void onClickAttach();
        void toCall(String roomID);
        void onClickGallery();
        void onClickCamera();
        void sendImage(String path);

    }

    interface Model {

        void sendMessage(String body);
        void getChatList(int roomId, ResultCallback callback);
        void updateUnreadChat(int userId, ChatRoom room);
        void sendImage(String body);
        void uploadImage(int _id, String path, ResultCallback callback);

    }

}
