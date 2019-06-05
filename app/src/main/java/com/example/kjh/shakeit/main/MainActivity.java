package com.example.kjh.shakeit.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.app.AppManager;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.fcm.FcmGenerator;
import com.example.kjh.shakeit.main.adapter.ChatRoomListAdapter;
import com.example.kjh.shakeit.main.adapter.ViewPagerAdapter;
import com.example.kjh.shakeit.main.chat.AddChatActivity;
import com.example.kjh.shakeit.main.chat.ChatActivity;
import com.example.kjh.shakeit.main.friend.AddFriendActivity;
import com.example.kjh.shakeit.main.friend.ShakeActivity;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.utils.Injector;
import com.example.kjh.shakeit.utils.ProgressDialogGenerator;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_FRIEND_LIST_TO_PROFILE_DETAIL;
import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_MAIN_AFTER_LOGIN_TO_ADD_CHAT;
import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_MAIN_TO_CHAT;
import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_MAIN_TO_SHAKE;
import static com.example.kjh.shakeit.app.Constant.nonTabIcon;
import static com.example.kjh.shakeit.app.Constant.onTabIcon;
import static com.example.kjh.shakeit.app.Constant.tabLayoutImage;
import static com.example.kjh.shakeit.app.Constant.titles;

/**
 * 로그인 후 메인 화면 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 26. PM 5:51
 **/
public class MainActivity extends AppCompatActivity implements MainContract.View {

    private String TAG = MainActivity.class.getSimpleName();

    private MainContract.Presenter presenter;

    private User user;

    private Point size;
    private ProgressDialog dialog;

    private Unbinder unbinder;
    @BindView(R.id.layout_tab) TabLayout tabLayout;
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.add) ImageView add;
    @BindView(R.id.shake) ImageView shake;

    /** 뷰페이저 변수 */
    private ViewPagerAdapter viewPagerAdapter;
    private int position = 0;

    private ArrayList<User> friends = new ArrayList<>();

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_after_login);

        /** 디스플레이 해상도 구하기 */
        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        Log.d(TAG, "해상도: X => " + size.x + " / Y => " + size.y);

        unbinder = ButterKnife.bind(this);

        presenter = new MainPresenter(this, Injector.provideAfterLoginMainModel());

        BusProvider.getInstance().register(this);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        presenter.onCreate();

        /** 로그인 후 Token 정보 업데이트 */
        FcmGenerator.updateUserToken(user.getUserId(), "login");

        /** 뷰페이저 초기화 및 셋팅 */
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        setIconToViewpager(0);

        /** 초기 화면 설정 위해 시간 벌어주기 */
        dialog = ProgressDialogGenerator.create(this, "초기 화면 설정중입니다");
        dialog.show();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onStart()
     ------------------------------------------------------------------*/
    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onResume()
     ------------------------------------------------------------------*/
    @Override
    protected void onResume() {
        super.onResume();

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setIconToViewpager(position);
            }
        });

        if(dialog != null) {
            new Handler().postDelayed(() -> {
                dialog.dismiss();
                dialog = null;
            }, 2000);
        }

    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    protected void onDestroy() {
        AppManager.getAppManager().removeActivity(this);

        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        unbinder.unbind();
        presenter.onDestroy();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> TabLayout 우측에 친구 추가, 채팅룸 추가
     ------------------------------------------------------------------*/
    @OnClick(R.id.add)
    void onClickAdd() {
        Intent intent;
        switch (position){
            case 0:
                /** 친구추가 */
                intent = new Intent(MainActivity.this, AddFriendActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case 1:
                /** 채팅 추가 */
                intent = new Intent(MainActivity.this, AddChatActivity.class);
                intent.putExtra("user", user);
                Bundle bundle = new Bundle();
                bundle.putSerializable("friends",friends);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_CODE_MAIN_AFTER_LOGIN_TO_ADD_CHAT);
                break;
        }

    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> TabLayout 우측에 흔들기
     ------------------------------------------------------------------*/
    @OnClick(R.id.shake)
    void onClickShake() {
        Intent intent = new Intent(MainActivity.this, ShakeActivity.class);
        intent.putExtra("user", user);
        startActivityForResult(intent, REQUEST_CODE_MAIN_TO_SHAKE);
    }

    /**------------------------------------------------------------------
     콜백이벤트 ==> onActivityResult()
     ------------------------------------------------------------------*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;

        ArrayList<ChatRoom> chatRooms = null;
        ArrayList<User> participants = null;

        /** 채팅방 만든후 */
        if(requestCode == REQUEST_CODE_MAIN_AFTER_LOGIN_TO_ADD_CHAT) {
            chatRooms = ChatRoomListAdapter.getChatRooms();
            participants = (ArrayList<User>) data.getSerializableExtra("invitedFriends");
        }
        /** ChatActivity 에서 돌아옴(그전 1:1 채팅을 어디선가 실행) */
        else if(requestCode == REQUEST_CODE_MAIN_TO_CHAT) {
            chatRooms = ChatRoomListAdapter.getChatRooms();
            User friend = (User) data.getSerializableExtra("friend");
            participants = new ArrayList<>();
            participants.add(friend);
        }
        /** ProfileDetailActivity 에서 돌아옴(1:1 채팅 눌렀을 때) */
        else if(requestCode == REQUEST_CODE_FRIEND_LIST_TO_PROFILE_DETAIL) {
            chatRooms = ChatRoomListAdapter.getChatRooms();
            User friend = (User) data.getSerializableExtra("friend");
            participants = new ArrayList<>();
            participants.add(friend);
        }
        /** ShakeActivity 에서 돌아옴(흔든 사람과 매칭되어 친구 추가 후 1:1 채팅 눌렀을 때) */
        else if (requestCode == REQUEST_CODE_MAIN_TO_SHAKE) {
            chatRooms = ChatRoomListAdapter.getChatRooms();
            User friend = (User) data.getSerializableExtra("friend");
            participants = new ArrayList<>();
            participants.add(friend);
        }

        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra("user", user);

        /** 중복되는 방이 존재하는지 확인 */
        ChatRoom room = new ChatRoom();
        room.setParticipants(participants);
        room.setMemberCount(participants.size() + 1);
        intent.putExtra("room", room);

        for(int index = 0; index < chatRooms.size(); index++){
            if(isEqual(participants, chatRooms.get(index).getParticipants()))
                intent.putExtra("room", chatRooms.get(index));
        }

        /** 단체 채팅인 경우 참여자들 메뉴를 통해 1:1 채팅할 수 있으므로 StartActivityForResult 호출 */
        if(room.getParticipants().size() > 1)
            startActivityForResult(intent, REQUEST_CODE_MAIN_TO_CHAT);
        else
            startActivity(intent);
    }

    /**------------------------------------------------------------------
     메서드 ==> 순서 상관 없이 두 ArrayList가 중복되는지 확인
     ------------------------------------------------------------------*/
    private boolean isEqual(ArrayList<User> participants, ArrayList<User> comparison) {
        int participantsLength = participants.size();
        int comparisonLength = comparison.size();
        int cnt = 0;

        if(participantsLength != comparisonLength)
            return false;

        for(int participantsIdx = 0; participantsIdx < participantsLength; participantsIdx++) {
            for(int comparisonIdx = 0; comparisonIdx < comparisonLength; comparisonIdx++) {
                if(participants.get(participantsIdx).getUserId() == comparison.get(comparisonIdx).getUserId()) {
                    cnt++;
                    break;
                }
            }
        }

        if(cnt == participantsLength && cnt == comparisonLength)
            return true;

        return false;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Point getPoint() {
        return size;
    }

    /**------------------------------------------------------------------
     메서드 ==> 뷰페이저 탭 변경시 적절한 탭 아이콘 변경 로직
     ------------------------------------------------------------------*/
    private void setIconToViewpager(int position) {
        this.position = position;
        for(int index = 0; index < viewPagerAdapter.getCount(); index++) {
            if(index == position) {
                title.setText(titles[index]);
                switch (position) {
                    case 0:
                        add.setImageResource(tabLayoutImage[index]);
                        shake.setImageResource(R.drawable.ic_shake);
                        break;
                    case 1:
                        add.setImageResource(tabLayoutImage[index]);
                        shake.setImageDrawable(null);
                        break;
                    case 2:
                        add.setImageDrawable(null);
                        shake.setImageDrawable(null);
                        break;
                }
                tabLayout.getTabAt(index).setIcon(onTabIcon[index]);
            } else
                tabLayout.getTabAt(index).setIcon(nonTabIcon[index]);
        }

    }

    /**------------------------------------------------------------------
     구독이벤트 ==> 프로필 변경시 발생
     ------------------------------------------------------------------*/
    @Subscribe
    public void getUpdateProfileInfo(Events.updateProfileEvent event) {
        user = event.getUser();
    }

    /**------------------------------------------------------------------
     구독이벤트 ==> 친구목록 변경시 발생
     ------------------------------------------------------------------*/
    @Subscribe
    public void getFriendList(Events.friendEvent event) {
        friends = event.getFriends();
    }
}
