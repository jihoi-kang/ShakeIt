package com.example.kjh.shakeit.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringDef;

import com.example.kjh.shakeit.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 알림 관린 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 6. 12. PM 5:47
 **/
public class NotificationManager {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createChannel(Context context) {
        /** 메시지 채널 */
        NotificationChannel channelMessage = new NotificationChannel(Channel.MESSAGE,
                context.getString(R.string.notification_channel_message_title), android.app.NotificationManager.IMPORTANCE_HIGH);
        channelMessage.setDescription(context.getString(R.string.notification_channel_message_description));
        channelMessage.enableLights(true);
        channelMessage.enableVibration(true);
        channelMessage.setShowBadge(false);
        channelMessage.setVibrationPattern(new long[]{100, 200, 100, 200});
        channelMessage.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager(context).createNotificationChannel(channelMessage);
    }

    /**------------------------------------------------------------------
     메서드 ==> NotificationManager 반환
     ------------------------------------------------------------------*/
    private static android.app.NotificationManager getManager(Context context) {
        return (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**------------------------------------------------------------------
     메서드 ==> 알림 보내기
     ------------------------------------------------------------------*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void sendNotification(Context context, int id, @Channel String channel, String title, String body, PendingIntent pendingIntent) {
        Notification.Builder builder = new Notification.Builder(context, channel)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(getSmallIcon())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        getManager(context).notify(id, builder.build());
    }

    private static int getSmallIcon() {
        return R.drawable.launcher_icon;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            Channel.MESSAGE
    })

    public @interface Channel {
        String MESSAGE = "message";
    }
}
