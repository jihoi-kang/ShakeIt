package com.example.kjh.shakeit.login.presenter;

import com.example.kjh.shakeit.callback.ResultCallback;
import com.example.kjh.shakeit.login.contract.EmailLoginContract;
import com.example.kjh.shakeit.utils.Validator;

public class EmailLoginPresenter implements EmailLoginContract.Presenter {

    private EmailLoginContract.View view;
    private EmailLoginContract.Model model;

    public EmailLoginPresenter(EmailLoginContract.View view, EmailLoginContract.Model model) {
        this.view = view;
        this.model = model;
    }

    /**------------------------------------------------------------------
     메서드 ==> 이메일 로그인 클릭시 로직
     ------------------------------------------------------------------*/
    @Override
    public void onClickEmailLogin() {
        String inputEmail = view.getInputEmail();
        String inputPassword = view.getInputPassword();

        /** 정규식 확인 */
        if(!Validator.isValidEmail(inputEmail)) {
            view.showMessageForIncorrectEmail();
            return;
        }

        if(!Validator.isValidPassword(inputPassword)) {
            view.showMessageForIncorrectPassword();
            return;
        }

        view.hideSoftKeyboard();
        view.showLoadingDialog();

        model.login(inputEmail, inputPassword, new ResultCallback(){

            @Override
            public void onSuccess(String body) {
                view.hideLoadingDialog();
                view.showMessageForSuccessLoginAndFinishActivity(body);
            }

            @Override
            public void onFailure(String errorMsg) {
                view.hideLoadingDialog();
                view.showMessageForFailureLogin(errorMsg);
            }
        });
    }
}

