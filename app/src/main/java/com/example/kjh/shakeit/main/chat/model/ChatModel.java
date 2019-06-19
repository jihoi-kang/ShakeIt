package com.example.kjh.shakeit.main.chat.model;

import com.example.kjh.shakeit.api.ApiClient;
import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.ReadHolder;
import com.example.kjh.shakeit.main.chat.contract.ChatContract;
import com.example.kjh.shakeit.netty.NettyClient;
import com.example.kjh.shakeit.netty.protocol.ProtocolHeader;
import com.example.kjh.shakeit.utils.Serializer;
import com.example.kjh.shakeit.utils.TimeManager;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.kjh.shakeit.app.Constant.ERROR_BAD_REQUEST;
import static com.example.kjh.shakeit.app.Constant.ERROR_SERVICE_UNAVAILABLE;
import static com.example.kjh.shakeit.app.Constant.SUCCESS_CREATED;
import static com.example.kjh.shakeit.app.Constant.SUCCESS_OK;

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
     메서드 ==> 소켓서버로 이미지 전송
     ------------------------------------------------------------------*/
    @Override
    public void sendImage(String body) {
        MessageHolder holder = new MessageHolder();
        holder.setSign(ProtocolHeader.REQUEST);
        holder.setType(ProtocolHeader.IMAGE);
        holder.setBody(body);
        NettyClient.getInstance().sendMsgToServer(holder, null);
    }

    /**------------------------------------------------------------------
     메서드 ==> 사용자 정보
     ------------------------------------------------------------------*/
    @Override
    public void getUser(int userId, ResultCallback callback) {
        Call<ResponseBody> result = ApiClient.create().getUserById(userId);

        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    switch (response.code()){
                        case SUCCESS_OK:
                            callback.onSuccess(response.body().string());
                            break;
                        case ERROR_SERVICE_UNAVAILABLE:
                            callback.onFailure("SERVICE_UNAVAILABLE");
                            break;
                        case ERROR_BAD_REQUEST:
                            callback.onFailure("SERVER_ERROR");
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
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

    /**------------------------------------------------------------------
     메서드 ==> 채팅방 세션 변경(To Netty Server)
     ------------------------------------------------------------------*/
    @Override
    public void updateChatroomSession(String body, byte type) {
        MessageHolder holder = new MessageHolder();
        holder.setSign(ProtocolHeader.REQUEST);
        holder.setType(type);
        holder.setBody(body);
        NettyClient.getInstance().sendMsgToServer(holder, null);
    }

    /**------------------------------------------------------------------
     메서드 ==> 이미지 업로드 요청
     ------------------------------------------------------------------*/
    @Override
    public void uploadImage(int _id, String path, ResultCallback callback) {
        File file = new File(path);

        /** 확장자 분류 */
        int Idx = file.getName().lastIndexOf(".");
        String format = file.getName().substring(Idx+1);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/" + format), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", _id + "@" + TimeManager.now() + "." + format, requestFile);

        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), String.valueOf("profile"));

        Call<ResponseBody> result = ApiClient.create().uploadImage(body, type);

        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    switch (response.code()){
                        case SUCCESS_CREATED:
                            /** 업로드한 이미지 URL 반환 */
                            callback.onSuccess(response.body().string());
                            break;
                        case ERROR_SERVICE_UNAVAILABLE: callback.onFailure("SERVICE_UNAVAILABLE"); break;
                        case ERROR_BAD_REQUEST: callback.onFailure("SERVER_ERROR"); break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure("" + t.getMessage());
            }
        });
    }

}
