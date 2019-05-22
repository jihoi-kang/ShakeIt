package com.example.kjh.shakeit.webrtc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 처리되지 않은 예외 처리기
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 21. PM 5:29
 **/
public class UnhandledExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "AppRTCMobileActivity";
    private final Activity activity;

    public UnhandledExceptionHandler(final Activity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(Thread unusedThread, Throwable e) {
        activity.runOnUiThread(() -> {
            String title = "Fatal error: " + getTopLevelCauseMessage(e);
            String msg = getRecursiveStackTrace(e);
            TextView errorView = new TextView(activity);
            errorView.setText(msg);
            errorView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
            ScrollView scrollingContainer = new ScrollView(activity);
            scrollingContainer.addView(errorView);
            Log.e(TAG, title + "\n\n" + msg);
            DialogInterface.OnClickListener listener = (dialog, which) -> {
                dialog.dismiss();
                System.exit(1);
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title)
                    .setView(scrollingContainer)
                    .setPositiveButton("Exit", listener)
                    .show();
        });
    }

    /** 원인 포함된 메시지 반환 */
    private static String getTopLevelCauseMessage(Throwable t) {
        Throwable topLevelCause = t;
        while (topLevelCause.getCause() != null) {
            topLevelCause = topLevelCause.getCause();
        }
        return topLevelCause.getMessage();
    }

    private static String getRecursiveStackTrace(Throwable t) {
        StringWriter writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
