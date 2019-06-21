package com.example.kjh.shakeit.main.friend.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.friend.ProfileDetailActivity;
import com.example.kjh.shakeit.main.friend.ShakeActivity;
import com.example.kjh.shakeit.main.friend.contract.ShakeContract;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.utils.Serializer;
import com.example.kjh.shakeit.utils.StrUtil;
import com.squareup.otto.Subscribe;

import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_SHAKE_TO_PROFILE_DETAIL;
import static com.example.kjh.shakeit.main.friend.ShakeActivity.shakeActHandler;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.SHAKE_OFF;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.SHAKE_ON;

public class ShakePresenter implements ShakeContract.Presenter {

    private final String TAG = ShakePresenter.class.getSimpleName();

    private ShakeContract.View view;
    private ShakeContract.Model model;

    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;

    public ShakePresenter(ShakeContract.View view, ShakeContract.Model model) {
        this.view = view;
        this.model = model;
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    public void onCreate() {
        BusProvider.getInstance().register(this);
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
    }

    /**------------------------------------------------------------------
     메서드 ==> 흔들렸음을 감지
     ------------------------------------------------------------------*/
    @Override
    public void onSensorChanged(float gForce) {
        if(gForce > SHAKE_THRESHOLD_GRAVITY && !view.getIsShaking()) {
            Log.d(TAG, "흔들기 발생");
            view.hideUserInfo();

            model.sendMessageToServer(view.getUser(), SHAKE_ON);

            view.executeVibrate();
            view.setIsShaking(true);
            view.executeThread();
        }
    }

    /**------------------------------------------------------------------
     메서드 ==> 흔들기 시간 초과 종료
     ------------------------------------------------------------------*/
    @Override
    public void offShake() {
        model.sendMessageToServer(view.getUser(), SHAKE_OFF);
    }

    @Override
    public void onClickProfile() {
        /** ProfileImage, Name View를 숨기는 것이기 때문에 잘 못 눌릴 수도 있음 */
        /** 비어있으면 클릭되지 않음으로 인지 */
        if(StrUtil.isBlank(view.getNameTxt()))
            return;

        Intent intent = new Intent(view.getContext(), ProfileDetailActivity.class);
        intent.putExtra("user", view.getUser());
        intent.putExtra("friend", view.getTargetUser());
        intent.putExtra("from", ShakeActivity.class.getSimpleName());
        ((Activity)view.getContext()).startActivityForResult(intent, REQUEST_CODE_SHAKE_TO_PROFILE_DETAIL);
    }

    /**------------------------------------------------------------------
     구독이벤트 ==> Netty에서 이벤트 왔을 때 ==> 메시지 받거나 콜백
     ------------------------------------------------------------------*/
    @Subscribe
    public void nettyEvent (Events.nettyEvent event) {
        MessageHolder holder = event.getMessageHolder();
        User user;
        if(holder.getType() == SHAKE_ON) {
            user = Serializer.deserialize(holder.getBody(), User.class);
            Message msg = shakeActHandler.obtainMessage();
            msg.obj = user;
            shakeActHandler.sendMessage(msg);
        } else
            return;

    }
}
