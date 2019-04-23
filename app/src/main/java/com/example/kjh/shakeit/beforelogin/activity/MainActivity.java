package com.example.kjh.shakeit.beforelogin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.beforelogin.contract.MainContract;
import com.example.kjh.shakeit.beforelogin.presenter.MainPresenter;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.utils.Injector;
import com.example.kjh.shakeit.utils.Serializer;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.kjh.shakeit.Statics.REQUEST_CODE_MAIN_BEFORE_LOGIN_TO_EMAILLOGIN;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private MainContract.Presenter presenter;

    Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_before_login);

        unbinder = ButterKnife.bind(this);

        presenter = new MainPresenter(this, Injector.provideBeforeLoginMainModel());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.signUp)
    void onClickSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.emailLogin)
    void onClickEmailLogin() {
        Intent intent = new Intent(this, EmailLoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MAIN_BEFORE_LOGIN_TO_EMAILLOGIN);
    }

    @OnClick(R.id.facebookLogin)
    void onClickFacebookLogin() {

    }

    @OnClick(R.id.googleLogin)
    void onClickgoogleLogin() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;

        switch (requestCode){
            case REQUEST_CODE_MAIN_BEFORE_LOGIN_TO_EMAILLOGIN:
                User user = Serializer.deserialize(data.getStringExtra("user"), User.class);
//                ArrayList<User> userArray = new ArrayList<>();
//                userArray.add(user);

                Intent intent = new Intent(MainActivity.this, com.example.kjh.shakeit.main.MainActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
        }

    }
}
