package com.example.kjh.shakeit.fcm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.app.App;
import com.example.kjh.shakeit.app.AppManager;
import com.example.kjh.shakeit.login.MainActivity;
import com.example.kjh.shakeit.utils.NotificationManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    /**------------------------------------------------------------------
     메서드 ==> 새로운 토큰 발급, 이 토큰을 통해 디바이스에 대한 고유값으로 푸쉬 보냄
     ------------------------------------------------------------------*/
    @Override
    public void onNewToken(String s) {
        Log.d("FirebaseMessaging", "Refreshed => " + s);
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

        /** 오레오 버전 이상 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent;
            if (title.equals("메시지")) {
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("notifyType", "message");

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code*/, intent,
                        PendingIntent.FLAG_ONE_SHOT);

                NotificationManager.sendNotification(this, 1, NotificationManager.Channel.MESSAGE, title, message, pendingIntent);
            } else if (title.equals("알림")) {
                // 카카오페이 송금 알림
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("notifyType", "notice");

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code*/, intent,
                        PendingIntent.FLAG_ONE_SHOT);

                // 액티비티 스택이 0보다 클 때 들어와 있음 => Intent를 보내지 않음
                if(AppManager.getActivityStack().size() > 0)
                    pendingIntent = null;

                NotificationManager.sendNotification(this, 2, NotificationManager.Channel.NOTICE, title, message, pendingIntent);
            }
        } else {
            Intent intent = new Intent(this, MainActivity.class);

            if(title.equals("메시지"))
                intent.putExtra("notifyType", "message");
            else if(title.equals("알림"))
                intent.putExtra("notifyType", "notice");

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code*/, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            // 액티비티 스택이 0보다 클 때 들어와 있음 => Intent를 보내지 않음
            if(AppManager.getActivityStack().size() > 0)
                pendingIntent = null;

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(App.getApplication(), getString(R.string.notification_channel_notice_title))
                            .setSmallIcon(R.drawable.launcher_icon)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

            android.app.NotificationManager notificationManager =
                    (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(3, notificationBuilder.build());
        }
    }
}
