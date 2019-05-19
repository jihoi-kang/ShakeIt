package com.example.kjh.shakeit.main.chat.presenter;

import android.os.Message;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.ReadHolder;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.chat.contract.ChatContract;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.utils.Serializer;
import com.example.kjh.shakeit.utils.TimeManager;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static com.example.kjh.shakeit.main.chat.ChatActivity.chatActHandler;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.DELIVERY;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.MESSAGE;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.UPDATE_UNREAD;

public class ChatPresenter implements ChatContract.Presenter {

    private final String TAG = ChatPresenter.class.getSimpleName();

    private ChatContract.View view;
    private ChatContract.Model model;

    private ArrayList<ChatHolder> chats;

    public ChatPresenter(ChatContract.View view, ChatContract.Model model) {
        this.view = view;
        this.model = model;

        chats = new ArrayList<>();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    public void onCreate() {
        BusProvider.getInstance().register(this);

        User user = view.getUser();
        ChatRoom room = view.getChatRoom();

        /** 채팅 목록 */
        getChatList();

        /** 읽지않은 메시지 읽었다고 알림 */
        model.updateUnreadChat(user.getUserId(), room);
    }

    /**------------------------------------------------------------------
     메서드 ==> 텍스트 메시지 전송 로직
     ------------------------------------------------------------------*/
    @Override
    public void onClickSend() {
        String content = view.getInputContent();
        User user = view.getUser();
        ChatRoom room = view.getChatRoom();
        String time = TimeManager.nowTime();

        JSONArray unreadUsers = new JSONArray();
        for(int index = 0; index < room.getParticipants().size(); index++)
            unreadUsers.put(room.getParticipants().get(index).getUserId());

        room.setChatHolder(
                new ChatHolder(0, room.getRoomId(), user.getUserId(), "text", content, time, unreadUsers.toString(), true)
        );
        String body = Serializer.serialize(room);

        model.sendMessage(body);
        view.clearInputContent();
    }

    /**------------------------------------------------------------------
     메서드 ==> 채팅 목록
     ------------------------------------------------------------------*/
    private void getChatList() {
        ChatRoom room = view.getChatRoom();
        chats.clear();

        model.getChatList(room.getRoomId(), new ResultCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONArray jsonArray = new JSONArray(body);
                    for(int index = 0; index < jsonArray.length(); index++) {
                        ChatHolder holder = Serializer.deserialize(jsonArray.getString(index), ChatHolder.class);
                        chats.add(holder);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                view.showChatList(chats);
            }
            @Override
            public void onFailure(String errorMsg) {}
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
     메서드 ==> 텍스트 외 메시지를 보내려고 add 눌렀을 때
     ------------------------------------------------------------------*/
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

        if(holder.getType() == MESSAGE) {
            ChatRoom room = Serializer.deserialize(holder.getBody(), ChatRoom.class);

            if(view.getChatRoom().getRoomId() != room.getRoomId())
                return;

            if(holder.getSign() == DELIVERY)
                model.updateUnreadChat(view.getUser().getUserId(), view.getChatRoom());

            chats.add(room.getChatHolder());
        } else if(holder.getType() == UPDATE_UNREAD) {
            ReadHolder readHolder = Serializer.deserialize(holder.getBody(), ReadHolder.class);

            for(int index = 0; index < readHolder.getChatIds().size(); index++) {
                for(int chatsIdx = 0; chatsIdx < chats.size(); chatsIdx++) {
                    if(chats.get(chatsIdx).getChatId() == readHolder.getChatIds().get(index)) {
                        ChatHolder chatHolder = chats.get(chatsIdx);

                        JSONArray unreadUsers = null;
                        try {
                            unreadUsers = new JSONArray(chatHolder.getUnreadUsers());
                            for(int unreadIdx = 0; unreadIdx < unreadUsers.length(); unreadIdx++) {
                                if(unreadUsers.getInt(unreadIdx) == readHolder.getUserId())
                                    unreadUsers.remove(unreadIdx);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        chatHolder.setUnreadUsers(unreadUsers.toString());
                        chats.set(chatsIdx, chatHolder);
                    }
                }
            }
        }

        Message msg = chatActHandler.obtainMessage();
        msg.obj = chats;
        chatActHandler.sendMessage(msg);
    }
}
