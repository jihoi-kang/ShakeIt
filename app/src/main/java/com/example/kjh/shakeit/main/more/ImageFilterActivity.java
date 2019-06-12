package com.example.kjh.shakeit.main.more;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.app.AppManager;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

/**
 * 이미지 필터 씌우는 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 6. 12. PM 5:40
 **/
public class ImageFilterActivity extends AppCompatActivity {

    private final String TAG = ImageFilterActivity.class.getSimpleName();

    private Unbinder unbinder;
    @BindView(R.id.profile_image) ImageView profileImage;
    @BindView(R.id.originalImage) ImageView originalImage;
    @BindView(R.id.grayImage) ImageView grayImage;
    @BindView(R.id.blurImage) ImageView blurImage;
    @BindView(R.id.contrastImage) ImageView contrastImage;
    @BindView(R.id.brightnessImage) ImageView brightnessImage;

    private Mat matInput = new Mat();
    private Mat matResult = new Mat();

    private Bitmap originalBitmap, grayBitmap, blurBitmap, contrastBitmap, brightBitmap;
    private Intent intent;

    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);
    public native void ConvertToBlur(long matAddrInput, long matAddrResult);
    public native void ConvertToContrast(long matAddrInput, long matAddrResult);
    public native void ConvertToBrightness(long matAddrInput, long matAddrResult);

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_filter);

        unbinder = ButterKnife.bind(this);

        intent = getIntent();

        /** 이미지 셋팅 */
        originalBitmap = BitmapFactory.decodeFile(intent.getStringExtra("path"));
        // 사진찍어서 넘어온 경우 각도가 다를 수 있음 => 맞춰주기
        switch(intent.getIntExtra("orientation", 0)) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                originalBitmap = rotateImage(originalBitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                originalBitmap = rotateImage(originalBitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                originalBitmap = rotateImage(originalBitmap, 270);
                break;
            default: break;
        }
        /** 이미지 셋팅(원본 이미지부터 필터 이미지 까지) */
        // 원본
        profileImage.setImageBitmap(originalBitmap);
        originalImage.setImageBitmap(originalBitmap);
        Utils.bitmapToMat(originalBitmap.copy(Bitmap.Config.ARGB_8888, true), matInput);

        // 흑백
        ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
        grayBitmap = Bitmap.createBitmap(matResult.cols(), matResult.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matResult, grayBitmap);
        grayImage.setImageBitmap(grayBitmap);

        // 흐림
        ConvertToBlur(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
        blurBitmap = Bitmap.createBitmap(matResult.cols(), matResult.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matResult, blurBitmap);
        blurImage.setImageBitmap(blurBitmap);

        // 대비
        ConvertToContrast(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
        contrastBitmap = Bitmap.createBitmap(matResult.cols(), matResult.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matResult, contrastBitmap);
        contrastImage.setImageBitmap(contrastBitmap);

        // 밝기
        ConvertToBrightness(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
        brightBitmap = Bitmap.createBitmap(matResult.cols(), matResult.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matResult, brightBitmap);
        brightnessImage.setImageBitmap(brightBitmap);

    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    protected void onDestroy() {
        AppManager.getAppManager().removeActivity(this);

        super.onDestroy();
        unbinder.unbind();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 닫기
     ------------------------------------------------------------------*/
    @OnClick(R.id.close)
    void onClickClose() {
        finish();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 적용
     ------------------------------------------------------------------*/
    @OnClick(R.id.apply)
    void onClickApply() {
        Bitmap bitmap = ((BitmapDrawable)profileImage.getDrawable()).getBitmap();
        Uri uri = getImageUri(this, bitmap);

        Intent intent = new Intent();
        intent.putExtra("uri", uri);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 필터 씌우기
     ------------------------------------------------------------------*/
    @OnClick({R.id.original, R.id.gray, R.id.blur, R.id.contrast, R.id.brightness})
    void onClickFilter(View view) {
        switch (view.getId()) {
            case R.id.original:
                profileImage.setImageBitmap(originalBitmap);
                break;
            case R.id.gray:
                profileImage.setImageBitmap(grayBitmap);
                break;
            case R.id.blur:
                profileImage.setImageBitmap(blurBitmap);
                break;
            case R.id.contrast:
                profileImage.setImageBitmap(contrastBitmap);
                break;
            case R.id.brightness:
                profileImage.setImageBitmap(brightBitmap);
                break;
        }

    }

    /**------------------------------------------------------------------
     메서드 ==> Bitmap 파일 저장 및 URI 반환
     ------------------------------------------------------------------*/
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
