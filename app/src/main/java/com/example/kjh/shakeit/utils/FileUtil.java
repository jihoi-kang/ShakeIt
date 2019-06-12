package com.example.kjh.shakeit.utils;

import android.os.Environment;

import java.io.File;

/**
 * 파일 관련 유틸리티
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 6. 12. PM 5:46
 **/
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
