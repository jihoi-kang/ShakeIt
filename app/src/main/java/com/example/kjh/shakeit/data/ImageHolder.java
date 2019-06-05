package com.example.kjh.shakeit.data;

import io.realm.RealmObject;

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
