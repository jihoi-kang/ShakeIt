package com.example.kjh.shakeit.main.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.beforelogin.view.MainActivity;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.fcm.FcmGenerator;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.profile.UpdateProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;

/**
 * 더보기 탭
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 27. PM 5:47
 **/
public class TabMoreFragment extends Fragment {

    private final String TAG = TabMoreFragment.class.getSimpleName();

    private static TabMoreFragment instance = null;
    private View view;

    private Unbinder unbinder;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.email) TextView statusMessage;
    @BindView(R.id.profile_image) ImageView profileImage;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private User user;

    public static TabMoreFragment getInstance() {
        if(instance == null)
            instance = new TabMoreFragment();

        return instance;
    }

    /**------------------------------------------------------------------
     생명주기 ==> onAttach()
     ------------------------------------------------------------------*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        user = ((com.example.kjh.shakeit.main.view.MainActivity)getActivity()).getUser();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BusProvider.getInstance().register(this);

        preferences = getActivity().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreateView()
     ------------------------------------------------------------------*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_more, container, false);
        unbinder = ButterKnife.bind(this, view);

        showProfileInfo();

        return view;
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroyView()
     ------------------------------------------------------------------*/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 프로필 수정
     ------------------------------------------------------------------*/
    @OnClick(R.id.update_profile)
    void onClickUpdateProfile() {
        Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 로그아웃
     ------------------------------------------------------------------*/
    @OnClick(R.id.logout)
    void onClickLogout() {
        FirebaseAuth.getInstance().signOut();
        editor.clear();
        editor.commit();

        FcmGenerator.updateUserToken(user.get_id(), "logout");

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(intent);
    }

    /**------------------------------------------------------------------
     메서드 ==> 프로필 정보 나타내기
     ------------------------------------------------------------------*/
    private void showProfileInfo() {
        name.setText(user.getName());
        statusMessage.setText(user.getEmail());

        if(user.getImage_url() == null || user.getImage_url().equals(""))
            profileImage.setImageResource(R.drawable.ic_basic_profile);
        else {
            Glide.with(this)
                    .load(user.getImage_url())
                    .into(profileImage);
        }
    }

    /**------------------------------------------------------------------
     구독이벤트 ==> 프로필 변경시 발생
     ------------------------------------------------------------------*/
    @Subscribe
    public void getUpdateProfileInfo(Events.updateProfileEvent event) {
        user = event.getUser();
        showProfileInfo();
    }
}

