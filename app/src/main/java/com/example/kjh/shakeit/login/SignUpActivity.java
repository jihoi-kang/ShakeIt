package com.example.kjh.shakeit.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.login.contract.SignUpContract;
import com.example.kjh.shakeit.login.presenter.SignUpPresenter;
import com.example.kjh.shakeit.utils.Injector;
import com.example.kjh.shakeit.utils.KeyboardManager;
import com.example.kjh.shakeit.utils.ProgressDialogGenerator;
import com.example.kjh.shakeit.utils.ToastGenerator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 회원가입 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 26. PM 5:35
 **/
public class SignUpActivity extends AppCompatActivity implements SignUpContract.View {

    private SignUpContract.Presenter presenter;

    private Unbinder unbinder;
    @BindView(R.id.inputEmail) EditText inputEmail;
    @BindView(R.id.inputPassword) EditText inputPassword;
    @BindView(R.id.inputPasswordAgain) EditText inputPasswordAgain;
    @BindView(R.id.inputName) EditText inputName;

    private ProgressDialog progressDialog;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        unbinder = ButterKnife.bind(this);

        presenter = new SignUpPresenter(this, Injector.provideSignUpModel());

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
     클릭이벤트 ==> 회원가입
     ------------------------------------------------------------------*/
    @OnClick(R.id.signUpButton)
    void onClickSignUp() {
        presenter.onClickSignUp();
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
        ToastGenerator.show(R.string.msg_for_incorrect_email);
    }

    @Override
    public String getInputPassword() {
        return inputPassword.getText().toString();
    }

    @Override
    public void showMessageForIncorrectPassword() {
        ToastGenerator.show(R.string.msg_for_incorrect_password);
    }

    @Override
    public String getInputPasswordAgain() {
        return inputPasswordAgain.getText().toString();
    }

    @Override
    public void showMessageForIncorrectPasswordAgain() {
        ToastGenerator.show(R.string.msg_for_incorrect_password_again);
    }

    @Override
    public String getInputName() {
        return inputName.getText().toString();
    }

    @Override
    public void showMessageForIncorrectName() {
        ToastGenerator.show(R.string.msg_for_incorrect_name);
    }

    @Override
    public void hideSoftKeyboard() {
        KeyboardManager.hideKeyboard(this, inputEmail);
        KeyboardManager.hideKeyboard(this, inputPassword);
        KeyboardManager.hideKeyboard(this, inputPasswordAgain);
        KeyboardManager.hideKeyboard(this, inputName);
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
    public void showMessageForSuccessSignUp() {
        ToastGenerator.show(R.string.msg_for_success_signUp);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void showMessageForFailureSignUp(String errorMsg) {
        switch (errorMsg){
            case "SERVICE_UNAVAILABLE":
                ToastGenerator.show(R.string.msg_for_failure_signUp_becauseOfOverlapEmail);
                break;
            default:
                ToastGenerator.show(R.string.msg_for_failure_signUp);
                break;
        }
    }
}
