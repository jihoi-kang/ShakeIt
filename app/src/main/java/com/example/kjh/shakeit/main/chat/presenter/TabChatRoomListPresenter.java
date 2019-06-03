package com.example.kjh.shakeit.main.chat.presenter;

import android.os.Message;
import android.util.Log;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.ReadHolder;
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
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.CALLBACK;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.DELIVERY;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.MESSAGE;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.UPDATE_UNREAD;

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
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
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
                    for(int index = 0; index < jsonArray.length(); index++){
                        ChatRoom chatRoom = Serializer.deserialize(jsonArray.getJSONObject(index).toString(), ChatRoom.class);
                        ChatHolder chatHolder = Serializer.deserialize(jsonArray.getJSONObject(index).getJSONObject("chatHolder").toString(), ChatHolder.class);
                        chatRoom.setUnreadCount(model.getUnreadCount(chatRoom.getRoomId()));
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
     구독이벤트 ==> Netty에서 이벤트 왔을 때 ==> 메시지 받거나 콜백
     ------------------------------------------------------------------*/
    @Subscribe
    public void nettyEvent (Events.nettyEvent event) {
        MessageHolder holder = event.getMessageHolder();

        if(holder.getType() == MESSAGE) {
            int cnt = 0;
            ChatRoom chatRoom = Serializer.deserialize(holder.getBody(), ChatRoom.class);
            /** RoomId를 통해 변경된 방을 찾아 변경 */
            for (int index = 0; index < rooms.size(); index++) {
                if (rooms.get(index).getRoomId() == chatRoom.getRoomId()) {
                    ChatRoom room = rooms.get(index);
                    room.setChatHolder(chatRoom.getChatHolder());
                    room.setUnreadCount(model.getUnreadCount(chatRoom.getRoomId()));
                    rooms.remove(index);
                    rooms.add(0, room);
                } else
                    cnt++;
            }

            /** 채팅방 처음 만들어서 첫 메시지를 보냈을 때 */
            if(cnt == rooms.size()) {
                /** refresh */
                getChatRoomList();
            }

        } else if (holder.getType() == UPDATE_UNREAD) {
            Log.d(TAG, "NettyEvnet!!!!! In UPDATE_UNREAD");
            ReadHolder readHolder = Serializer.deserialize(holder.getBody(), ReadHolder.class);
            /** 채팅방 목록에서 UnreadCount 컨트롤 */
            for(int index = 0; index < rooms.size(); index++) {
                if(rooms.get(index).getRoomId() == readHolder.getRoomId()) {
                    ChatRoom room = rooms.get(index);

                    Log.d(TAG, "unread => " + room.getUnreadCount());
                    Log.d(TAG, "size => " + readHolder.getChatIds().size());

                    room.setUnreadCount(room.getUnreadCount() - readHolder.getChatIds().size());
                    if(holder.getSign() == CALLBACK) {
                        rooms.remove(index);
                        rooms.add(0, room);
                    } else if(holder.getSign() == DELIVERY)
                        rooms.set(index, room);

                }
            }
        }

        Message msg = chatRoomFragHandler.obtainMessage();
        msg.obj = rooms;
        chatRoomFragHandler.sendMessage(msg);
    }
}
