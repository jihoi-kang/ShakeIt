package com.example.kjh.shakeit.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeManager {

    public static String now() {
        SimpleDateFormat format_for_save = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.KOREA);
        long time_mil = System.currentTimeMillis();
        Date date = new Date(time_mil);
        String result = format_for_save.format(date);
        return result;
    }

}
