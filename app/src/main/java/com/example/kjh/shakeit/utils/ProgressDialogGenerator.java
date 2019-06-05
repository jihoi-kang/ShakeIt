package com.example.kjh.shakeit.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * ProgressDialog를 발생시키는 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 26. PM 5:55
 **/
public class ProgressDialogGenerator {

    public static ProgressDialog create(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        return progressDialog;
    }

}
