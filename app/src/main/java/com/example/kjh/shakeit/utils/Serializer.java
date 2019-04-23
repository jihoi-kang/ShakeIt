package com.example.kjh.shakeit.utils;

import com.google.gson.Gson;

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
