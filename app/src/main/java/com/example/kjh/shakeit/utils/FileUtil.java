package com.example.kjh.shakeit.utils;

import android.os.Environment;

import java.io.File;

public class FileUtil {

    public static File create() {
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ShakeIt";
        File folder = new File(sdPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // 현재 시간 기준 파일명 생성
        String now = TimeManager.now();
        String imageFileName = ShareUtil.getPreferInt("userId") + now + ".jpg";
        // 파일 생성
        File curFile = new File(sdPath, imageFileName);
        return curFile;
    }

}
