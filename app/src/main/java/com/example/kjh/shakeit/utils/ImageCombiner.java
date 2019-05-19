package com.example.kjh.shakeit.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * 채팅방 대표사진 결합 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 18. PM 5:12
 **/
public class ImageCombiner {

    /**------------------------------------------------------------------
     메서드 ==> 사진 2개 결합
     ------------------------------------------------------------------*/
    public static Bitmap combine(Bitmap c, Bitmap s) {
        Bitmap cs = null;
        int width = (c.getWidth() + s.getWidth()) / 2;
        int height = (c.getHeight() + s.getHeight()) / 2;

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        c = centerCrop(c);
        s = centerCrop(s);

        c = Bitmap.createBitmap(c,
                c.getWidth() / 4,
                0,
                c.getWidth() / 2,
                c.getHeight());
        c = Bitmap.createScaledBitmap(c, width / 2, height, false);
        s = Bitmap.createScaledBitmap(s, width / 2, height, false);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth() + 6, 0f, null);

        return cs;
    }

    /**------------------------------------------------------------------
     메서드 ==> 사진 3개 결합
     ------------------------------------------------------------------*/
    public static Bitmap combine(Bitmap c, Bitmap s, Bitmap p) {
        Bitmap cs = null;
        int width = (c.getWidth() + s.getWidth() + p.getWidth()) / 3;
        int height = (c.getHeight() + s.getHeight() + p.getHeight()) / 3;

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        c = centerCrop(c);
        s = centerCrop(s);
        p = centerCrop(p);

        c = Bitmap.createBitmap(c,
                c.getWidth() / 4,
                0,
                c.getWidth() / 2,
                c.getHeight());
        c = Bitmap.createScaledBitmap(c, width/2, height, false);
        s = Bitmap.createScaledBitmap(s, width/2, height / 2, false);
        p = Bitmap.createScaledBitmap(p, width/2, height / 2, false);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth() + 6, 0, null);
        comboImage.drawBitmap(p,c.getWidth() + 6, s.getHeight() + 6, null);

        return cs;
    }

    /**------------------------------------------------------------------
     메서드 ==> 사진 4개 결합
     ------------------------------------------------------------------*/
    public static Bitmap combine(Bitmap c, Bitmap q, Bitmap s, Bitmap p) {
        Bitmap cs = null;
        int width = (c.getWidth() + s.getWidth() + p.getWidth() + q.getWidth()) / 4;
        int height = (c.getHeight() + s.getHeight() + p.getHeight() + q.getHeight()) / 4;

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        c = centerCrop(c);
        s = centerCrop(s);
        p = centerCrop(p);
        q = centerCrop(q);

        c = Bitmap.createBitmap(c,
                c.getWidth() / 4,
                0,
                c.getWidth() / 4,
                c.getHeight());
        c = Bitmap.createScaledBitmap(c, width / 2, height / 2, false);
        q = Bitmap.createScaledBitmap(q, width / 2, height / 2, false);
        s = Bitmap.createScaledBitmap(s, width / 2, height / 2, false);
        p = Bitmap.createScaledBitmap(p, width / 2, height / 2, false);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(q, 0f, c.getHeight() + 6, null);
        comboImage.drawBitmap(s, c.getWidth() + 6, 0, null);
        comboImage.drawBitmap(p,c.getWidth() + 6, s.getHeight() + 6, null);

        return cs;
    }

    private static Bitmap centerCrop(Bitmap srcBmp){
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){
            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        } else {
            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }
        return dstBmp;
    }


}