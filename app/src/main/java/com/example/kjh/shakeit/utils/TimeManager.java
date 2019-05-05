package com.example.kjh.shakeit.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 시간 관련 관리 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 5. PM 7:30
 **/
public class TimeManager {

    public static String now() {
        SimpleDateFormat format_for_save = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.KOREA);
        long time_mil = System.currentTimeMillis();
        Date date = new Date(time_mil);
        String result = format_for_save.format(date);
        return result;
    }

}
