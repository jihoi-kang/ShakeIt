package com.example.kjh.shakeit.login.presenter;

import android.support.annotation.NonNull;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.login.contract.MainContract;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;
    private MainContract.Model model;

    public MainPresenter(MainContract.View view, MainContract.Model model) {
        this.view = view;
        this.model = model;
    }

    /**------------------------------------------------------------------
     메서드 ==> 페이스북 로그인 클릭시 로직
     ------------------------------------------------------------------*/
    @Override
    public void onClickFacebookLogin() {
        final LoginManager loginManager = LoginManager.getInstance();

        loginManager.logInWithReadPermissions(view.getActivity(),
                Arrays.asList("public_profile", "email"));

        loginManager.registerCallback(view.getCallbackManager(), new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                saveAuth(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException error) {
                view.showMessageForFailureLogin();
            }
        });
    }

    /**------------------------------------------------------------------
     메서드 ==> 구글 로그인 클릭시 로직
     ------------------------------------------------------------------*/
    @Override
    public void onClickGoogleLogin(GoogleSignInResult result) {
        if(!result.isSuccess()) {
            view.showMessageForFailureLogin();
            return;
        }

        GoogleSignInAccount account = result.getSignInAccount();
        saveAuth(account);
    }

    /**------------------------------------------------------------------
     메서드 ==> 자동 로그인시 유저 정보를 받아옴
     ------------------------------------------------------------------*/
    @Override
    public void autoLogin(int _id) {
        view.showLoadingDialog();
        model.getUser(_id, new ResultCallback(){
            @Override
            public void onSuccess(String body) {
                view.hideLoadingDialog();
                view.moveActivityWithUserInfo(body);
            }

            @Override
            public void onFailure(String errorMsg) {
                view.hideLoadingDialog();
                view.showMessageForFailureLogin();
            }
        });
    }

    /**------------------------------------------------------------------
     메서드 ==> 로그인 자격 증명서 저장(페이스북, 구글)
     ------------------------------------------------------------------*/
    private void saveAuth(Object auth) {
        view.showLoadingDialog();
        AuthCredential credential = null;
        String login_type = "";
        if(auth instanceof GoogleSignInAccount){
            /** 구글 */
            GoogleSignInAccount googleSignInAccount = (GoogleSignInAccount)auth;
            credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
            login_type = "g";
        } else if(auth instanceof AccessToken) {
            /** 페이스북 */
            AccessToken accessToken = (AccessToken)auth;
            credential = FacebookAuthProvider.getCredential(accessToken.getToken());
            login_type = "f";
        }

        final String finalLogin_type = login_type;
        view.getAuth().signInWithCredential(credential)
                .addOnCompleteListener(view.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = view.getAuth().getCurrentUser();
                            model.socialLogin(user.getEmail(), user.getUid(), user.getDisplayName(), user.getPhotoUrl().toString(), finalLogin_type, new ResultCallback(){

                                @Override
                                public void onSuccess(String body) {
                                    view.hideLoadingDialog();
                                    view.moveActivityWithUserInfo(body);
                                }

                                @Override
                                public void onFailure(String errorMsg) {
                                    view.hideLoadingDialog();
                                    view.showMessageForFailureLogin();
                                }
                            });
                        } else {
                            view.hideLoadingDialog();
                            view.showMessageForFailureLogin();
                        }
                    }
                });

    }

}
