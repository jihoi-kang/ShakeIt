package com.example.kjh.shakeit.main;

import android.content.Intent;
import android.util.Log;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.app.App;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.netty.NettyService;

public class MainPresenter implements MainContract.Presenter {

    private final String TAG = MainPresenter.class.getSimpleName();

    private MainContract.View view;
    private MainContract.Model model;

    public MainPresenter(MainContract.View view, MainContract.Model model) {
        this.view = view;
        this.model = model;
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    public void onCreate() {
        User user = view.getUser();

        /** 채팅목록 Realm에 저장 */
        model.getChatLogList(user.getUserId(), view.getPoint(), new ResultCallback() {
            @Override
            public void onSuccess(String body) {
                Log.d(TAG, "onSuccess");
            }

            @Override
            public void onFailure(String errorMsg) {
                Log.d(TAG, "onFailure => " + errorMsg);
            }
        });
    }

    /**------------------------------------------------------------------
     생명주기 ==> onStart()
     ------------------------------------------------------------------*/
    @Override
    public void onStart() {
        /** Netty 서비스 시작 */
        Intent intent = new Intent(App.getApplication(), NettyService.class);
        intent.putExtra("size", view.getPoint());
        App.getApplication().startService(intent);
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    public void onDestroy() {
        /** Netty 서비스 종료 */
//        App.getApplication().stopService(new Intent(App.getApplication(), NettyService.class));
    }
}
