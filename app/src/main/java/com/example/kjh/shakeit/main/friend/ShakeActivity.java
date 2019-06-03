package com.example.kjh.shakeit.main.friend;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.app.AppManager;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.friend.contract.ShakeContract;
import com.example.kjh.shakeit.main.friend.presenter.ShakePresenter;
import com.example.kjh.shakeit.utils.ImageLoaderUtil;
import com.example.kjh.shakeit.utils.Injector;
import com.example.kjh.shakeit.utils.StrUtil;
import com.example.kjh.shakeit.utils.ToastGenerator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_SHAKE_TO_PROFILE_DETAIL;

public class ShakeActivity extends AppCompatActivity implements SensorEventListener, ShakeContract.View {

    private final static String TAG = ShakeActivity.class.getSimpleName();

    private final static int SHAKE_SKIP_TIME = 1000;

    private ShakeContract.Presenter presenter;

    private SensorManager sensorManager;
    private Sensor sensor;

    private Vibrator vibrator;

    private User user, targetUserInfo;

    private boolean isConfirm = false;
    private boolean isShaking = false;

    private Unbinder unbinder;
    @BindView(R.id.shake_before) TextView shakeBefore;
    @BindView(R.id.shaking) LinearLayout shaking;
    @BindView(R.id.profile_image) ImageView profileImage;
    @BindView(R.id.name) TextView nameTxt;
    @BindView(R.id.info_layout) LinearLayout infoLayout;

    public static Handler shakeActHandler;
    private Thread thread = null;

    private long temp_time = 0;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);

        presenter = new ShakePresenter(this, Injector.provideShakeModel());
        presenter.onCreate();

        unbinder = ButterKnife.bind(this);

        user = (User) getIntent().getSerializableExtra("user");

        /** 가속도를 측정하는 센서 */
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        /** 진동 */
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        showTipDialog();

        shakeActHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                /** Netty에서 관련 메시지 왔고 변경 */
                User userInfo = (User) msg.obj;

                showUserInfo(userInfo);
                setIsShaking(false);
                thread.interrupt();
                thread = null;

                temp_time = System.currentTimeMillis();
                showMessageForFound();
            }
        };
    }

    /**------------------------------------------------------------------
     생명주기 ==> onResume()
     ------------------------------------------------------------------*/
    @Override
    protected void onResume() {
        super.onResume();
        /** 팁 다이어로그에서 확인을 누른 이후부터 Sensor 동작할 수 있음 */
        if(isConfirm) {
            sensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    /**------------------------------------------------------------------
     생명주기 ==> onPause()
     ------------------------------------------------------------------*/
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    protected void onDestroy() {
        AppManager.getAppManager().removeActivity(this);

        super.onDestroy();

        /** 흔들기 중 종료할 때 */
        if(thread != null) {
            thread.interrupt();
            presenter.offShake();
        }

        presenter.onDestroy();
        unbinder.unbind();
    }

    /**------------------------------------------------------------------
     콜백이벤트 ==> onActivityResult()
     ------------------------------------------------------------------*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;

        /** ProfileDetailActivity에서 1:1채팅하기 누를 때 */
        if(requestCode == REQUEST_CODE_SHAKE_TO_PROFILE_DETAIL) {
            Intent intent = new Intent();
            User friend = (User) data.getSerializableExtra("friend");
            intent.putExtra("friend", friend);
            setResult(RESULT_OK, intent);
            finish();
        }

    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 뒤로가기
     ------------------------------------------------------------------*/
    @OnClick(R.id.back)
    void onClickBack() {
        finish();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 찾은 친구 프로필
     ------------------------------------------------------------------*/
    @OnClick(R.id.info_layout)
    void onClickProfile() {
        presenter.onClickProfile();
    }

    @Override
    public boolean getIsShaking() {
        return isShaking;
    }

    /**------------------------------------------------------------------
     메서드 ==> 1초 동안 진동
     ------------------------------------------------------------------*/
    @Override
    public void executeVibrate() {
        vibrator.vibrate(1000);
    }

    @Override
    public void setIsShaking(boolean isShaking) {
        this.isShaking = isShaking;

        if(isShaking) {
            shakeBefore.setVisibility(View.GONE);
            shaking.setVisibility(View.VISIBLE);
        } else {
            shakeBefore.setVisibility(View.VISIBLE);
            shaking.setVisibility(View.GONE);
        }
    }

    /**------------------------------------------------------------------
     메서드 ==> 흔든 친구를 찾는 시간 10초로 제한
     ------------------------------------------------------------------*/
    @Override
    public void executeThread() {
        thread = new Thread(() -> {
            try {
                Thread.sleep(10000);
                // 10초동안 친구를 찾지 못했을 때
                runOnUiThread(() -> {
                    setIsShaking(false);
                    showMessageForNotFound();
                    presenter.offShake();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void showMessageForNotFound() {
        ToastGenerator.show(R.string.msg_for_not_found);
    }

    @Override
    public void showMessageForFound() {
        ToastGenerator.show(R.string.msg_for_found);
    }

    @Override
    public void showUserInfo(User userInfo) {
        targetUserInfo = userInfo;

        infoLayout.setBackgroundResource(R.drawable.shape_round_edge_back_light_gray);

        nameTxt.setText("" + userInfo.getName());

        if(StrUtil.isBlank(userInfo.getImageUrl()))
            profileImage.setImageResource(R.drawable.ic_basic_profile);
        else
            ImageLoaderUtil.display(this, profileImage, userInfo.getImageUrl());
    }

    @Override
    public void hideUserInfo() {
        targetUserInfo = null;

        infoLayout.setBackground(null);
        nameTxt.setText("");
        profileImage.setImageDrawable(null);
    }

    @Override
    public String getNameTxt() {
        return nameTxt.getText().toString();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public User getTargetUser() {
        return targetUserInfo;
    }

    /**------------------------------------------------------------------
     메서드 ==> 가속도 센서로 흔들기 변화 감지
     ------------------------------------------------------------------*/
    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis();
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && currentTime > temp_time + SHAKE_SKIP_TIME) {
            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];

            float gravityX = axisX / SensorManager.GRAVITY_EARTH;
            float gravityY = axisY / SensorManager.GRAVITY_EARTH;
            float gravityZ = axisZ / SensorManager.GRAVITY_EARTH;

            Float f = gravityX * gravityX + gravityY * gravityY + gravityZ * gravityZ;
            double squaredD = Math.sqrt(f.doubleValue());
            float gForce = (float) squaredD;

            presenter.onSensorChanged(gForce);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    /**------------------------------------------------------------------
     메서드 ==> 흔들기 TIP 보여주기
     ------------------------------------------------------------------*/
    private void showTipDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_shake_tip);
        Button button = dialog.findViewById(R.id.confirm);
        button.setOnClickListener(view -> {
            dialog.dismiss();
            sensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
            isConfirm = true;
        });
        dialog.show();
    }
}
