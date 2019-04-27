package com.example.kjh.shakeit.beforelogin.contract;

import com.example.kjh.shakeit.beforelogin.callback.ResultCallback;

public interface SignUpContract {

    interface View {

        String getInputEmail();
        void showMessageForIncorrectEmail();
        String getInputPassword();
        void showMessageForIncorrectPassword();
        String getInputPasswordAgain();
        void showMessageForIncorrectPasswordAgain();
        String getInputName();
        void showMessageForIncorrectName();
        void hideSoftKeyboard();
        void showLoadingDialog();
        void hideLoadingDialog();
        void showMessageForSuccessSignUp();
        void finishActivity();
        void showMessageForFailureSignUp(String errorMsg);
    }

    interface Presenter {

        void onClickSignUp();

    }

    interface Model {

        void signUp(String email, String password, String name, ResultCallback callback);

    }
}
