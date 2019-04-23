package com.example.kjh.shakeit.api;

import com.example.kjh.shakeit.Statics;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
