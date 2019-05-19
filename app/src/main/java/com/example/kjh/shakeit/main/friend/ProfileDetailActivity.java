package com.example.kjh.shakeit.main.friend;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.utils.ImageLoaderUtil;
import com.example.kjh.shakeit.utils.StrUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ProfileDetailActivity extends AppCompatActivity {

    private final String TAG = ProfileDetailActivity.class.getSimpleName();

    private User user;

    private Unbinder unbinder;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.profile_image) ImageView profileImage;
    @BindView(R.id.profile_background_img) ImageView backgroundProfileImage;
    @BindView(R.id.email) TextView email;
    @BindView(R.id.direct_chat) LinearLayout directChat;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        unbinder = ButterKnife.bind(this);
    }

    /**------------------------------------------------------------------
     생명주기 ==> onResume()
     ------------------------------------------------------------------*/
    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        /** 친구목록의 위치 */
        int position = intent.getIntExtra("position", -1);

        name.setText(user.getName());
        email.setText(user.getEmail());

        if(StrUtil.isBlank(user.getImageUrl())) {
            profileImage.setImageResource(R.drawable.ic_basic_profile);
            backgroundProfileImage.setImageResource(R.color.black);
        } else {
            ImageLoaderUtil.display(this, profileImage, user.getImageUrl());
            ImageLoaderUtil.display(this, backgroundProfileImage, user.getImageUrl());
        }

        /** 위치가 0이면 자기 자신 */
        if(position == 0)
            directChat.setVisibility(View.GONE);
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 1:1 채팅
     ------------------------------------------------------------------*/
    @OnClick(R.id.direct_chat)
    public void onClickDirectChat() {
        // TODO: 2019. 5. 3. 클릭시 채팅 할 수 있도록
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 닫기
     ------------------------------------------------------------------*/
    @OnClick(R.id.close)
    public void onClose() {
        finish();
    }
}
