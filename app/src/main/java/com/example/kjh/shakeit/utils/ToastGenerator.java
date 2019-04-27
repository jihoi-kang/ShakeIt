package com.example.kjh.shakeit.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast를 발생시키는 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 26. PM 5:57
 **/
public class ToastGenerator {

    public static void show(Context context, int redId) {
        Toast.makeText(context, redId, Toast.LENGTH_SHORT).show();
    }

}
