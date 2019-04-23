package com.example.kjh.shakeit.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogGenerator {

    public static ProgressDialog show(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("");
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        return progressDialog;
    }

}
