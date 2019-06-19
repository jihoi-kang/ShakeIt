package com.example.kjh.shakeit.cash;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.app.AppManager;
import com.example.kjh.shakeit.cash.contract.WireCashContract;
import com.example.kjh.shakeit.cash.presenter.WireCashPresenter;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.chat.ChatActivity;
import com.example.kjh.shakeit.utils.CurrencyUnitUtil;
import com.example.kjh.shakeit.utils.ImageLoaderUtil;
import com.example.kjh.shakeit.utils.Injector;
import com.example.kjh.shakeit.utils.StrUtil;
import com.example.kjh.shakeit.utils.ToastGenerator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 송금 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 6. 12. PM 5:20
 **/
public class WireCashActivity extends AppCompatActivity implements TextWatcher, WireCashContract.View {

    private final String TAG = WireCashActivity.class.getSimpleName();

    private WireCashContract.Presenter presenter;

    private Unbinder unbinder;
    @BindView(R.id.wire) TextView wire;
    @BindView(R.id.profile_image) ImageView profileImage;
    @BindView(R.id.name) TextView nameTxt;
    @BindView(R.id.amount) EditText amountEdit;
    @BindView(R.id.status) TextView statusTxt;
    @BindView(R.id.lack_point) TextView lackPoint;

    private User user, otherUser;
    private ChatRoom room;

    // 화폐 단위로 표시한 값
    private String currencyUnitResult;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wire_cash);

        unbinder = ButterKnife.bind(this);

        presenter = new WireCashPresenter(this, Injector.provideWireCashModel());

        user = (User) getIntent().getSerializableExtra("user");
        otherUser = (User) getIntent().getSerializableExtra("otherUser");

        /** ChatActivity에서 포인트 송금하는 경우 */
        if(getIntent().getStringExtra("from").equals(ChatActivity.class.getSimpleName()))
            room = (ChatRoom) getIntent().getSerializableExtra("room");
        /** 더보기 탭에서 포인트 보내는 경우 */
        else if(getIntent().getStringExtra("from").equals(ChooseFriendActivity.class.getSimpleName()))
            presenter.getChatRoom();

        /** UI 셋팅 */
        amountEdit.addTextChangedListener(this);

        if(StrUtil.isBlank(otherUser.getImageUrl()))
            profileImage.setImageResource(R.drawable.ic_basic_profile);
        else
            ImageLoaderUtil.display(this, profileImage, otherUser.getImageUrl());

        nameTxt.setText(otherUser.getName());

        statusTxt.setText("포인트 : " + CurrencyUnitUtil.toCurrency(user.getCash()));
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
     클릭이벤트 ==> 송금
     ------------------------------------------------------------------*/
    @OnClick(R.id.wire)
    void onClickWire() {
        presenter.onClickWire();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 닫기
     ------------------------------------------------------------------*/
    @OnClick(R.id.close)
    void onClickClose() {
        finish();
    }

    @Override
    public ChatRoom getChatRoom() {
        return room;
    }

    @Override
    public void showMessageForLackOfPoint() {
        ToastGenerator.show(R.string.msg_for_lack_of_point);
    }

    @Override
    public int getAmount() {
        return CurrencyUnitUtil.toAmount(amountEdit.getText().toString());
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public User getOtherUser() {
        return otherUser;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showMessageForSuccess() {
        ToastGenerator.show(R.string.msg_for_success_wire);
    }

    @Override
    public void setChatRoom(ChatRoom room) {
        this.room = room;
    }

    /**------------------------------------------------------------------
     메서드 ==> EditText 변화 이벤트
     ------------------------------------------------------------------*/
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(TextUtils.isEmpty(charSequence.toString()))
            wire.setVisibility(View.GONE);
        else
            wire.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(charSequence.toString())
                && !charSequence.toString().equals(currencyUnitResult)){
            currencyUnitResult = CurrencyUnitUtil.toCurrency(charSequence.toString());
            amountEdit.setText(currencyUnitResult);
            amountEdit.setSelection(currencyUnitResult.length());

            if(CurrencyUnitUtil.toAmount(charSequence.toString()) <= user.getCash())
                lackPoint.setVisibility(View.GONE);
            else
                lackPoint.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    @Override
    public void afterTextChanged(Editable editable) {}
}
