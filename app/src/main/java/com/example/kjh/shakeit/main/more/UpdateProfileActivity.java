package com.example.kjh.shakeit.main.more;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.app.AppManager;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.more.contract.UpdateProfileContract;
import com.example.kjh.shakeit.main.more.presenter.UpdateProfilePresenter;
import com.example.kjh.shakeit.utils.ImageLoaderUtil;
import com.example.kjh.shakeit.utils.Injector;
import com.example.kjh.shakeit.utils.KeyboardManager;
import com.example.kjh.shakeit.utils.ProgressDialogGenerator;
import com.example.kjh.shakeit.utils.StrUtil;
import com.example.kjh.shakeit.utils.TimeManager;
import com.example.kjh.shakeit.utils.ToastGenerator;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rebus.bottomdialog.BottomDialog;

import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_CAMERA;
import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_GALLERY;
import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_UPDATE_PROFILE_TO_IMAGE_FILTER;

public class UpdateProfileActivity extends AppCompatActivity implements UpdateProfileContract.View, TextWatcher {

    private final String TAG = UpdateProfileActivity.class.getSimpleName();

    private UpdateProfileContract.Presenter presenter;

    private User user;

    private ProgressDialog dialog;

    private File file;
    private boolean isChangedProfileImage = false;
    private String path;
    private int tempOrientation = 0;

    private Unbinder unbinder;
    @BindView(R.id.update) TextView update;
    @BindView(R.id.inputName) EditText inputName;
    @BindView(R.id.inputStatusMessage) EditText inputStatusMessage;
    @BindView(R.id.profile_image) ImageView profileImage;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        unbinder = ButterKnife.bind(this);

        presenter = new UpdateProfilePresenter(this, Injector.provideUpdateProfileModel());

        /** Input 변화 리스너 */
        inputName.addTextChangedListener(this);
        inputStatusMessage.addTextChangedListener(this);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        /** 프로필 이미지 path */
        path = user.getImageUrl();

        /** 초기값 */
        inputName.setText(user.getName());

        if(StrUtil.isNotBlank(user.getStatusMessage()))
            inputStatusMessage.setText(user.getStatusMessage());

        if(StrUtil.isBlank(user.getImageUrl()))
            profileImage.setImageResource(R.drawable.ic_basic_profile);
        else
            ImageLoaderUtil.display(this, profileImage, user.getImageUrl());


    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    protected void onDestroy() {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
        unbinder.unbind();

        /** 파일 삭제 */
        File file = new File(path);
        if(file.exists())
            file.delete();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 화면 닫기
     ------------------------------------------------------------------*/
    @OnClick(R.id.back)
    void onClickBack() {
        finish();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 프로필 이미지 변경
     ------------------------------------------------------------------*/
    @OnClick(R.id.update_profile_image)
    void onClickUpdateProfileImage() {
        presenter.onClickProfileImage();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 수정
     ------------------------------------------------------------------*/
    @OnClick(R.id.update)
    void onClickUpdate() {
        presenter.onClickUpdate();
    }

    /**------------------------------------------------------------------
     콜백이벤트 ==> onActivityResult()
     ------------------------------------------------------------------*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            /** 갤러리에서 선택한 후 */
            case REQUEST_CODE_GALLERY:
                path = uriToPath(data.getData());

                Intent toImageFilterGallery = new Intent(UpdateProfileActivity.this, ImageFilterActivity.class);
                toImageFilterGallery.putExtra("path", path);
                startActivityForResult(toImageFilterGallery, REQUEST_CODE_UPDATE_PROFILE_TO_IMAGE_FILTER);

                break;
            /** 카메라에서 찍은 후 */
            case REQUEST_CODE_CAMERA:
                Intent media_scan_intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                media_scan_intent.setData(Uri.fromFile(file));
                sendBroadcast(media_scan_intent);

                try {
                    ExifInterface ei = new ExifInterface(file.getAbsolutePath());
                    tempOrientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Crop.of(Uri.fromFile(file), Uri.fromFile(file)).asSquare().start(getActivity());
                break;
            /** 이미지를 크롭한 후 */
            case Crop.REQUEST_CROP:
                path = file.getAbsolutePath();

                Intent toImageFilterCamera = new Intent(UpdateProfileActivity.this, ImageFilterActivity.class);
                toImageFilterCamera.putExtra("path", path);
                toImageFilterCamera.putExtra("orientation", tempOrientation);
                startActivityForResult(toImageFilterCamera, REQUEST_CODE_UPDATE_PROFILE_TO_IMAGE_FILTER);
                break;
            /** 필터작업 까지 다 마친 후 */
            case REQUEST_CODE_UPDATE_PROFILE_TO_IMAGE_FILTER:
                path = uriToPath(data.getParcelableExtra("uri"));

                ImageLoaderUtil.display(this, profileImage, path);
                isChangedProfileImage = true;

                presenter.onChangedInput();
                break;
        }

    }

    @Override
    public void showLoadingDialog() {
        dialog = ProgressDialogGenerator.show(this, "잠시만 기다려주세요");
        dialog.show();
    }

    @Override
    public void hideLoadingDialog() {
        dialog.dismiss();
    }

    @Override
    public void showMessageForFailureUpload(String errorMsg) {
        ToastGenerator.show(R.string.msg_for_failure_upload);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void hideSoftKeyboard() {
        KeyboardManager.hideKeyboard(this, inputName);
        KeyboardManager.hideKeyboard(this, inputStatusMessage);
    }

    /**------------------------------------------------------------------
     메서드 ==> 프로필 이미지 변경 선택시 어떤 방법으로 변경할지 유형 정하기
     ------------------------------------------------------------------*/
    @Override
    public void showSelectType() {
        BottomDialog dialog = new BottomDialog(this);
        dialog.canceledOnTouchOutside(true);
        dialog.cancelable(true);
        dialog.inflateMenu(R.menu.menu_main);
        dialog.setOnItemSelectedListener(id -> {
            switch (id) {
                /** 카메라 선택 */
                case R.id.action_camera:
                    file = createFile();
                    presenter.onClickCamera();
                    return true;
                /** 갤러리 선택 */
                case R.id.action_gallery:
                    presenter.onClickGallery();
                    return true;
                /** 기본이미지 사용 */
                case R.id.action_basic:
                    profileImage.setImageResource(R.drawable.ic_basic_profile);
                    isChangedProfileImage = true;
                    presenter.onChangedInput();
                    path = null;
                    return true;
                default:
                    return false;
            }
        });
        dialog.show();
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showUpdateText() {
        update.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideUpdateText() {
        update.setVisibility(View.GONE);
    }

    @Override
    public String getInputName() {
        return inputName.getText().toString();
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public String getInputStatusMessage() {
        return inputStatusMessage.getText().toString();
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public boolean getIsChangedProfileImage() {
        return isChangedProfileImage;
    }

    @Override
    public void showMessageForFailure() {
        ToastGenerator.show(R.string.msg_for_failure);
    }

    /**------------------------------------------------------------------
     메서드 ==> EditText 변화 이벤트
     ------------------------------------------------------------------*/
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        presenter.onChangedInput();
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    @Override
    public void afterTextChanged(Editable editable) {}

    /**------------------------------------------------------------------
     메서드 ==> 파일 생성
     ------------------------------------------------------------------*/
    private File createFile() {
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ShakeIt";
        File folder = new File(sdPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // 현재 시간 기준 파일명 생성
        String now = TimeManager.now();
        String imageFileName = user.getUserId() + now + ".jpg";
        // 파일 생성
        File curFile = new File(sdPath, imageFileName);
        return curFile;
    }

    /**------------------------------------------------------------------
     메서드 ==> Uri값을 통해 Path 얻기
     ------------------------------------------------------------------*/
    private String uriToPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
