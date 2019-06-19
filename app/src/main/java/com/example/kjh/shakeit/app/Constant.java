package com.example.kjh.shakeit.app;

import com.example.kjh.shakeit.R;

/**
 * 앱에서 사용하는 변수들 집합
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 26. PM 5:58
 **/
public class Constant {

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
    public static final String GET_CHATROOM_LIST = "api/chatroom/read.php";
    public static final String GET_CHATROOM = "api/chatroom/read_one.php";
    public static final String GET_CHATLOG_LIST = "api/chatlog/read.php";
    public static final String IS_FRIEND = "api/friend/isfriend.php";
    public static final String KAKAOPAY_READY = "kakaopay/payment.php";
    public static final String WIRE_CASH = "api/point/wire.php";

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

    public static final int REQUEST_CODE_MAIN_AFTER_LOGIN_TO_ADD_CHAT = 10005;

    public static final int REQUEST_CODE_UPDATE_PROFILE_TO_IMAGE_FILTER = 10006;

    public static final int REQUEST_CONNECTION = 10007;

    public static final int REQUEST_CODE_CHAT_TO_CALL_WAIT = 10008;

    public static final int REQUEST_CODE_MAIN_TO_CHAT = 10009;
    public static final int REQUEST_CODE_CHAT_TO_PROFILE_DETAIL = 10010;
    public static final int REQUEST_CODE_FRIEND_LIST_TO_PROFILE_DETAIL = 10011;
    public static final int REQUEST_CODE_SHAKE_TO_PROFILE_DETAIL = 10012;
    public static final int REQUEST_CODE_MAIN_TO_SHAKE = 10013;
    public static final int REQUEST_CODE_MAIN_TO_CHARGE = 10015;
    public static final int REQUEST_CODE_CHOOSE_FRIEND_TO_WIRE_CASH = 10016;


    /** WebRTC */
    public static final String ROOM_URL = "https://appr.tc";
    public static final boolean VIDEO_CALL_ENABLED = true;
    public static final boolean USE_SCREEN_CAPTURE = false;
    public static final boolean USE_CAMERA_2 = true;
    public static final String VIDEO_CODEC = "VP9";
    public static final String AUDIO_CODEC = "OPUS";
    public static final boolean HW_CODEC = true;
    public static final boolean CAPTURE_TO_TEXTURE = true;
    public static final boolean FLEXFEC_ENABLED = false;
    public static final boolean NO_AUDIO_PROCESSING = false;
    public static final boolean AEC_DUMP = false;
    public static final boolean USE_OPENSLES = false;
    public static final boolean DISABLE_BUILT_IN_AEC = false;
    public static final boolean DISABLE_BUILT_IN_AGC = false;
    public static final boolean DISABLE_BUILT_IN_NS = false;
    public static final boolean ENABLE_LEVEL_CONTROL = false;
    public static final boolean DISABLE_WEBRTC_AGC_AND_HPE = false;
    public static final int HD_VIDEO_WIDTH = 1280;
    public static final int HD_VIDEO_HEIGHT = 720;
    public static final int DEFAULT_CAMERA_FPS = 24;
    public static final boolean CAPTURE_QUALITY_SLIDER = false;
    public static final int VIDEO_START_BITRATE = 1700;
    public static final int AUDIO_START_BITRATE = 32;
    public static final boolean DISPLAY_HUD = false;
    public static final boolean TRACING = false;
    public static final boolean DATA_CHANNEL_ENABLED = true;
    public static final boolean ORDERED = true;
    public static final boolean NEGOTIATED = false;
    public static final int MAX_RETR_MS = -1;
    public static final int MAX_RETR = -1;
    public static final int ID = -1;
    public static final String PROTOCOL = "";

    /** WebRTC 인텐트 */
    public static final String EXTRA_ROOMID = "org.appspot.apprtc.ROOMID";
    public static final String EXTRA_URLPARAMETERS = "org.appspot.apprtc.URLPARAMETERS";
    public static final String EXTRA_LOOPBACK = "org.appspot.apprtc.LOOPBACK";
    public static final String EXTRA_VIDEO_CALL = "org.appspot.apprtc.VIDEO_CALL";
    public static final String EXTRA_SCREENCAPTURE = "org.appspot.apprtc.SCREENCAPTURE";
    public static final String EXTRA_CAMERA2 = "org.appspot.apprtc.CAMERA2";
    public static final String EXTRA_VIDEO_WIDTH = "org.appspot.apprtc.VIDEO_WIDTH";
    public static final String EXTRA_VIDEO_HEIGHT = "org.appspot.apprtc.VIDEO_HEIGHT";
    public static final String EXTRA_VIDEO_FPS = "org.appspot.apprtc.VIDEO_FPS";
    public static final String EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED = "org.appsopt.apprtc.VIDEO_CAPTUREQUALITYSLIDER";
    public static final String EXTRA_VIDEO_BITRATE = "org.appspot.apprtc.VIDEO_BITRATE";
    public static final String EXTRA_VIDEOCODEC = "org.appspot.apprtc.VIDEOCODEC";
    public static final String EXTRA_HWCODEC_ENABLED = "org.appspot.apprtc.HWCODEC";
    public static final String EXTRA_CAPTURETOTEXTURE_ENABLED = "org.appspot.apprtc.CAPTURETOTEXTURE";
    public static final String EXTRA_FLEXFEC_ENABLED = "org.appspot.apprtc.FLEXFEC";
    public static final String EXTRA_AUDIO_BITRATE = "org.appspot.apprtc.AUDIO_BITRATE";
    public static final String EXTRA_AUDIOCODEC = "org.appspot.apprtc.AUDIOCODEC";
    public static final String EXTRA_NOAUDIOPROCESSING_ENABLED = "org.appspot.apprtc.NOAUDIOPROCESSING";
    public static final String EXTRA_AECDUMP_ENABLED = "org.appspot.apprtc.AECDUMP";
    public static final String EXTRA_OPENSLES_ENABLED = "org.appspot.apprtc.OPENSLES";
    public static final String EXTRA_DISABLE_BUILT_IN_AEC = "org.appspot.apprtc.DISABLE_BUILT_IN_AEC";
    public static final String EXTRA_DISABLE_BUILT_IN_AGC = "org.appspot.apprtc.DISABLE_BUILT_IN_AGC";
    public static final String EXTRA_DISABLE_BUILT_IN_NS = "org.appspot.apprtc.DISABLE_BUILT_IN_NS";
    public static final String EXTRA_ENABLE_LEVEL_CONTROL = "org.appspot.apprtc.ENABLE_LEVEL_CONTROL";
    public static final String EXTRA_DISABLE_WEBRTC_AGC_AND_HPF = "org.appspot.apprtc.DISABLE_WEBRTC_GAIN_CONTROL";
    public static final String EXTRA_DISPLAY_HUD = "org.appspot.apprtc.DISPLAY_HUD";
    public static final String EXTRA_TRACING = "org.appspot.apprtc.TRACING";
    public static final String EXTRA_CMDLINE = "org.appspot.apprtc.CMDLINE";
    public static final String EXTRA_RUNTIME = "org.appspot.apprtc.RUNTIME";
    public static final String EXTRA_VIDEO_FILE_AS_CAMERA = "org.appspot.apprtc.VIDEO_FILE_AS_CAMERA";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE = "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH = "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_WIDTH";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT = "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT";
    public static final String EXTRA_USE_VALUES_FROM_INTENT = "org.appspot.apprtc.USE_VALUES_FROM_INTENT";
    public static final String EXTRA_DATA_CHANNEL_ENABLED = "org.appspot.apprtc.DATA_CHANNEL_ENABLED";
    public static final String EXTRA_ORDERED = "org.appspot.apprtc.ORDERED";
    public static final String EXTRA_MAX_RETRANSMITS_MS = "org.appspot.apprtc.MAX_RETRANSMITS_MS";
    public static final String EXTRA_MAX_RETRANSMITS = "org.appspot.apprtc.MAX_RETRANSMITS";
    public static final String EXTRA_PROTOCOL = "org.appspot.apprtc.PROTOCOL";
    public static final String EXTRA_NEGOTIATED = "org.appspot.apprtc.NEGOTIATED";
    public static final String EXTRA_ID = "org.appspot.apprtc.ID";

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
            R.drawable.ic_add_person_black_48dp,
            R.drawable.ic_add_chat_room_black_48dp
    };
}
