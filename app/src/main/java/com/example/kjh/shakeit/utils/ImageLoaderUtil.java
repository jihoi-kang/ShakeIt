package com.example.kjh.shakeit.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kjh.shakeit.R;

import java.io.InputStream;
import java.net.URL;

/**
 * 이미지 관련 유틸리티
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 6. 12. PM 5:47
 **/
public class ImageLoaderUtil {

    public static void display(Context context, ImageView imageView, String url) {
        if (imageView == null)
            throw new IllegalArgumentException("argument error");

        Glide.with(context).load(url)
                .apply(new RequestOptions().placeholder(R.drawable.ic_image_loading))
                .into(imageView);
    }

    public static Bitmap getBitmap(String url) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
        } catch (Exception e) {}

        /** 비율 맞추기 위해 */
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int convertHeight = (height * 500) / width;

        bitmap = Bitmap.createScaledBitmap(bitmap,500, convertHeight,true);

        return bitmap;
    }

    /**------------------------------------------------------------------
     메서드 ==> 디스플레이 2 / 3의 width만큼 너비 Bitmap
     ------------------------------------------------------------------*/
    public static Bitmap getBitmap(String url, Point size) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
        } catch (Exception e) {}

        /** 비율 맞추기 위해 */
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int convertHeight = (height * ((2 * size.x) / 3)) / width;

        bitmap = Bitmap.createScaledBitmap(bitmap,(2 * size.x) / 3, convertHeight,true);

        return bitmap;
    }

}
