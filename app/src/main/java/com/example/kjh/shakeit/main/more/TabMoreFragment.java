package com.example.kjh.shakeit.main.more;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.app.App;
import com.example.kjh.shakeit.cash.ChargeAmountActivity;
import com.example.kjh.shakeit.cash.ChooseFriendActivity;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ImageHolder;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.fcm.FcmGenerator;
import com.example.kjh.shakeit.login.MainActivity;
import com.example.kjh.shakeit.netty.NettyService;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.utils.ImageLoaderUtil;
import com.example.kjh.shakeit.utils.ShareUtil;
import com.example.kjh.shakeit.utils.StrUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.otto.Subscribe;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_MAIN_TO_CHARGE;

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
    @BindView(R.id.cash) TextView cashTxt;

    private User user;
    private ArrayList<User> friends = new ArrayList<>();

    private DecimalFormat decimalFormat = new DecimalFormat("#,###");

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
        user = ((com.example.kjh.shakeit.main.MainActivity)getActivity()).getUser();
        friends = ((com.example.kjh.shakeit.main.MainActivity)getActivity()).getFriends();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BusProvider.getInstance().register(this);

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
     클릭이벤트 ==> 포인트 보내기
     ------------------------------------------------------------------*/
    @OnClick(R.id.send)
    void onClickSend() {
        Intent intent = new Intent(getContext(), ChooseFriendActivity.class);
        intent.putExtra("user", user);
        Bundle bundle = new Bundle();
        bundle.putSerializable("friends", friends);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 충전하기
     ------------------------------------------------------------------*/
    @OnClick(R.id.charge)
    void onClickCharge() {
        Intent intent = new Intent(getContext(), ChargeAmountActivity.class);
        intent.putExtra("cash", user.getCash());
        getActivity().startActivityForResult(intent, REQUEST_CODE_MAIN_TO_CHARGE);
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
        ShareUtil.clear();

        /** FCM Token 값 삭제 */
        FcmGenerator.updateUserToken(user.getUserId(), "logout");

        /** Realm 데이터 삭제 */
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        RealmResults<ChatHolder> result = realm.where(ChatHolder.class).findAll();
        result.deleteAllFromRealm();
        RealmResults<ImageHolder> imageResult = realm.where(ImageHolder.class).findAll();
        imageResult.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();

        /** Netty 서버와 연결 끊기 */
        App.getApplication().stopService(new Intent(App.getApplication(), NettyService.class));
    }

    /**------------------------------------------------------------------
     메서드 ==> 프로필 정보 나타내기
     ------------------------------------------------------------------*/
    private void showProfileInfo() {
        name.setText(user.getName());
        statusMessage.setText(user.getEmail());
        cashTxt.setText(decimalFormat.format(user.getCash()));

        if(StrUtil.isBlank(user.getImageUrl()))
            profileImage.setImageResource(R.drawable.ic_basic_profile);
        else
            ImageLoaderUtil.display(getContext(), profileImage, user.getImageUrl());
    }

    /**------------------------------------------------------------------
     구독이벤트 ==> 프로필 변경시 발생
     ------------------------------------------------------------------*/
    @Subscribe
    public void getUpdateProfileInfo(Events.updateProfileEvent event) {
        user = event.getUser();
        getActivity().runOnUiThread(() -> showProfileInfo());
    }

    /**------------------------------------------------------------------
     구독이벤트 ==> 친구목록 변경시 발생
     ------------------------------------------------------------------*/
    @Subscribe
    public void getFriendList(Events.friendEvent event) {
        friends = event.getFriends();
    }

}

