package com.example.kjh.shakeit.cash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.app.AppManager;
import com.example.kjh.shakeit.utils.CurrencyUnitUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 충전 금액을 지정하는 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 6. 12. PM 5:17
 **/
public class ChargeAmountActivity extends AppCompatActivity implements TextWatcher {

    private Unbinder unbinder;
    @BindView(R.id.amount) EditText amount;
    @BindView(R.id.status) TextView status;
    @BindView(R.id.charge) TextView charge;

    // 화폐 단위로 표시한 값
    private String currencyUnitResult = "";

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_amount);

        unbinder = ButterKnife.bind(this);

        amount.addTextChangedListener(this);
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
     클릭이벤트 ==> 충전하기
     ------------------------------------------------------------------*/
    @OnClick(R.id.charge)
    void onClickCharge() {
        Intent intent = new Intent();
        intent.putExtra("amount", CurrencyUnitUtil.toAmount(amount.getText().toString()));
        setResult(RESULT_OK, intent);
        finish();
    }

    /**------------------------------------------------------------------
     메서드 ==> EditText 변화 이벤트
     ------------------------------------------------------------------*/
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(TextUtils.isEmpty(charSequence.toString())) {
            status.setText("");
            charge.setVisibility(View.GONE);
        }
        // 1000원 이하 충전 결제 불가
        else if(CurrencyUnitUtil.toAmount(charSequence.toString()) < 1000
                    && !charSequence.toString().equals(currencyUnitResult)) {
            currencyUnitResult = CurrencyUnitUtil.toCurrency(charSequence.toString());
            amount.setText(currencyUnitResult);
            amount.setSelection(currencyUnitResult.length());
            status.setText(R.string.msg_for_charge_over_1000);
            charge.setVisibility(View.GONE);
        } else if(!charSequence.toString().equals(currencyUnitResult)){
            currencyUnitResult = CurrencyUnitUtil.toCurrency(charSequence.toString());
            amount.setText(currencyUnitResult);
            amount.setSelection(currencyUnitResult.length());
            charge.setVisibility(View.VISIBLE);

            int afterCharge = CurrencyUnitUtil.toAmount(charSequence.toString()) + getIntent().getIntExtra("cash", 0);
            String afterChargeStr = CurrencyUnitUtil.toCurrency(afterCharge);
            status.setText("충전 후 포인트 : " + afterChargeStr);
        }
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    @Override
    public void afterTextChanged(Editable editable) {}
}
