package com.example.kjh.shakeit.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kjh.shakeit.R;

public class ImageLoaderUtil {

    public static void display(Context context, ImageView imageView, String url) {
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context).load(url)
                .apply(new RequestOptions().placeholder(R.drawable.ic_image_loading))
                .into(imageView);
    }

}
