package com.example.kjh.shakeit.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastGenerator {
    public static void show(Context context, int redId) {
        Toast.makeText(context, redId, Toast.LENGTH_SHORT).show();
    }
}
