package com.example.kjh.shakeit;

/**
 * 앱에서 사용하는 변수들 집합
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 26. PM 5:58
 **/
public class Statics {

    /** HTTP REQUEST URL */
    public static final String SERVER_URL = "http://115.71.239.176";

    public static final String LOGIN = "api/user/login.php";
    public static final String SINGUP = "api/user/sign_up.php";
    public static final String SOCIAL_LOGIN = "api/user/social_login.php";
    public static final String UPDATE_TOKEN = "api/user/update_token.php";
    public static final String GET_USER = "api/user/read_one.php";
    public static final String GET_FRIEND_INFO = "api/friend/read_one.php";
    public static final String UPDATE_PROFILE = "api/user/update_profile.php";
    public static final String UPLOAD_IMAGE = "api/uploads.php";
    public static final String GET_FRIEND_LIST = "api/friend/read.php";
    public static final String ADD_FRIEND = "api/friend/create.php";


    /** HTTP RESPONSE CODE */
    public static final int SUCCESS_OK = 200;
    public static final int SUCCESS_CREATED = 201;

    public static final int ERROR_BAD_REQUEST = 400;
    public static final int ERROR_NOT_FOUND = 404;

    public static final int ERROR_SERVICE_UNAVAILABLE = 503;

    /** NETTY */
    public static final String SOCKET_HOST = "115.71.239.176";
    public static final int SOCKET_PORT = 8080;

    /** 인텐트 숫자 CODE */
    public static final int REQUEST_CODE_MAIN_BEFORE_LOGIN_TO_EMAILLOGIN = 10001;
    public static final int REQUEST_CODE_FACEBOOK_LOGIN = 64206;
    public static final int REQUEST_CODE_GOOGLE_LOGIN = 10002;

    public static final int REQUEST_CODE_CAMERA = 10003;
    public static final int REQUEST_CODE_GALLERY = 10004;


    /** 뷰페이저 */
    public final static int[] nonTabIcon = {
            R.drawable.ic_person_outline_grey_24dp,
            R.drawable.ic_chat_bubble_outline_grey_24dp,
            R.drawable.ic_more_horiz_grey_24dp
    };
    public final static int[] onTabIcon = {
            R.drawable.ic_person_primary_24dp,
            R.drawable.ic_chat_bubble_primary_24dp,
            R.drawable.ic_more_horiz_primary_24dp
    };
    public final static String[] titles = new String[]{
            "친구목록", "채팅목록", "더보기"
    };
    public final static int[] tabLayoutImage = {
            R.drawable.ic_add_person_white_18dp,
            R.drawable.ic_add_chat_room_white_24dp
    };


}
