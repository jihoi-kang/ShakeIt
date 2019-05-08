package com.example.kjh.shakeit.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.fcm.FcmGenerator;
import com.example.kjh.shakeit.main.adapter.ViewPagerAdapter;
import com.example.kjh.shakeit.main.friend.AddFriendActivity;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.utils.Injector;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.kjh.shakeit.Statics.nonTabIcon;
import static com.example.kjh.shakeit.Statics.onTabIcon;
import static com.example.kjh.shakeit.Statics.tabLayoutImage;
import static com.example.kjh.shakeit.Statics.titles;

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

    private Unbinder unbinder;
    @BindView(R.id.layout_tab) TabLayout tabLayout;
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.add) ImageView add;

    /** 뷰페이저 변수 */
    private ViewPagerAdapter viewPagerAdapter;
    private int position = 0;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_after_login);

        unbinder = ButterKnife.bind(this);

        presenter = new MainPresenter(this, Injector.provideAfterLoginMainModel());

        BusProvider.getInstance().register(this);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        /** 로그인 후 Token 정보 업데이트 */
        FcmGenerator.updateUserToken(user.getUserId(), "login");

        /** 뷰페이저 초기화 및 셋팅 */
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        add.setImageResource(tabLayoutImage[0]);
        title.setText(titles[0]);
        tabLayout.getTabAt(0).setIcon(onTabIcon[0]);
        tabLayout.getTabAt(1).setIcon(nonTabIcon[1]);
        tabLayout.getTabAt(2).setIcon(nonTabIcon[2]);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setIconToViewpager(position);
            }
        });

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
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        BusProvider.getInstance().unregister(this);

        presenter.onDestroy();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> TabLayout 우측에 친구 추가, 채팅룸 추가
     ------------------------------------------------------------------*/
    @OnClick(R.id.add)
    void onClickAdd() {
        Intent intent = null;
        switch (position){
            case 0:
                /** 친구추가 */
                intent = new Intent(MainActivity.this, AddFriendActivity.class);
                intent.putExtra("user", user);
                break;
            case 1:
                /** 채팅 추가 */
                break;
        }
        startActivity(intent);
    }

    @Override
    public User getUser() {
        return user;
    }


    /**------------------------------------------------------------------
     메서드 ==> 뷰페이저 탭 변경시 적절한 탭 아이콘 변경 로직
     ------------------------------------------------------------------*/
    private void setIconToViewpager(int position) {
        this.position = position;
        for(int i = 0; i < viewPagerAdapter.getCount(); i++) {
            if(i == position) {
                title.setText(titles[i]);
                if(position == 2)
                    add.setImageDrawable(null);
                else
                    add.setImageResource(tabLayoutImage[i]);
                tabLayout.getTabAt(i).setIcon(onTabIcon[i]);
            } else
                tabLayout.getTabAt(i).setIcon(nonTabIcon[i]);
        }

    }

    /**------------------------------------------------------------------
     구독이벤트 ==> 프로필 변경시 발생
     ------------------------------------------------------------------*/
    @Subscribe
    public void getUpdateProfileInfo(Events.updateProfileEvent event) {
        user = event.getUser();
    }
}
