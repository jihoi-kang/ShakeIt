package com.example.kjh.shakeit.utils;

public class StrUtil {

    public static boolean isBlank(String str) {
        if (null != str) {
            if (str.trim().length() == 0) {
                return true;
            } else {
                if (str.equalsIgnoreCase("null")) {
                    return true;
                }
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
