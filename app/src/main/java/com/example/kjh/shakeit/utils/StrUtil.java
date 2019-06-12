package com.example.kjh.shakeit.utils;

/**
 * 문자열 유틸리티
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 6. 12. PM 5:49
 **/
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
