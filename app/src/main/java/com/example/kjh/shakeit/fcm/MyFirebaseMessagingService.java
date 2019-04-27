package com.example.kjh.shakeit.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseInstanceIDService extends FirebaseMessagingService {

    /**------------------------------------------------------------------
     메서드 ==> 새로운 토큰 발급, 이 토큰을 통해 디바이스에 대한 고유값으로 푸쉬 보냄
     ------------------------------------------------------------------*/
    @Override
    public void onNewToken(String s) {
        Log.e("FirebaseMessaging", s);
    }

    /**------------------------------------------------------------------
     메서드 ==> 메시지를 받았을 때 발생하는 이벤트
     ------------------------------------------------------------------*/
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage != null && remoteMessage.getData().size() > 0) {
            sendNotification(remoteMessage);
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");

    }
}
