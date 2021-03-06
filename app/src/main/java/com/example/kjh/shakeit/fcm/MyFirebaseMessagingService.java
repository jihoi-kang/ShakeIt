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
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.MainActivity;
import com.example.kjh.shakeit.main.chat.ChatActivity;
import com.example.kjh.shakeit.utils.CurrencyUnitUtil;
import com.example.kjh.shakeit.utils.NotificationManager;
import com.example.kjh.shakeit.utils.Serializer;
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

        ChatRoom room = Serializer.deserialize(message.replaceAll("\'", "\""), ChatRoom.class);

        Intent intent;
        if(title.equals("메시지")){
            // 1. App이 실행중이며
            // 2. ChatActivity에 머무는 중이며
            // 3. 채팅방 번호가 같은 곳에 있을 시
            // 알림을 울리지 않는다.
            if(AppManager.getActivityStack().size() > 0
                    && AppManager.getAppManager().currentActivity().getClass() == ChatActivity.class
                    // 채팅방 번호가 일치한 경우
                    && App.getApplication().getRoomId() == room.getRoomId()) {
                return;
            }

            // 앱을 실행중이지 않을 수 있음
            // 앱은 실행중이나 다른 곳을 보고 있을 수 있음

            if(room.getChatHolder().getMessageType().equals("text"))
                message = room.getChatHolder().getMessageContent();
            else if(room.getChatHolder().getMessageType().equals("image"))
                message = "사진";
            else if(room.getChatHolder().getMessageType().equals("point"))
                message = CurrencyUnitUtil.toCurrency(room.getChatHolder().getMessageContent()) + "포인트를 보냅니다!";

            for(User user : room.getParticipants())
                if(user.getUserId() == room.getChatHolder().getUserId())
                    title = user.getName();

            intent = new Intent(this, MainActivity.class);
            intent.putExtra("user", App.getApplication().getUser());
            intent.putExtra("notifyType", "message");
            intent.putExtra("room", room);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code*/, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            /** 오레오 버전 이상 */
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                NotificationManager.sendNotification(
                        this,
                        1,
                        NotificationManager.Channel.MESSAGE,
                        title,
                        message,
                        pendingIntent
                );
            /** 오레오 버전 미만 */
            else {
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(App.getApplication(), getString(R.string.notification_channel_message_title))
                                .setSmallIcon(R.drawable.launcher_icon)
                                .setContentTitle(title)
                                .setContentText(message)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);

                android.app.NotificationManager notificationManager =
                        (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(2, notificationBuilder.build());
            }
        }
    }
}
