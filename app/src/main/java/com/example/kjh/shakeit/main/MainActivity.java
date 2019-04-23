package com.example.kjh.shakeit.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.data.User;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_after_login);

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");
        Log.d(TAG, user.getEmail());

        Toast.makeText(this, "" + user.getEmail(), Toast.LENGTH_SHORT).show();
    }
}
