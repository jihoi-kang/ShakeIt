package com.example.kjh.shakeit.main.friend;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.utils.ImageLoaderUtil;
import com.example.kjh.shakeit.utils.ToastGenerator;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ImageDetailActivity extends AppCompatActivity {

    private Unbinder unbinder;
    @BindView(R.id.imageView) SubsamplingScaleImageView imageView;
    @BindView(R.id.download) ImageView download;

    Bitmap bitmap;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        unbinder = ButterKnife.bind(this);

        // 이미지 셋팅
        new Thread(() -> {
            bitmap = ImageLoaderUtil.getBitmap(getIntent().getStringExtra("imageUrl"));
            runOnUiThread(() -> imageView.setImage(ImageSource.bitmap(bitmap)));
        }).start();

    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 이미지 다운로드
     ------------------------------------------------------------------*/
    @OnClick(R.id.download)
    void onClickDownload() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", null);

        ToastGenerator.show(R.string.msg_download_success);
    }
}
