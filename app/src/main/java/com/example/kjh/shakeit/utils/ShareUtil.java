package com.example.kjh.shakeit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.kjh.shakeit.data.User;

/**
 * SharedPreferences 유틸리티
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 6. 12. PM 5:48
 **/
public class ShareUtil {

    static SharedPreferences sharedPreferences;

    public static void init(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void clear() {
        sharedPreferences.edit().clear().commit();
    }

    /** Setter */
    public static void setPreferStr(String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }
    public static void setPreferBool(String key, boolean condition) {
        sharedPreferences.edit().putBoolean(key, condition).commit();
    }
    public static void setPreferInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).commit();
    }
    public static void setPreferUser(User user) {
        setPreferInt("userId", user.getUserId());
        setPreferStr("email", user.getEmail());
        setPreferStr("loginType", user.getLoginType());
        setPreferStr("name", user.getName());
        setPreferStr("imageUrl", user.getImageUrl());
        setPreferStr("statusMessage", user.getStatusMessage());
        setPreferInt("cash", user.getCash());
    }

    /** Getter */
    public static String getPreferStr(String key) {
        return sharedPreferences.getString(key, "");
    }
    public static boolean getPreferBool(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
    public static int getPreferInt(String key) {
        return sharedPreferences.getInt(key,-1);
    }

}
