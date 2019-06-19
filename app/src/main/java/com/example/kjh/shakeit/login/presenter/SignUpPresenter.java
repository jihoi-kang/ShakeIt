package com.example.kjh.shakeit.login.presenter;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.login.contract.SignUpContract;
import com.example.kjh.shakeit.utils.Validator;

public class SignUpPresenter implements SignUpContract.Presenter {

    private SignUpContract.View view;
    private SignUpContract.Model model;

    public SignUpPresenter(SignUpContract.View view, SignUpContract.Model model) {
        this.view = view;
        this.model = model;
    }

    /**------------------------------------------------------------------
     메서드 ==> 회원가입 클릭시 로직
     ------------------------------------------------------------------*/
    @Override
    public void onClickSignUp() {
        String email = view.getInputEmail();
        String password = view.getInputPassword();
        String passwordAgain = view.getInputPasswordAgain();
        String name = view.getInputName();

        /** 정규식 확인 */
        if(!Validator.isValidEmail(email)) {
            view.showMessageForIncorrectEmail();
            return;
        }
        if(!Validator.isValidPassword(password)) {
            view.showMessageForIncorrectPassword();
            return;
        }
        if(!(password.equals(passwordAgain))) {
            view.showMessageForIncorrectPasswordAgain();
            return;
        }
        if(!Validator.isValidName(name)) {
            view.showMessageForIncorrectName();
            return;
        }

        view.hideSoftKeyboard();
        view.showLoadingDialog();

        model.signUp(email, password, name, new ResultCallback(){
            @Override
            public void onSuccess(String body) {
                view.hideLoadingDialog();
                view.showMessageForSuccessSignUp();
                view.finishActivity();
            }

            @Override
            public void onFailure(String errorMsg) {
                view.hideLoadingDialog();
                view.showMessageForFailureSignUp(errorMsg);
            }
        });
    }
}
