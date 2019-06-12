package com.example.kjh.shakeit.cash;

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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_CHOOSE_FRIEND_TO_WIRE_CASH;

/**
 * 송금할 친구를 선택하는 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 6. 12. PM 5:18
 **/
public class ChooseFriendActivity extends AppCompatActivity {

    private final String TAG = ChooseFriendActivity.class.getSimpleName();

    private Unbinder unbinder;
    @BindView(R.id.confirm) TextView confirm;
    @BindView(R.id.friend_list) RecyclerView friendListView;

    private FriendListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<User> friends = new ArrayList<>();
    User otherUser, user;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_friend);

        unbinder = ButterKnife.bind(this);

        Intent intent = getIntent();
        friends = (ArrayList<User>) intent.getExtras().getSerializable("friends");
        user = (User) intent.getSerializableExtra("user");
        // 자기자신은 제거
        friends.remove(0);

        friendListView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        friendListView.setLayoutManager(layoutManager);

        adapter = new FriendListAdapter(
                this,
                friends, ChooseFriendActivity.class.getSimpleName(),
                /** 대상 선택 이벤트 */
                (user, isChecked) -> onItemClick(user, isChecked)
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
     콜백이벤트 ==> onActivityResult()
     ------------------------------------------------------------------*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 결제가 정상적으로 완료 되어 돌아온 경우
        if(resultCode == RESULT_OK
                && requestCode == REQUEST_CODE_CHOOSE_FRIEND_TO_WIRE_CASH)
            finish();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 닫기
     ------------------------------------------------------------------*/
    @OnClick(R.id.close)
    void onClickClose() {
        finish();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 송금할 대상 선택 후 확인
     ------------------------------------------------------------------*/
    @OnClick(R.id.confirm)
    void onClickConfirm() {
        Intent intent = new Intent(this, WireCashActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("otherUser", otherUser);
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_FRIEND_TO_WIRE_CASH);
    }

    /**------------------------------------------------------------------
     메서드 ==> 아이템 클릭시 이벤트
     ------------------------------------------------------------------*/
    private void onItemClick(User user, boolean isChecked) {
        /** 초대할 대상 Array 업데이트 */
        if(isChecked) {
            otherUser = user;
            confirm.setVisibility(View.VISIBLE);
        } else {
            otherUser = null;
            confirm.setVisibility(View.GONE);
        }
    }
}
