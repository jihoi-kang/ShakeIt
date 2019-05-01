package com.example.kjh.shakeit.profile;

import com.example.kjh.shakeit.api.ApiClient;
import com.example.kjh.shakeit.callback.ResultCallback;
import com.example.kjh.shakeit.utils.TimeManager;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.kjh.shakeit.etc.Statics.ERROR_BAD_REQUEST;
import static com.example.kjh.shakeit.etc.Statics.ERROR_SERVICE_UNAVAILABLE;
import static com.example.kjh.shakeit.etc.Statics.SUCCESS_CREATED;
import static com.example.kjh.shakeit.etc.Statics.SUCCESS_OK;

public class UpdateProfileModel implements UpdateProfileContract.Model {

    /**------------------------------------------------------------------
     메서드 ==> 프로필 업데이트 요청
     ------------------------------------------------------------------*/
    @Override
    public void updateProfile(int _id, String image_url, String inputName, String inputStatusMessage, ResultCallback callback) {
        Call<ResponseBody> updateResult = ApiClient.create().updateProfile(_id, image_url, inputName, inputStatusMessage);
        updateResult.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                switch (response.code()){
                    case SUCCESS_OK:
                        try {
                            callback.onSuccess(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case ERROR_SERVICE_UNAVAILABLE:
                        callback.onFailure("SERVICE_UNAVAILABLE");
                        break;
                    case ERROR_BAD_REQUEST:
                        callback.onFailure("SERVER_ERROR");
                        break;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    /**------------------------------------------------------------------
     메서드 ==> 이미지 업로드 요청
     ------------------------------------------------------------------*/
    @Override
    public void uploadImage(int _id, String path, ResultCallback callback) {
        File file = new File(path);

        /** 확장자 분류 */
        int Idx = file.getName().lastIndexOf(".");
        String format = file.getName().substring(Idx+1);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/" + format), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", _id + "@" + TimeManager.now() + "." + format, requestFile);

        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), String.valueOf("profile"));

        Call<ResponseBody> result = ApiClient.create().uploadImage(body, type);

        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    switch (response.code()){
                        case SUCCESS_CREATED:
                            /** 업로드한 이미지 URL 반환 */
                            callback.onSuccess(response.body().string());
                            break;
                        case ERROR_SERVICE_UNAVAILABLE:
                            callback.onFailure("SERVICE_UNAVAILABLE");
                            break;
                        case ERROR_BAD_REQUEST:
                            callback.onFailure("SERVER_ERROR");
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure("" + t.getMessage());
            }
        });
    }
}
