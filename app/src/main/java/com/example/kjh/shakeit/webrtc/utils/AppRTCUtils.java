package com.example.kjh.shakeit.webrtc.utils;

import android.os.Build;
import android.util.Log;

/**
 * 쓰레드를 안전하게 관리하게 돕는 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 20. PM 9:28
 **/
public class AppRTCUtils {

    private AppRTCUtils() {
    }

    /** False 일때 어설션 에러 */
    public static void assertIsTrue(boolean condition) {
        if(!condition)
            throw new AssertionError("Expected condition to be true");
    }

    /** 쓰레드 정보 반환 */
    public static String getThreadInfo() {
        return "@[name=" + Thread.currentThread().getName() + ", id=" + Thread.currentThread().getId() + "]";
    }

    public static void logDeviceInfo(String tag) {
        Log.d(tag, "Android SDK: " + Build.VERSION.SDK_INT + ", "
                + "Release: " + Build.VERSION.RELEASE + ", "
                + "Brand: " + Build.BRAND + ", "
                + "Device: " + Build.DEVICE + ", "
                + "Id: " + Build.ID + ", "
                + "Hardware: " + Build.HARDWARE + ", "
                + "Manufacturer: " + Build.MANUFACTURER + ", "
                + "Model: " + Build.MODEL + ", "
                + "Product: " + Build.PRODUCT);
    }
}
