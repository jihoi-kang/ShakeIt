package com.example.kjh.shakeit.api;

import com.example.kjh.shakeit.app.Constant;

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
 * HTTP 요청 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 26. PM 5:32
 **/
public interface ApiInterface {

    /** 이메일 로그인 */
    @FormUrlEncoded
    @POST("/" + Constant.LOGIN)
    Call<ResponseBody> login(
            @Field(value = "email", encoded = true) String email,
            @Field(value = "password", encoded = true) String password,
            @Field(value = "loginType", encoded = true) String loginType
    );

    /** 이메일 회원가입 */
    @FormUrlEncoded
    @POST("/" + Constant.SINGUP)
    Call<ResponseBody> signUp(
            @Field(value = "email", encoded = true) String email,
            @Field(value = "password", encoded = true) String password,
            @Field(value = "name", encoded = true) String name,
            @Field(value = "loginType", encoded = true) String loginType
    );

    /** 소셜 로그인 */
    @FormUrlEncoded
    @POST("/" + Constant.SOCIAL_LOGIN)
    Call<ResponseBody> socialLogin(
            @Field(value = "email", encoded = true) String email,
            @Field(value = "password", encoded = true) String password,
            @Field(value = "name", encoded = true) String name,
            @Field(value = "imageUrl", encoded = true) String imageUrl,
            @Field(value = "loginType", encoded = true) String loginType

    );

    /** 사용자의 device_token 업데이트 */
    @FormUrlEncoded
    @POST("/" + Constant.UPDATE_TOKEN)
    Call<ResponseBody> updateUserToken(
            @Field(value = "userId") int userId,
            @Field(value = "deviceToken") String deviceToken
    );

    /** 아이디 값으로 사용자 한명의 정보 받아오기 */
    @GET("/" + Constant.GET_USER)
    Call<ResponseBody> getUserById(
            @Query("userId") Integer userId
    );

    /** 검색한 한명의 정보 받아오기 */
    @GET("/" + Constant.GET_FRIEND_INFO)
    Call<ResponseBody> getUserByEmail(
            @Query("userId") Integer userId,
            @Query("email") String email
    );

    /** 이미지 업로드 */
    @Multipart
    @POST("/" + Constant.UPLOAD_IMAGE)
    Call<ResponseBody> uploadImage(
            @Part MultipartBody.Part file,
            @Part("type") RequestBody type
    );

    /** 프로필 업데이트 */
    @FormUrlEncoded
    @POST("/" + Constant.UPDATE_PROFILE)
    Call<ResponseBody> updateProfile(
            @Field(value = "userId") int userId,
            @Field(value = "imageUrl") String imageUrl,
            @Field(value = "name") String name,
            @Field(value = "statusMessage") String statusMessage
    );

    /** 친구 목록 */
    @GET("/" + Constant.GET_FRIEND_LIST)
    Call<ResponseBody> getFriendList(
            @Query("userId") Integer userId
    );

    /** 친구 추가 */
    @FormUrlEncoded
    @POST("/" + Constant.ADD_FRIEND)
    Call<ResponseBody> addFriend(
            @Field(value = "userId") int userId,
            @Field(value = "friendId") int friendId
    );

    /** 채팅방 목록 */
    @GET("/" + Constant.GET_CHATROOM_LIST)
    Call<ResponseBody> getChatRoomList(
            @Query("userId") Integer userId
    );

    /** 채팅로그 목록 */
    @GET("/" + Constant.GET_CHATLOG_LIST)
    Call<ResponseBody> getChatLogList(
            @Query("userId") Integer userId
    );

    /** 친구인지 확인 */
    @GET("/" + Constant.IS_FRIEND)
    Call<ResponseBody> isFriend(
            @Query("userId") Integer userId,
            @Query("friendId") Integer friendId
    );

    /** 카카오페이 충전 준비 URL */
    @GET("/" + Constant.KAKAOPAY_READY)
    Call<ResponseBody> chargeReady(
            @Query("userId") Integer userId,
            @Query("amount") Integer amount
    );

    /** 포인트 전송 */
    @FormUrlEncoded
    @POST("/" + Constant.WIRE_CASH)
    Call<ResponseBody> wire(
            @Field(value = "userId") int userId,
            @Field(value = "friendId") int friendId,
            @Field(value = "amount") int amount
    );
}

