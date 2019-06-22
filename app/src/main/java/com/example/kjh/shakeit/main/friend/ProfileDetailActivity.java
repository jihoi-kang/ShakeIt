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
import com.example.kjh.shakeit.app.AppManager;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.chat.ChatActivity;
import com.example.kjh.shakeit.main.friend.contract.ProfileDetailContract;
import com.example.kjh.shakeit.main.friend.presenter.ProfileDetailPresenter;
import com.example.kjh.shakeit.utils.ImageLoaderUtil;
import com.example.kjh.shakeit.utils.Injector;
import com.example.kjh.shakeit.utils.StrUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ProfileDetailActivity extends AppCompatActivity implements ProfileDetailContract.View {

    private final String TAG = ProfileDetailActivity.class.getSimpleName();

    private ProfileDetailContract.Presenter presenter;

    private User user, friend;

    private Unbinder unbinder;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.profile_image) ImageView profileImage;
    @BindView(R.id.profile_background_img) ImageView backgroundProfileImage;
    @BindView(R.id.email) TextView email;
    @BindView(R.id.direct_chat) LinearLayout directChat;
    @BindView(R.id.add_friend) LinearLayout addFriend;

    private final String TAB_FRIENDLIST_FRAGMENT = TabFriendListFragment.class.getSimpleName();
    private final String CHAT_ACTIVITY = ChatActivity.class.getSimpleName();
    private final String SHAKE_ACTIVITY = ShakeActivity.class.getSimpleName();

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        presenter = new ProfileDetailPresenter(this, Injector.provideProfileDetailModel());

        unbinder = ButterKnife.bind(this);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        friend = (User) intent.getSerializableExtra("friend");
        String from = intent.getStringExtra("from");

        /** TabFriendListFragment.class 에서 온 Intent */
        if(from.equals(TAB_FRIENDLIST_FRAGMENT)) {
            // 자기 자신이 아닌경우
            if(intent.getIntExtra("position", -1) != 0)
                directChat.setVisibility(View.VISIBLE);
        }
        /** ChatActivity.class 에서 온 Intent */
        else if(from.equals(CHAT_ACTIVITY)) {
            // 자기 자신이 아닌경우
            if(intent.getIntExtra("position", -1) != 0)
                presenter.checkIsFriend();
        }
        /** ShakeActivity.class 에서 온 Intent */
        else if(from.equals(SHAKE_ACTIVITY)) {
            presenter.checkIsFriend();
        }

        /** 프로필 정보 셋팅 */
        name.setText(friend.getName());
        email.setText(friend.getEmail());

        if(StrUtil.isBlank(friend.getImageUrl())) {
            profileImage.setImageResource(R.drawable.ic_basic_profile);
            backgroundProfileImage.setImageResource(R.color.black);
        } else {
            ImageLoaderUtil.display(this, profileImage, friend.getImageUrl());
            ImageLoaderUtil.display(this, backgroundProfileImage, friend.getImageUrl());
        }

    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    protected void onDestroy() {
        AppManager.getAppManager().removeActivity(this);

        super.onDestroy();
        unbinder.unbind();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 1:1 채팅
     ------------------------------------------------------------------*/
    @OnClick(R.id.direct_chat)
    void onClickDirectChat() {
        Intent intent = new Intent();
        intent.putExtra("friend", friend);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 친구 추가
     ------------------------------------------------------------------*/
    @OnClick(R.id.add_friend)
    void onClickAddFriend() {
        presenter.addFriend();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 닫기
     ------------------------------------------------------------------*/
    @OnClick(R.id.close)
    public void onClose() {
        finish();
    }

    @OnClick(R.id.profile_image)
    void onClickProfileImage() {
        if(StrUtil.isBlank(friend.getImageUrl()))
            return;

        Intent intent = new Intent(ProfileDetailActivity.this, ImageDetailActivity.class);
        intent.putExtra("imageUrl", friend.getImageUrl());
        startActivity(intent);
    }

    /**------------------------------------------------------------------
     메서드 ==> 친구추가 클릭 후 성공 했을시 발생
     ------------------------------------------------------------------*/
    @Override
    public void showAfterAddFriend() {
        /** 개인 채팅일 경우 */
        if(getIntent().getStringExtra("from").equals(CHAT_ACTIVITY)
                && getIntent().getIntExtra("size", 0) == 2){
            addFriend.setVisibility(View.GONE);
            return;
        }

        addFriend.setVisibility(View.GONE);
        directChat.setVisibility(View.VISIBLE);
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public User getFriend() {
        return friend;
    }

    @Override
    public void setIsFriend(boolean condition) {
        if(condition) {
            /** 개인 채팅일 경우 */
            if(getIntent().getStringExtra("from").equals(CHAT_ACTIVITY)
                    && getIntent().getIntExtra("size", 0) == 2)
                return;

            directChat.setVisibility(View.VISIBLE);
        } else
            addFriend.setVisibility(View.VISIBLE);
    }
}
