package com.example.kjh.shakeit.friend;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.utils.Injector;
import com.example.kjh.shakeit.utils.KeyboardManager;
import com.example.kjh.shakeit.utils.ProgressDialogGenerator;
import com.example.kjh.shakeit.utils.ToastGenerator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 친구 추가 액티비티 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 2. PM 9:12
 **/
public class AddFriendActivity extends AppCompatActivity implements AddFriendContract.View, TextView.OnEditorActionListener {

    private final String TAG = AddFriendActivity.class.getSimpleName();

    private AddFriendContract.Presenter presenter;

    private Unbinder unbinder;
    @BindView(R.id.inputEmail) EditText inputEmail;
    @BindView(R.id.result_layout) LinearLayout resultLayout;
    @BindView(R.id.no_result_layout) LinearLayout noResultLayout;
    @BindView(R.id.inputName) TextView inputName;
    @BindView(R.id.profile_image) ImageView profileImage;
    @BindView(R.id.add_friend) Button addFriend;
    @BindView(R.id.already_friend) TextView alreadyFriend;

    private User user;
    private int friendId = 0;

    private ProgressDialog dialog;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        unbinder = ButterKnife.bind(this);
        inputEmail.setOnEditorActionListener(this);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        presenter = new AddFriendPresenter(this, Injector.provideAddFriendModel());
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
     클릭이벤트 ==> 검색
     ------------------------------------------------------------------*/
    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        presenter.onClickSearch();
        return true;
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 취소
     ------------------------------------------------------------------*/
    @OnClick(R.id.close)
    public void onClose(){
        finish();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 친구 추가
     ------------------------------------------------------------------*/
    @OnClick(R.id.add_friend)
    public void onClickAddFriend() {
        presenter.onClickAddFriend();
    }

    @Override
    public String getInputEmail() {
        return inputEmail.getText().toString();
    }

    @Override
    public void showMessageForNoResult() {
        noResultLayout.setVisibility(View.VISIBLE);
        resultLayout.setVisibility(View.GONE);
    }

    @Override
    public int getFriendId() {
        return friendId;
    }

    @Override
    public void showMessageForIncorrectEmail() {
        ToastGenerator.show(this,R.string.msg_for_incorrect_email);
    }

    @Override
    public void showMessageForFailure() {
        ToastGenerator.show(this, R.string.msg_for_error);
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void showLoadingDialog() {
        dialog = ProgressDialogGenerator.show(this, "잠시만 기다려주세요");
        dialog.show();
    }

    @Override
    public void hideLoadingDialog() {
        dialog.dismiss();
    }

    @Override
    public void showFriendInfo(int _id, String name, String imageUrl, int isFriend) {
        noResultLayout.setVisibility(View.GONE);
        resultLayout.setVisibility(View.VISIBLE);

        inputName.setText(name);

        if(imageUrl == null || imageUrl.equals(""))
            profileImage.setImageResource(R.drawable.ic_basic_profile);
        else
            Glide.with(this).load(imageUrl).into(profileImage);

        /** 친구 추가가 안되어 있음 */
        if(isFriend == 0){
            friendId = _id;
            addFriend.setVisibility(View.VISIBLE);
            alreadyFriend.setVisibility(View.GONE);
        }
        /** 친구 추가 되어 있음 */
        else {
            addFriend.setVisibility(View.GONE);
            alreadyFriend.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideSoftKeyboard() {
        KeyboardManager.hideKeyboard(this, inputEmail);
    }

    @Override
    public void showAfterFriend() {
        addFriend.setVisibility(View.GONE);
        alreadyFriend.setVisibility(View.VISIBLE);
    }
}
