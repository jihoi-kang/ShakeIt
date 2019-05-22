package com.example.kjh.shakeit.webrtc;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import com.example.kjh.shakeit.webrtc.utils.AppRTCUtils;

import org.webrtc.ThreadUtils;

/**
 * 근접 센서와 관련된 부분 관리
 * NEAR 혹은 FAR만 반환
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 21. AM 2:38
 **/
public class AppRTCProximitySensor implements SensorEventListener {

    private static final String TAG = "AppRTCProximitySensor";

    // 오직 하나의 스레드에서 생성, 시작, 중지 시킨다
    private final ThreadUtils.ThreadChecker threadChecker = new ThreadUtils.ThreadChecker();

    private final Runnable onSensorStateListener;
    private final SensorManager sensorManager;
    private Sensor proximitySensor = null;
    private boolean lastStateReportIsNear = false;

    /** Construction */
    static AppRTCProximitySensor create(Context context, Runnable sensorStateListener) {
        return new AppRTCProximitySensor(context, sensorStateListener);
    }

    private AppRTCProximitySensor(Context context, Runnable sensorStateListener) {
        Log.d(TAG, "AppRTCProximitySensor" + AppRTCUtils.getThreadInfo());
        onSensorStateListener = sensorStateListener;
        sensorManager = ((SensorManager) context.getSystemService(Context.SENSOR_SERVICE));
    }

    /** 근접 센서 활성화 & 초기화 */
    public boolean start() {
        threadChecker.checkIsOnValidThread();
        Log.d(TAG, "start" + AppRTCUtils.getThreadInfo());
        if (!initDefaultSensor()) {
            // 근접센서를 지원하지 않는 디바이스
            return false;
        }
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        return true;
    }

    /** 근접 센서 비 활성화 */
    public void stop() {
        threadChecker.checkIsOnValidThread();
        Log.d(TAG, "stop" + AppRTCUtils.getThreadInfo());
        if (proximitySensor == null) {
            return;
        }
        sensorManager.unregisterListener(this, proximitySensor);
    }

    /** 마지막으로 보고된 상태 반환, NEAR시 True */
    public boolean sensorReportsNearState() {
        threadChecker.checkIsOnValidThread();
        return lastStateReportIsNear;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        threadChecker.checkIsOnValidThread();
        AppRTCUtils.assertIsTrue(sensor.getType() == Sensor.TYPE_PROXIMITY);
        if (accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Log.e(TAG, "The values returned by this sensor cannot be trusted");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        threadChecker.checkIsOnValidThread();
        AppRTCUtils.assertIsTrue(event.sensor.getType() == Sensor.TYPE_PROXIMITY);

        // 가능하면 이 메소드 내에서 수행하고 블로킹을 피하세요
        float distanceInCentimeters = event.values[0];
        if (distanceInCentimeters < proximitySensor.getMaximumRange()) {
            Log.d(TAG, "Proximity sensor => NEAR state");
            lastStateReportIsNear = true;
        } else {
            Log.d(TAG, "Proximity sensor => FAR state");
            lastStateReportIsNear = false;
        }

        // 수신 대기중인 사용자에 대한 새 상태 보고
        // sensorReportsNearState()을 통해 현재 상태 확인(NEAR or FAR)

        if (onSensorStateListener != null) {
            onSensorStateListener.run();
        }

        Log.d(TAG, "onSensorChanged" + AppRTCUtils.getThreadInfo() + ": "
                + "accuracy=" + event.accuracy + ", timestamp=" + event.timestamp + ", distance="
                + event.values[0]);
    }

    /** 기본 근접 센서가 가능하면 True */
    private boolean initDefaultSensor() {
        if (proximitySensor != null) {
            return true;
        }
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (proximitySensor == null) {
            return false;
        }
        logProximitySensorInfo();
        return true;
    }

    /** 근접 센서에 대한 정보 */
    private void logProximitySensorInfo() {
        if (proximitySensor == null) {
            return;
        }
        StringBuilder info = new StringBuilder("Proximity sensor: ");
        info.append("name=").append(proximitySensor.getName());
        info.append(", vendor: ").append(proximitySensor.getVendor());
        info.append(", power: ").append(proximitySensor.getPower());
        info.append(", resolution: ").append(proximitySensor.getResolution());
        info.append(", max range: ").append(proximitySensor.getMaximumRange());
        info.append(", min delay: ").append(proximitySensor.getMinDelay());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            // Added in API level 20.
            info.append(", type: ").append(proximitySensor.getStringType());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Added in API level 21.
            info.append(", max delay: ").append(proximitySensor.getMaxDelay());
            info.append(", reporting mode: ").append(proximitySensor.getReportingMode());
            info.append(", isWakeUpSensor: ").append(proximitySensor.isWakeUpSensor());
        }
        Log.d(TAG, info.toString());
    }
}
