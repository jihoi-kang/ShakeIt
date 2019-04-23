package com.example.kjh.shakeit;

public class Statics {

    public static final String SERVER_URL = "http://115.71.239.176";

    public static final String LOGIN = "api/user/login.php";
    public static final String SINGUP = "api/user/sign_up.php";


    /** HTTP RESPONSE CODE */
    public static final int SUCCESS_OK = 200;
    public static final int SUCCESS_CREATED = 201;

    public static final int ERROR_BAD_REQUEST = 400;

    public static final int ERROR_SERVICE_UNAVAILABLE = 503;

    /** 인텐트 숫자 CODE */
    public static final int REQUEST_CODE_MAIN_BEFORE_LOGIN_TO_EMAILLOGIN = 10001;

}
