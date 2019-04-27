package com.example.kjh.shakeit.beforelogin.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.beforelogin.contract.EmailLoginContract;
import com.example.kjh.shakeit.beforelogin.presenter.EmailLoginPresenter;
import com.example.kjh.shakeit.utils.Injector;
import com.example.kjh.shakeit.utils.KeyboardManager;
import com.example.kjh.shakeit.utils.ProgressDialogGenerator;
import com.example.kjh.shakeit.utils.ToastGenerator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 이메일로 로그인
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 26. PM 5:33
 **/
public class EmailLoginActivity extends AppCompatActivity implements EmailLoginContract.View {

    private EmailLoginContract.Presenter presenter;

    private Unbinder unbinder;
    @BindView(R.id.inputEmail) EditText inputEmail;
    @BindView(R.id.inputPassword) EditText inputPassword;

    private ProgressDialog progressDialog;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        unbinder = ButterKnife.bind(this);

        presenter = new EmailLoginPresenter(this, Injector.provideEmailLoginModel());
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
     클릭이벤트 ==> 이메일 로그인
     ------------------------------------------------------------------*/
    @OnClick(R.id.emailLoginButton)
    void onClickEmailLogin() {
        presenter.onClickEmailLogin();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 화면 닫기
     ------------------------------------------------------------------*/
    @OnClick(R.id.back)
    void onClickBack() {
        finish();
    }

    @Override
    public String getInputEmail() {
        return inputEmail.getText().toString();
    }

    @Override
    public void showMessageForIncorrectEmail() {
        ToastGenerator.show(this,R.string.msg_for_incorrect_email);
    }

    @Override
    public String getInputPassword() {
        return inputPassword.getText().toString();
    }

    @Override
    public void showMessageForIncorrectPassword() {
        ToastGenerator.show(this,R.string.msg_for_incorrect_password);
    }

    @Override
    public void hideSoftKeyboard() {
        KeyboardManager.hideKeyboard(this, inputEmail);
        KeyboardManager.hideKeyboard(this, inputPassword);
    }

    @Override
    public void showLoadingDialog() {
        progressDialog = ProgressDialogGenerator.show(this, "잠시만 기다려주세요");
        progressDialog.show();
    }

    @Override
    public void hideLoadingDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void showMessageForSuccessLoginAndFinishActivity(String body) {
        ToastGenerator.show(this,R.string.msg_for_success_login);
        Intent intent = new Intent();
        intent.putExtra("user", body);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showMessageForFailureLogin(String errorMsg) {
        ToastGenerator.show(this,R.string.msg_for_failure_login);
    }
}
