package com.example.kjh.shakeit.utils;

import com.google.gson.Gson;

/**
 * 클래스 ==> Json 정보
 * Json정보 ==> 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 26. PM 5:56
 **/
public class Serializer {

    private static Gson gson;

    private static Gson getGson() {
        if (gson == null) {
            synchronized (Serializer.class) {
                if (gson == null) {
                    gson = new Gson();
                }
            }
        }
        return gson;
    }

    public static String serialize(Object object) {
        return getGson().toJson(object);
    }

    public static <T> T deserialize(String json, Class<T> clazz) {
        return getGson().fromJson(json, clazz);
    }

}
