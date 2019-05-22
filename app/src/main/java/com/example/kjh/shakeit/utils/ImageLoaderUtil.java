package com.example.kjh.shakeit.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kjh.shakeit.R;

import java.io.InputStream;
import java.net.URL;

public class ImageLoaderUtil {

    public static void display(Context context, ImageView imageView, String url) {
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context).load(url)
                .apply(new RequestOptions().placeholder(R.drawable.ic_image_loading))
                .into(imageView);
    }

    public static Bitmap getBitmap(String url) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
        } catch (Exception e) {}
        bitmap = Bitmap.createScaledBitmap(bitmap,500,500,true);

        return bitmap;
    }

}
