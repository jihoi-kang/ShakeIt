package com.example.kjh.shakeit.netty;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.kjh.shakeit.api.ApiClient;
import com.example.kjh.shakeit.app.App;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.ImageHolder;
import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.ReadHolder;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.fcm.FcmGenerator;
import com.example.kjh.shakeit.netty.protocol.ProtocolHeader;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.utils.ImageLoaderUtil;
import com.example.kjh.shakeit.utils.Serializer;
import com.example.kjh.shakeit.utils.ShareUtil;
import com.example.kjh.shakeit.utils.StrUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;

import io.realm.Realm;
import io.realm.internal.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.kjh.shakeit.app.Constant.SUCCESS_OK;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.CALLBACK;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.CONN;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.DELIVERY;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.IMAGE;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.MESSAGE;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.REQUEST;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.UPDATE_UNREAD;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.WIRE;

public class NettyService extends Service implements NettyListener {

    private final String TAG = NettyService.class.getSimpleName();

    private NetworkReceiver receiver;

    private Point size;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    public void onCreate() {
        super.onCreate();

        /** 로컬 브로드캐스트에 대한 수신 등록 */
        receiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    /**------------------------------------------------------------------
     생명주기 ==> onStartCommand() --> 서버와 연결
     ------------------------------------------------------------------*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        size = intent.getParcelableExtra("size");
        NettyClient.getInstance().setListener(this);
        connect();
        return START_NOT_STICKY;
    }

    /**------------------------------------------------------------------
     생명주기 ==> onBind()
     ------------------------------------------------------------------*/
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        shutdown();
    }

    /**------------------------------------------------------------------
     메서드 ==> 서버와 연결
     ------------------------------------------------------------------*/
    private void connect() {
        if(!NettyClient.getInstance().isConnect()) {
            new Thread(() -> NettyClient.getInstance().connect()).start();
        }
    }

    /**------------------------------------------------------------------
     메서드 ==> 서버와 연결 끊음
     ------------------------------------------------------------------*/
    private void shutdown() {
        NettyClient.getInstance().setReconnectNum(0);
        NettyClient.getInstance().disconnect();
    }

    /**------------------------------------------------------------------
     메서드 ==> 연결 상태 모니터링
     ------------------------------------------------------------------*/
    @Override
    public void connectStatusChanged(int statusCode) {
        switch (statusCode){
            case NettyListener.STATUS_CONNECT_SUCCESS:
                /** 셋팅할 시간 필요. 시간을 주지 않으면 셋팅이 되지 않은 상태에서 Socket을 사용할 수 있음 */
                try {
                    Thread.sleep(200);
                    authenticData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case NettyListener.STATUS_CONNECT_ERROR: Log.e(TAG, "tcp connect error"); break;
            case NettyListener.STATUS_CONNECT_CLOSED: Log.d(TAG, "tcp connect closed"); break;
        }
    }

    /**------------------------------------------------------------------
     메서드 ==> 인증 데이터 요청
     ------------------------------------------------------------------*/
    private void authenticData() {
        MessageHolder messageHolder = new MessageHolder();
        messageHolder.setSign(ProtocolHeader.REQUEST);
        messageHolder.setType(CONN);

        User user = new User();
        user.setUserId(ShareUtil.getPreferInt("userId"));

        messageHolder.setBody(Serializer.serialize(user));
        NettyClient.getInstance().sendMsgToServer(messageHolder, null);
    }

    /**------------------------------------------------------------------
     메서드 ==> 메시지 처리
     ------------------------------------------------------------------*/
    @Override
    public void onMessageResponse(MessageHolder holder) {
        Log.d(TAG, "MessageHolder Body => " + holder.getBody());

        if(holder.getSign() == REQUEST)
            return;

        switch (holder.getType()) {
            case MESSAGE:
                insertChatHolder(holder);
                if(holder.getSign() == CALLBACK) postRequest(holder);
                break;
            case UPDATE_UNREAD: updateUnreadStatus(holder); break;
            case IMAGE:
                insertChatHolderAndImageHolder(holder);
                if(holder.getSign() == CALLBACK) postRequest(holder);
                break;
            case WIRE:
                insertChatHolder(holder);
                if(holder.getSign() == DELIVERY) updateUserCash(holder);
                if(holder.getSign() == CALLBACK) postRequest(holder);
                break;
        }

        Events.nettyEvent event = new Events.nettyEvent(holder);
        BusProvider.getInstance().post(event);
    }

    /**------------------------------------------------------------------
     메서드 ==> FCM 메시지 보내기
     ------------------------------------------------------------------*/
    private void postRequest(MessageHolder holder) {
        User user = App.getApplication().getUser();
        ChatRoom newRoom = null;
        try {
            newRoom = Serializer.deserialize(holder.getBody(), ChatRoom.class).copy();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        for(User targetUser : newRoom.getParticipants()) {
            ChatRoom finalNewRoom = null;
            try {
                finalNewRoom = newRoom.copy();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            Call<ResponseBody> result = ApiClient.create().getUserById(targetUser.getUserId());

            ChatRoom finalNewRoom1 = finalNewRoom;
            result.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if(response.code() == SUCCESS_OK) {
                            User otherUser = Serializer.deserialize(response.body().string(), User.class);
                            // 서버의 Token 상태 확인
                            if (StrUtil.isBlank(otherUser.getDeviceToken()))
                                return;

                            // 참가자들 변경
                            for(int idx = 0; idx < finalNewRoom1.getParticipants().size(); idx++) {
                                if(finalNewRoom1.getParticipants().get(idx).getUserId() == targetUser.getUserId()) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(finalNewRoom1.getChatHolder().getUnreadUsers());
                                        for(int index = 0; index < jsonArray.length(); index++){
                                            if(jsonArray.getInt(index) == targetUser.getUserId()) {
                                                jsonArray.remove(index);
                                                jsonArray.put(user.getUserId());
                                            }
                                        }

                                        finalNewRoom1.getChatHolder().setUnreadUsers(jsonArray.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    finalNewRoom1.getParticipants().set(idx, user);
                                }
                            }

                            // FCM 전송
                            FcmGenerator.postRequest(otherUser.getDeviceToken(), "메시지", Serializer.serialize(finalNewRoom1).replaceAll("\"", "\'"));
                        }
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }

    /**------------------------------------------------------------------
     메서드 ==> 사용자 포인트 변경사항 저장
     ------------------------------------------------------------------*/
    private void updateUserCash(MessageHolder holder) {
        ChatRoom room = Serializer.deserialize(holder.getBody(), ChatRoom.class);
        if(room.getParticipants().get(0).getUserId() == ShareUtil.getPreferInt("userId")) {
            ShareUtil.setPreferInt("cash", ShareUtil.getPreferInt("cash") + Integer.parseInt(room.getChatHolder().getMessageContent()));

            Events.updateProfileEvent event = new Events.updateProfileEvent(App.getApplication().getUser());
            BusProvider.getInstance().post(event);
        }
    }

    /**------------------------------------------------------------------
     메서드 ==> 읽지않은 상태 읽음으로 변경 Realm에 저장
     ------------------------------------------------------------------*/
    private void updateUnreadStatus(MessageHolder holder) {
        ReadHolder readHolder = Serializer.deserialize(holder.getBody(), ReadHolder.class);
        Realm realm = Realm.getDefaultInstance();

        try {
            realm.beginTransaction();
            for(int index = 0; index < readHolder.getChatIds().size(); index++) {
                ChatHolder chatHolder = realm.where(ChatHolder.class).equalTo("chatId", readHolder.getChatIds().get(index)).findFirst();

                JSONArray unreadUsers = new JSONArray(chatHolder.getUnreadUsers());
                for(int unreadIdx = 0; unreadIdx < unreadUsers.length(); unreadIdx++) {
                    if(unreadUsers.getInt(unreadIdx) == readHolder.getUserId())
                        unreadUsers.remove(unreadIdx);
                }
                chatHolder.setUnreadUsers(unreadUsers.toString());
                if(readHolder.getUserId() == ShareUtil.getPreferInt("userId"))
                    chatHolder.setRead(true);
            }

            realm.commitTransaction();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }
    }

    /**------------------------------------------------------------------
     메서드 ==> 채팅 받으면 Realm에 저장(DELIVERY, CALLBACK 둘다)
     ------------------------------------------------------------------*/
    private void insertChatHolder(MessageHolder holder) {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.beginTransaction();
            ChatRoom room = Serializer.deserialize(holder.getBody(), ChatRoom.class);
            ChatHolder chatHolder = room.getChatHolder();
            realm.copyToRealm(chatHolder);
            realm.commitTransaction();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }
    }

    /**------------------------------------------------------------------
     메서드 ==> 채팅 이미지 받으면 Realm에 저장 및 이미지 캐시
     ------------------------------------------------------------------*/
    private void insertChatHolderAndImageHolder(MessageHolder holder) {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.beginTransaction();
            ChatRoom room = Serializer.deserialize(holder.getBody(), ChatRoom.class);
            ChatHolder chatHolder = room.getChatHolder();

            if(chatHolder.getMessageType().equals("image")){
                new Thread(() -> {
                    Realm rm = Realm.getDefaultInstance();
                    rm.beginTransaction();
                    Bitmap bitmap = ImageLoaderUtil.getBitmap(chatHolder.getMessageContent(), size);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] imageByteArray = stream.toByteArray();

                    rm.copyToRealm(new ImageHolder(chatHolder.getMessageContent(), imageByteArray));
                    rm.commitTransaction();

                    rm.close();
                }).start();
            }

            realm.copyToRealm(chatHolder);
            realm.commitTransaction();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }
    }

    /**------------------------------------------------------------------
     클래스 ==> 네트워크 상태에 따라 서버와 연결
     ------------------------------------------------------------------*/
    public class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            // connected to the internet
            if (activeNetwork != null) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI
                        || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    connect();
                }
            }
        }
    }

}
