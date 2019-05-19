package com.example.kjh.shakeit.api;

/**
 * HTTP 요청시 Presenter --> Model로 데이터를 요청하는데
 * Model에서 반환 데이터를 받고 다시 Presenter로 Callback 보내줄 때 사용하는 Interface
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 26. PM 5:39
 **/
public interface ResultCallback {

    void onSuccess(String body);
    void onFailure(String errorMsg);

}
