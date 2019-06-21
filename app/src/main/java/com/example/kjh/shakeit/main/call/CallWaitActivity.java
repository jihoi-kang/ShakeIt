package com.example.kjh.shakeit.main.call;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.app.AppManager;
import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.netty.NettyClient;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.utils.ImageLoaderUtil;
import com.example.kjh.shakeit.utils.StrUtil;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.DISCONN_WEBRTC;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.REQUEST;

/**
 * 영상 통화 하기전 거절 및 승락 하는 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 27. PM 8:53
 **/
public class CallWaitActivity extends AppCompatActivity {

    private Unbinder unbinder;
    @BindView(R.id.name) TextView nameView;
    @BindView(R.id.profile_image) ImageView profileImage;

    private String roomID;
    private User otherUser;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_wait);

        unbinder = ButterKnife.bind(this);
        BusProvider.getInstance().register(this);

        Intent intent = getIntent();
        roomID = intent.getStringExtra("roomID");
        otherUser = (User) intent.getSerializableExtra("otherUser");

        nameView.setText(otherUser.getName());
        if(StrUtil.isBlank(otherUser.getImageUrl()))
            profileImage.setImageResource(R.drawable.ic_basic_profile);
        else
            ImageLoaderUtil.display(this, profileImage, otherUser.getImageUrl());
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    protected void onDestroy() {
        AppManager.getAppManager().removeActivity(this);

        super.onDestroy();
        unbinder.unbind();
        BusProvider.getInstance().unregister(this);
    }

    /**------------------------------------------------------------------
     메서드 ==> Soft Back 키 눌렀을 때 어떤일도 해서는 안됨
     ------------------------------------------------------------------*/
    @Override
    public void onBackPressed() {}

    /**------------------------------------------------------------------
     클릭이벤트 ==> 거절
     ------------------------------------------------------------------*/
    @OnClick(R.id.decline)
    void onClickDecline() {
        MessageHolder holder = new MessageHolder();
        holder.setSign(REQUEST);
        holder.setType(DISCONN_WEBRTC);
        holder.setBody("" + otherUser.getUserId());
        NettyClient.getInstance().sendMsgToServer(holder, null);

        setResult(RESULT_CANCELED);
        finish();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 승락
     ------------------------------------------------------------------*/
    @OnClick(R.id.accept)
    void onClickAccept() {
        Intent intent = new Intent();
        intent.putExtra("roomID", roomID);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**------------------------------------------------------------------
     구독이벤트 ==> Netty에서 이벤트 왔을 때 ==> 메시지 받거나 콜백
     ------------------------------------------------------------------*/
    @Subscribe
    public void nettyEvent (Events.nettyEvent event) {
        MessageHolder holder = event.getMessageHolder();

        if(holder.getType() == DISCONN_WEBRTC)
            finish();
        else
            return;
    }

}
