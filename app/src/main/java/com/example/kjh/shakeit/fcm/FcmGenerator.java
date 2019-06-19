package com.example.kjh.shakeit.fcm;

import android.util.Log;

import com.example.kjh.shakeit.api.ApiClient;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * FCM시 필요 사항들을 돕는 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 26. PM 5:47
 **/
public class FcmGenerator {

    private static final String TAG = FcmGenerator.class.getSimpleName();

    private static boolean isSuccess = false;

    private static final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    private static String postUrl = "https://fcm.googleapis.com/fcm/send";

    /**------------------------------------------------------------------
     메서드 ==> FCM 메시지 발송
     ------------------------------------------------------------------*/
    public static boolean postRequest(String token, String title, String message) {
        String postBody="{\n" +
                "    \"to\": \"" + token + "\",\n" +
                "    \"priority\": \"high\",\n" +
                "    \"data\":{\n" +
                "       \"title\":\"" + title + "\",\n" +
                "       \"message\":\"" + message + "\"\n" +
                "    }\n" +
                "}";

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType, postBody);

        Request request = new Request.Builder()
                .header("Authorization", "key=AAAAlXm4S_s:APA91bFTlDbQNxcCOwy2e8mcUbIKYSlFVd-UwnK5bkLuKxai9e_Y6yb7tDQm4Hpde78gUlksxiGTpfdDgpQuEVRHmS_wv4jL5DhQX_mKF3Den733ntI6ga7sf6Suh5frH13ZNDUeylSe")
                .url(postUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                isSuccess = false;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, response.body().string());
                isSuccess = true;
            }
        });

        return isSuccess;
    }

    /**------------------------------------------------------------------
     메서드 ==> 로그인 / 로그아웃시 사용자 DB에 토큰 저장 및 삭제
     ------------------------------------------------------------------*/
    public static void updateUserToken(final int _id, String type) {
        /** 로그아웃시 */
        if(type.equals("logout")){
            retrofit2.Call<ResponseBody> result = ApiClient.create().updateUserToken(_id, null);

            result.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) { }

                @Override
                public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) { }
            });
        }
        /** 로그인시 */
        else if(type.equals("login")) {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful())
                            return;

                        String token = task.getResult().getToken();
//                        Log.d(TAG, "Token => " + token);

                        retrofit2.Call<ResponseBody> result = ApiClient.create().updateUserToken(_id, token);

                        result.enqueue(new retrofit2.Callback<ResponseBody>() {
                            @Override
                            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                                try {
                                    Log.d(TAG, response.body().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                                Log.e(TAG, t.getMessage());
                            }
                        });
                    });
        }
    }
}
