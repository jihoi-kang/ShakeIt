package com.example.kjh.shakeit.api;

import com.example.kjh.shakeit.Statics;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 사용자의 HTTP 요청 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 26. PM 5:31
 **/
public class ApiClient {

    public static ApiInterface create() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(
                        GsonConverterFactory.create())
                .baseUrl(Statics.SERVER_URL)
                .build();

        return retrofit.create(ApiInterface.class);
    }

}
