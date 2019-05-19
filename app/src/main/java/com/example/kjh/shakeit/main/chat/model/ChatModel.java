package com.example.kjh.shakeit.main.chat.model;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.ReadHolder;
import com.example.kjh.shakeit.main.chat.contract.ChatContract;
import com.example.kjh.shakeit.netty.NettyClient;
import com.example.kjh.shakeit.netty.protocol.ProtocolHeader;
import com.example.kjh.shakeit.utils.Serializer;

import org.json.JSONArray;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class ChatModel implements ChatContract.Model {

    private final String TAG = ChatModel.class.getSimpleName();

    /**------------------------------------------------------------------
     메서드 ==> 소켓서버로 메시지 전송
     ------------------------------------------------------------------*/
    @Override
    public void sendMessage(String body) {
        MessageHolder holder = new MessageHolder();
        holder.setSign(ProtocolHeader.REQUEST);
        holder.setType(ProtocolHeader.MESSAGE);
        holder.setBody(body);
        NettyClient.getInstance().sendMsgToServer(holder, null);
    }

    /**------------------------------------------------------------------
     메서드 ==> 채팅 목록 데이터 가져오기
     ------------------------------------------------------------------*/
    @Override
    public void getChatList(int roomId, ResultCallback callback) {
        Realm realm = Realm.getDefaultInstance();

        RealmResults<ChatHolder> result = realm.where(ChatHolder.class)
                .equalTo("roomId", roomId)
                .findAll();

        ArrayList<ChatHolder> holders = new ArrayList<>();
        holders.addAll(realm.copyFromRealm(result));

        JSONArray jsonArray = new JSONArray();
        for(int index = 0; index < holders.size(); index++)
            jsonArray.put(Serializer.serialize(holders.get(index)));

        realm.close();
        callback.onSuccess(jsonArray.toString());
    }

    /**------------------------------------------------------------------
     메서드 ==> 읽지 않은 채팅을 읽었음을 서버에 알려준다
     ------------------------------------------------------------------*/
    @Override
    public void updateUnreadChat(int userId, ChatRoom room) {
        Realm realm = Realm.getDefaultInstance();

        RealmResults<ChatHolder> result = realm.where(ChatHolder.class)
                .equalTo("roomId", room.getRoomId())
                .equalTo("isRead", false)
                .findAll();

        /** 업데이트할 읽지않은 메시지가 없을 때 */
        if(result.size() == 0) {
            realm.close();
            return;
        }

        /** 읽은 Chat Id들 */
        ArrayList<Integer> chatIds = new ArrayList<>();
        for(int index = 0; index < result.size(); index++)
            chatIds.add(result.get(index).getChatId());

        /** 채팅방 참여자 */
        ArrayList<Integer> participants = new ArrayList<>();
        for(int index = 0; index < room.getParticipants().size(); index++)
            participants.add(room.getParticipants().get(index).getUserId());

        /** MessageHolder Body */
        String body = Serializer.serialize(new ReadHolder(userId, room.getRoomId(), participants, chatIds));

        realm.close();

        MessageHolder holder = new MessageHolder();
        holder.setSign(ProtocolHeader.REQUEST);
        holder.setType(ProtocolHeader.UPDATE_UNREAD);
        holder.setBody(body);
        NettyClient.getInstance().sendMsgToServer(holder, null);
    }

}
