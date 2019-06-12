package com.example.kjh.shakeit.data;

import io.realm.RealmObject;

/**
 * 이미지 캐시를 위한 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 6. 12. PM 5:21
 **/
public class ImageHolder extends RealmObject {

    private String url;
    private byte[] imageArray;

    public ImageHolder() {
    }

    public ImageHolder(String url, byte[] imageArray) {
        this.url = url;
        this.imageArray = imageArray;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getImageArray() {
        return imageArray;
    }

    public void setImageArray(byte[] imageArray) {
        this.imageArray = imageArray;
    }
}
