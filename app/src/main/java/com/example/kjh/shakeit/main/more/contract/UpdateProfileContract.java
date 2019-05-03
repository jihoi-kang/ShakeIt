package com.example.kjh.shakeit.main.more.contract;

import android.app.Activity;

import com.example.kjh.shakeit.callback.ResultCallback;
import com.example.kjh.shakeit.data.User;

import java.io.File;

public interface UpdateProfileContract {

    interface View {

        void hideUpdateText();
        String getInputName();
        void showUpdateText();
        User getUser();
        String getInputStatusMessage();
        Activity getActivity();
        void showSelectType();
        void showLoadingDialog();
        void hideLoadingDialog();
        void showMessageForFailureUpload(String errorMsg);
        File getFile();
        String getPath();
        boolean getIsChangedProfileImage();
        void finishActivity();
        void hideSoftKeyboard();

    }

    interface Presenter {

        void onChangedInput();
        void onClickProfileImage();
        void onClickCamera();
        void onClickGallery();
        void onClickUpdate();

    }

    interface Model {

        void updateProfile(int id, String path, String name, String statusMessage, ResultCallback callback);
        void uploadImage(int id, String path, ResultCallback callback);

    }

}
