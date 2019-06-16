package com.example.kjh.shakeit.app;

import android.app.Application;
import android.graphics.Point;
import android.os.Build;

import com.example.kjh.shakeit.utils.NotificationManager;
import com.example.kjh.shakeit.utils.ShareUtil;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {

    private static App application;

    private Point size;
    private static int roomId = 0;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    public void onCreate() {
        super.onCreate();

        /** SharedPreferences 초기화 */
        ShareUtil.init(this);
        application = this;

        /** Realm 초기화 */
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("shake.realm")
                .build();
        Realm.setDefaultConfiguration(config);

        /** Notification 채널 만들기 */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            NotificationManager.createChannel(this);


    }

    public static App getApplication() {
        return application;
    }

    /** 디스플레이 => 해상도 */
    public void setDisplay(Point size) {
        this.size = size;
    }

    public Point getDisplay() {
        return size;
    }

    /** 현재 접속 중인 채팅방 번호 (0은 들어가 있지 않음을 표현) */
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
}
