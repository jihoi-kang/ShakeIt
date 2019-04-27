package com.example.kjh.shakeit.api;

import com.example.kjh.shakeit.etc.Statics;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    /** 사용자 한명의 정보 받아오기 */
    @GET("/" + Statics.GET_USER + "?_id={_id}")
    Call<ResponseBody> getUser(
            @Query("_id") Integer _id
    );
}

