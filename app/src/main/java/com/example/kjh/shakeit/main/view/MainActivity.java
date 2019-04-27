package com.example.kjh.shakeit.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.adapter.ViewPagerAdapter;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.fcm.FcmGenerator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.kjh.shakeit.Statics.nonTabIcon;
import static com.example.kjh.shakeit.Statics.onTabIcon;

/**
 * 로그인 후 메인 화면 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 26. PM 5:51
 **/
public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private User user;

    private Unbinder unbinder;
    @BindView(R.id.layout_tab) TabLayout tabLayout;
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.title) TextView title;

    private ViewPagerAdapter viewPagerAdapter;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_after_login);

        unbinder = ButterKnife.bind(this);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(onTabIcon[0]);
        tabLayout.getTabAt(1).setIcon(nonTabIcon[1]);
        tabLayout.getTabAt(2).setIcon(nonTabIcon[2]);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setIconToViewpager(position);
            }
        });

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        /** 로그인 후 Token 정보 업데이트 */
        FcmGenerator.updateUserToken(user.get_id(), "login");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    /**------------------------------------------------------------------
     메서드 ==> 뷰페이저 탭 변경시 적절한 탭 아이콘 변경 로직
     ------------------------------------------------------------------*/
    private void setIconToViewpager(int position) {
        for(int i = 0; i < viewPagerAdapter.getCount(); i++) {
            if(i == position)
                tabLayout.getTabAt(i).setIcon(onTabIcon[i]);
            else
                tabLayout.getTabAt(i).setIcon(nonTabIcon[i]);
        }

    }
}
