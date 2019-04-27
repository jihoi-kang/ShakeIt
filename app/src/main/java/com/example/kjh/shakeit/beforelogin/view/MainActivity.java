package com.example.kjh.shakeit.beforelogin.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.beforelogin.contract.MainContract;
import com.example.kjh.shakeit.beforelogin.presenter.MainPresenter;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.utils.Injector;
import com.example.kjh.shakeit.utils.ProgressDialogGenerator;
import com.example.kjh.shakeit.utils.Serializer;
import com.example.kjh.shakeit.utils.ToastGenerator;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.kjh.shakeit.etc.Statics.REQUEST_CODE_FACEBOOK_LOGIN;
import static com.example.kjh.shakeit.etc.Statics.REQUEST_CODE_GOOGLE_LOGIN;
import static com.example.kjh.shakeit.etc.Statics.REQUEST_CODE_MAIN_BEFORE_LOGIN_TO_EMAILLOGIN;

/**
 * 로그인 전 메인 액티비티 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 24. PM 3:35
 **/
public class MainActivity extends AppCompatActivity implements MainContract.View {

    private String TAG = MainActivity.class.getSimpleName();

    private MainContract.Presenter presenter;

    private Unbinder unbinder;

    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog progressDialog;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private User user = null;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_before_login);

        unbinder = ButterKnife.bind(this);

        presenter = new MainPresenter(this, Injector.provideBeforeLoginMainModel());

        preferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        editor = preferences.edit();

        /** 소셜 로그인 초기화 */
        mAuth = FirebaseAuth.getInstance();
        /** 페이스북 */
        mCallbackManager = CallbackManager.Factory.create();
        /** 구글 */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        showMessageForFailureLogin();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
//        reset();

    }

    private void reset(){
        editor.clear();
        editor.commit();
        FirebaseAuth.getInstance().signOut();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onResume()
     ------------------------------------------------------------------*/
    @Override
    protected void onResume() {
        super.onResume();

        /** 자동로그인 여부 확인 */
        if(preferences.getInt("_id", 0) == 0)
            return;

        presenter.autoLogin(preferences.getInt("_id", 0));
    }

    /**------------------------------------------------------------------
     생명주기 ==> onStop()
     ------------------------------------------------------------------*/
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();

        if(user != null) {
            editor.putString("login_type", user.getLogin_type());
            editor.putInt("_id", user.get_id());
            editor.commit();
        }
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 이메일로 가입
     ------------------------------------------------------------------*/
    @OnClick(R.id.signUp)
    void onClickSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 이메일로 로그인
     ------------------------------------------------------------------*/
    @OnClick(R.id.emailLogin)
    void onClickEmailLogin() {
        Intent intent = new Intent(this, EmailLoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MAIN_BEFORE_LOGIN_TO_EMAILLOGIN);
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 페이스북 로그인
     ------------------------------------------------------------------*/
    @OnClick(R.id.facebookLogin)
    void onClickFacebookLogin() {
        presenter.onClickFacebookLogin();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 구글 로그인
     ------------------------------------------------------------------*/
    @OnClick(R.id.googleLogin)
    void onClickgoogleLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_LOGIN);
    }

    @Override
    public void showLoadingDialog() {
        progressDialog = ProgressDialogGenerator.show(this, "잠시만 기다려주세요");
        progressDialog.show();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void hideLoadingDialog() {
        progressDialog.dismiss();
    }

    @Override
    public CallbackManager getCallbackManager() {
        return mCallbackManager;
    }

    @Override
    public FirebaseAuth getAuth() {
        return mAuth;
    }

    @Override
    public void showMessageForFailureLogin() {
        ToastGenerator.show(this, R.string.msg_for_failure_login);
    }

    /**------------------------------------------------------------------
     메서드 ==> 로그인 후 화면 이동
     ------------------------------------------------------------------*/
    @Override
    public void moveActivityWithUserInfo(String userInfo) {
        user = Serializer.deserialize(userInfo, User.class);

        Intent intent = new Intent(MainActivity.this, com.example.kjh.shakeit.main.view.MainActivity.class);
        intent.putExtra("user", user);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;

        switch (requestCode){
            /** From 이메일 로그인 화면 */
            case REQUEST_CODE_MAIN_BEFORE_LOGIN_TO_EMAILLOGIN:
                moveActivityWithUserInfo(data.getStringExtra("user"));
                break;
            case REQUEST_CODE_FACEBOOK_LOGIN:
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
                break;
            case REQUEST_CODE_GOOGLE_LOGIN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                presenter.onClickGoogleLogin(result);
                break;
            default:
                break;
        }

    }

}