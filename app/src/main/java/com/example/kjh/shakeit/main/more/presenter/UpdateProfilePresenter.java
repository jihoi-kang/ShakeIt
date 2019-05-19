package com.example.kjh.shakeit.main.more.presenter;

import android.Manifest;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.more.contract.UpdateProfileContract;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.utils.Validator;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_CAMERA;
import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_GALLERY;

public class UpdateProfilePresenter implements UpdateProfileContract.Presenter {

    private UpdateProfileContract.View view;
    private UpdateProfileContract.Model model;

    public UpdateProfilePresenter(UpdateProfileContract.View view, UpdateProfileContract.Model model) {
        this.view = view;
        this.model = model;
    }

    /**------------------------------------------------------------------
     메서드 ==> Input, 프로필 이미지가 변경되었을 때 호출 & 수정 버튼 Show Or Hide 로직
     ------------------------------------------------------------------*/
    @Override
    public void onChangedInput() {
        String inputName = view.getInputName();
        String inputStatusMessage = view.getInputStatusMessage();
        User user = view.getUser();

        if (!Validator.isValidName(inputName)) {
            view.hideUpdateText();
            return;
        }

        if (user.getStatusMessage() == null)
            user.setStatusMessage("");

        if (user.getName().equals(inputName)
                && (user.getStatusMessage()).equals(inputStatusMessage)
                && !view.getIsChangedProfileImage()) {
            view.hideUpdateText();
            return;
        }
        view.showUpdateText();
    }

    /**------------------------------------------------------------------
     메서드 ==> 프로필 이미지 변경 클릭
     ------------------------------------------------------------------*/
    @Override
    public void onClickProfileImage() {
        view.showSelectType();
    }

    /**------------------------------------------------------------------
     메서드 ==> 카메라
     ------------------------------------------------------------------*/
    @Override
    public void onClickCamera() {
        /** 권한 확인 */
        TedPermission.with(view.getActivity())
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(view.getActivity(), "com.example.kjh.shakeit.fileprovider", view.getFile()));
                        view.getActivity().startActivityForResult(intent, REQUEST_CODE_CAMERA);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                    }
                })
                .setDeniedTitle(R.string.permission_denied_title)
                .setDeniedMessage(R.string.permission_denied_message)
                .setGotoSettingButtonText(R.string.tedpermission_setting)
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    /**------------------------------------------------------------------
     메서드 ==> 갤러리 클릭
     ------------------------------------------------------------------*/
    @Override
    public void onClickGallery() {
        /** 권한 확인 */
        TedPermission.with(view.getActivity())
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        view.getActivity().startActivityForResult(intent, REQUEST_CODE_GALLERY);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {}
                })
                .setDeniedTitle(R.string.permission_denied_title)
                .setDeniedMessage(R.string.permission_denied_message)
                .setGotoSettingButtonText(R.string.tedpermission_setting)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

    }

    /**------------------------------------------------------------------
     메서드 ==> 수정 버튼 클릭
     ------------------------------------------------------------------*/
    @Override
    public void onClickUpdate() {
        User user = view.getUser();
        String path = view.getPath();
        int _id = view.getUser().getUserId();
        String name = view.getInputName();
        String statusMessage = view.getInputStatusMessage();
        boolean isChangedProfileImage = view.getIsChangedProfileImage();

        view.showLoadingDialog();
        view.hideSoftKeyboard();

        /** 프로필 이미지를 변경되었을 때 */
        if(isChangedProfileImage && path != null){
            /** 이미지 업로드 후 프로필 업데이트 */
            model.uploadImage(_id, path, new ResultCallback() {
                @Override
                public void onSuccess(String body) {
                    JSONObject jsonObject = null;
                    String resultMessage = "";
                    try {
                        jsonObject = new JSONObject(body);
                        resultMessage = jsonObject.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    /** holder.getMessage() 는 업로드 된 이미지 URL */

                    String finalResultMessage = resultMessage;
                    model.updateProfile(_id, resultMessage, name, statusMessage, new ResultCallback() {
                        @Override
                        public void onSuccess(String body) {
                            user.setImageUrl(finalResultMessage);
                            user.setName(name);
                            user.setStatusMessage(statusMessage);
                            noticeUpdateProfile(user);

                            view.hideLoadingDialog();
                            view.finishActivity();
                        }

                        @Override
                        public void onFailure(String errorMsg) {
                            view.hideLoadingDialog();
                        }
                    });
                }

                @Override
                public void onFailure(String errorMsg) {
                    view.hideLoadingDialog();
                    view.showMessageForFailureUpload(errorMsg);
                }
            });
        }
        /** 기본 프로필 이미지로 변경 했을 때 & 프로필 이미지 수정 안했을 때 */
        else {
            model.updateProfile(_id, view.getPath(), name, statusMessage, new ResultCallback() {
                @Override
                public void onSuccess(String body) {
                    user.setImageUrl(path);
                    user.setName(name);
                    user.setStatusMessage(statusMessage);
                    noticeUpdateProfile(user);

                    view.hideLoadingDialog();
                    view.finishActivity();
                }

                @Override
                public void onFailure(String errorMsg) {
                    view.hideLoadingDialog();
                    view.showMessageForFailure();
                }
            });
        }
    }

    /**------------------------------------------------------------------
     메서드 ==> 프로필이 업데이트 되었음을 알림
     ------------------------------------------------------------------*/
    private void noticeUpdateProfile(User user) {
        Events.updateProfileEvent event = new Events.updateProfileEvent(user);
        BusProvider.getInstance().post(event);
    }

}
