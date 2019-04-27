package com.example.kjh.shakeit.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Soft Keyboard 관련 돕는 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 26. PM 5:54
 **/
public class KeyboardManager {

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
