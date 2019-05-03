package com.example.kjh.shakeit.api;

import com.example.kjh.shakeit.Statics;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * 실제 요청 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 26. PM 5:32
 **/
public interface ApiInterface {

    /** 이메일 로그인 */
    @FormUrlEncoded
    @POST("/" + Statics.LOGIN)
    Call<ResponseBody> login(
            @Field(value = "email", encoded = true) String email,
            @Field(value = "password", encoded = true) String password,
            @Field(value = "login_type", encoded = true) String login_type
    );

    /** 이메일 회원가입 */
    @FormUrlEncoded
    @POST("/" + Statics.SINGUP)
    Call<ResponseBody> signUp(
            @Field(value = "email", encoded = true) String email,
            @Field(value = "password", encoded = true) String password,
            @Field(value = "name", encoded = true) String name,
            @Field(value = "login_type", encoded = true) String login_type
    );

    /** 소셜 로그인 */
    @FormUrlEncoded
    @POST("/" + Statics.SOCIAL_LOGIN)
    Call<ResponseBody> socialLogin(
            @Field(value = "email", encoded = true) String email,
            @Field(value = "password", encoded = true) String password,
            @Field(value = "name", encoded = true) String name,
            @Field(value = "photo_url", encoded = true) String photo_url,
            @Field(value = "login_type", encoded = true) String login_type

    );

    /** 사용자의 device_token 업데이트 */
    @FormUrlEncoded
    @POST("/" + Statics.UPDATE_TOKEN)
    Call<ResponseBody> updateUserToken(
            @Field(value = "_id") int _id,
            @Field(value = "device_token") String device_token
    );

    /** 아이디 값으로 사용자 한명의 정보 받아오기 */
    @GET("/" + Statics.GET_USER + "?_id={_id}")
    Call<ResponseBody> getUserById(
            @Query("_id") Integer _id
    );

    /** 검색한 한명의 정보 받아오기 */
    @GET("/" + Statics.GET_FRIEND_INFO)
    Call<ResponseBody> getUserByEmail(
            @Query("_id") Integer _id,
            @Query("email") String email
    );

    /** 이미지 업로드 */
    @Multipart
    @POST("/" + Statics.UPLOAD_IMAGE)
    Call<ResponseBody> uploadImage(
            @Part MultipartBody.Part file,
            @Part("type") RequestBody type
    );

    /** 프로필 업데이트 */
    @FormUrlEncoded
    @POST("/" + Statics.UPDATE_PROFILE)
    Call<ResponseBody> updateProfile(
            @Field(value = "_id") int _id,
            @Field(value = "image_url") String image_url,
            @Field(value = "name") String name,
            @Field(value = "status_message") String status_message
    );

    /** 친구 목록 */
    @GET("/" + Statics.GET_FRIEND_LIST + "?_id={_id}")
    Call<ResponseBody> getFriendList(
            @Query("_id") Integer _id
    );

    /** 친구 추가 */
    @FormUrlEncoded
    @POST("/" + Statics.ADD_FRIEND)
    Call<ResponseBody> addFriend(
            @Field(value = "_id") int _id,
            @Field(value = "friend_id") int friend_id
    );
}

