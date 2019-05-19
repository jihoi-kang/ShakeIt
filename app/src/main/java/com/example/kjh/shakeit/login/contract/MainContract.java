package com.example.kjh.shakeit.login.contract;

import android.app.Activity;

import com.example.kjh.shakeit.api.ResultCallback;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.FirebaseAuth;

public interface MainContract {

    interface View {

        void showLoadingDialog();
        Activity getActivity();
        void hideLoadingDialog();
        CallbackManager getCallbackManager();
        FirebaseAuth getAuth();
        void showMessageForFailureLogin();
        void moveActivityWithUserInfo(String userInfo);

    }

    interface Presenter {

        void onClickFacebookLogin();
        void onClickGoogleLogin(GoogleSignInResult result);
        void autoLogin(int _id);
    }

    interface Model {

        void socialLogin(String email, String uid, String displayName, String photoUrl, String login_type, ResultCallback callback);
        void getUser(int id, ResultCallback callback);
    }

}
