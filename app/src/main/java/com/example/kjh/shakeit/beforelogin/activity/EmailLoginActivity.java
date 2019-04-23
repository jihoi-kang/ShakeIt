package com.example.kjh.shakeit.beforelogin.activity;

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
import com.example.kjh.shakeit.utils.KeboardManager;
import com.example.kjh.shakeit.utils.ProgressDialogGenerator;
import com.example.kjh.shakeit.utils.ToastGenerator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class EmailLoginActivity extends AppCompatActivity implements EmailLoginContract.View {

    private EmailLoginContract.Presenter presenter;

    Unbinder unbinder;
    @BindView(R.id.inputEmail) EditText inputEmail;
    @BindView(R.id.inputPassword) EditText inputPassword;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        unbinder = ButterKnife.bind(this);

        presenter = new EmailLoginPresenter(this, Injector.provideEmailLoginModel());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.emailLoginButton)
    void onClickEmailLogin() {
        presenter.onClickEmailLogin();
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
        KeboardManager.hideKeyboard(this, inputEmail);
        KeboardManager.hideKeyboard(this, inputPassword);
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
