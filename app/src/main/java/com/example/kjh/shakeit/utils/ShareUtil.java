package com.example.kjh.shakeit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static void setPreferBool(String key) {
        sharedPreferences.edit().putBoolean(key, true).commit();
    }
    public static void setPreferInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).commit();
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
