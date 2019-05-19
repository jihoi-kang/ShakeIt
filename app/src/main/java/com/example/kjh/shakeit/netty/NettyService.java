package com.example.kjh.shakeit.netty;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.ReadHolder;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.netty.protocol.ProtocolHeader;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.utils.Serializer;
import com.example.kjh.shakeit.utils.ShareUtil;

import org.json.JSONArray;
import org.json.JSONException;

import io.realm.Realm;
import io.realm.internal.IOException;

import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.CONN;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.MESSAGE;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.REQUEST;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.UPDATE_UNREAD;

public class NettyService extends Service implements NettyListener {

    private final String TAG = NettyService.class.getSimpleName();

    private NetworkReceiver receiver;

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
            case MESSAGE: insertChatHolder(holder); break;
            case UPDATE_UNREAD: updateUnreadStatus(holder); break;
        }

        Events.nettyEvent event = new Events.nettyEvent(holder);
        BusProvider.getInstance().post(event);
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
