package com.example.kjh.shakeit.api;

import com.example.kjh.shakeit.Statics;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

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

}
