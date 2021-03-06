package com.example.kjh.shakeit.main.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.app.AppManager;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.adapter.FriendListAdapter;
import com.example.kjh.shakeit.main.chat.contract.AddChatContract;
import com.example.kjh.shakeit.main.chat.presenter.AddChatPresenter;
import com.example.kjh.shakeit.utils.Injector;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 채팅방 생성 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 6. 12. PM 5:38
 **/
public class AddChatActivity extends AppCompatActivity implements AddChatContract.View {

    private final String TAG = AddChatActivity.class.getSimpleName();

    private AddChatContract.Presenter presenter;

    private Unbinder unbinder;
    @BindView(R.id.invite) TextView invite;
    @BindView(R.id.friend_list) RecyclerView friendListView;

    private FriendListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<User> friends = new ArrayList<>();
    ArrayList<User> invitedFriends = new ArrayList<>();

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);

        unbinder = ButterKnife.bind(this);

        presenter = new AddChatPresenter(this, Injector.provideAddChatModel());

        Intent intent = getIntent();
        friends = (ArrayList<User>) intent.getExtras().getSerializable("friends");
        // 자기자신은 제거
        friends.remove(0);

        friendListView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        friendListView.setLayoutManager(layoutManager);

        adapter = new FriendListAdapter(
                this,
                friends, AddChatActivity.class.getSimpleName(),
                /** 대상 선택 이벤트 */
                (user, isChecked) -> presenter.onItemClick(user, isChecked)
        );
        friendListView.setAdapter(adapter);
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
     클릭이벤트 ==> 닫기
     ------------------------------------------------------------------*/
    @OnClick(R.id.close)
    void onClickClose() {
        finish();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 초대
     ------------------------------------------------------------------*/
    @OnClick(R.id.invite)
    void onClickInvite() {
        Intent intent = new Intent();
        intent.putExtra("invitedFriends", invitedFriends);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public ArrayList<User> getInvitedFriends() {
        return invitedFriends;
    }

    /**------------------------------------------------------------------
     메서드 ==> 선택된 대상에 따라 초대 버튼 변화
     ------------------------------------------------------------------*/
    @Override
    public void setInviteButton(ArrayList<User> invitedFriends) {
        this.invitedFriends = invitedFriends;

        if(invitedFriends.size() == 0)
            invite.setVisibility(View.GONE);
        else {
            invite.setVisibility(View.VISIBLE);
            invite.setText("초대 (" + invitedFriends.size() + ")");
        }

    }



}
