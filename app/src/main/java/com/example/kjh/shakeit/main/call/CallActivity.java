package com.example.kjh.shakeit.main.call;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.app.AppManager;
import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.netty.NettyClient;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.webrtc.AppRTCAudioManager;
import com.example.kjh.shakeit.webrtc.AppRTCAudioManager.AudioDevice;
import com.example.kjh.shakeit.webrtc.AppRTCClient;
import com.example.kjh.shakeit.webrtc.AppRTCClient.RoomConnectionParameters;
import com.example.kjh.shakeit.webrtc.AppRTCClient.SignalingParameters;
import com.example.kjh.shakeit.webrtc.CpuMonitor;
import com.example.kjh.shakeit.webrtc.DirectRTCClient;
import com.example.kjh.shakeit.webrtc.PeerConnectionClient;
import com.example.kjh.shakeit.webrtc.PeerConnectionClient.DataChannelParameters;
import com.example.kjh.shakeit.webrtc.PeerConnectionClient.PeerConnectionParameters;
import com.example.kjh.shakeit.webrtc.UnhandledExceptionHandler;
import com.example.kjh.shakeit.webrtc.WebSocketRTCClient;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.FileVideoCapturer;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon.ScalingType;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFileRenderer;
import org.webrtc.VideoRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.kjh.shakeit.app.Constant.EXTRA_AECDUMP_ENABLED;
import static com.example.kjh.shakeit.app.Constant.EXTRA_AUDIOCODEC;
import static com.example.kjh.shakeit.app.Constant.EXTRA_AUDIO_BITRATE;
import static com.example.kjh.shakeit.app.Constant.EXTRA_CAMERA2;
import static com.example.kjh.shakeit.app.Constant.EXTRA_CAPTURETOTEXTURE_ENABLED;
import static com.example.kjh.shakeit.app.Constant.EXTRA_CMDLINE;
import static com.example.kjh.shakeit.app.Constant.EXTRA_DATA_CHANNEL_ENABLED;
import static com.example.kjh.shakeit.app.Constant.EXTRA_DISABLE_BUILT_IN_AEC;
import static com.example.kjh.shakeit.app.Constant.EXTRA_DISABLE_BUILT_IN_AGC;
import static com.example.kjh.shakeit.app.Constant.EXTRA_DISABLE_BUILT_IN_NS;
import static com.example.kjh.shakeit.app.Constant.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF;
import static com.example.kjh.shakeit.app.Constant.EXTRA_ENABLE_LEVEL_CONTROL;
import static com.example.kjh.shakeit.app.Constant.EXTRA_FLEXFEC_ENABLED;
import static com.example.kjh.shakeit.app.Constant.EXTRA_HWCODEC_ENABLED;
import static com.example.kjh.shakeit.app.Constant.EXTRA_ID;
import static com.example.kjh.shakeit.app.Constant.EXTRA_LOOPBACK;
import static com.example.kjh.shakeit.app.Constant.EXTRA_MAX_RETRANSMITS;
import static com.example.kjh.shakeit.app.Constant.EXTRA_MAX_RETRANSMITS_MS;
import static com.example.kjh.shakeit.app.Constant.EXTRA_NEGOTIATED;
import static com.example.kjh.shakeit.app.Constant.EXTRA_NOAUDIOPROCESSING_ENABLED;
import static com.example.kjh.shakeit.app.Constant.EXTRA_OPENSLES_ENABLED;
import static com.example.kjh.shakeit.app.Constant.EXTRA_ORDERED;
import static com.example.kjh.shakeit.app.Constant.EXTRA_PROTOCOL;
import static com.example.kjh.shakeit.app.Constant.EXTRA_ROOMID;
import static com.example.kjh.shakeit.app.Constant.EXTRA_RUNTIME;
import static com.example.kjh.shakeit.app.Constant.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE;
import static com.example.kjh.shakeit.app.Constant.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT;
import static com.example.kjh.shakeit.app.Constant.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH;
import static com.example.kjh.shakeit.app.Constant.EXTRA_SCREENCAPTURE;
import static com.example.kjh.shakeit.app.Constant.EXTRA_TRACING;
import static com.example.kjh.shakeit.app.Constant.EXTRA_URLPARAMETERS;
import static com.example.kjh.shakeit.app.Constant.EXTRA_VIDEOCODEC;
import static com.example.kjh.shakeit.app.Constant.EXTRA_VIDEO_BITRATE;
import static com.example.kjh.shakeit.app.Constant.EXTRA_VIDEO_CALL;
import static com.example.kjh.shakeit.app.Constant.EXTRA_VIDEO_FILE_AS_CAMERA;
import static com.example.kjh.shakeit.app.Constant.EXTRA_VIDEO_FPS;
import static com.example.kjh.shakeit.app.Constant.EXTRA_VIDEO_HEIGHT;
import static com.example.kjh.shakeit.app.Constant.EXTRA_VIDEO_WIDTH;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.CONN_WEBRTC;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.DISCONN_WEBRTC;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.REQUEST;

/**
 * 영상 통화 액티비티
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 27. PM 8:44
 **/
public class CallActivity extends Activity implements AppRTCClient.SignalingEvents,
        PeerConnectionClient.PeerConnectionEvents,
        CallFragment.OnCallEvents {

    private static final String TAG = "CallRTCClient";

    private EglBase rootEglBase;

    private static final int CAPTURE_PERMISSION_REQUEST_CODE = 1;

    /** 피어 연결 콜백 기간(ms 단위) */
    private static final int STAT_CALLBACK_PERIOD = 1000;

    private static class ProxyRenderer implements VideoRenderer.Callbacks {
        private VideoRenderer.Callbacks target;

        @Override
        public void renderFrame(VideoRenderer.I420Frame frame) {
            if(target == null) {
                Logging.d(TAG, "Dropping frame in proxy because target is null.");
                VideoRenderer.renderFrameDone(frame);
                return;
            }

            target.renderFrame(frame);
        }

        synchronized public void setTarget(VideoRenderer.Callbacks target) {
            this.target = target;
        }
    }

    private final ProxyRenderer remoteProxyRenderer = new ProxyRenderer();
    private final ProxyRenderer localProxyVideoSink = new ProxyRenderer();
    private PeerConnectionClient peerConnectionClient = null;
    private AppRTCClient appRtcClient;
    private SignalingParameters signalingParameters;
    private AppRTCAudioManager audioManager = null;
    private VideoFileRenderer videoFileRenderer;
    private final List<VideoRenderer.Callbacks> remoteRenderers = new ArrayList<>();
    private Toast logToast;
    private boolean commandLineRun;
    private boolean activityRunning;
    private RoomConnectionParameters roomConnectionParameters;
    private PeerConnectionParameters peerConnectionParameters;
    private boolean iceConnected;
    private boolean isError;
    private boolean callControlFragmentVisible = true;
    private long callStartedTimeMs = 0;
    private boolean micEnabled = true;
    private boolean screencaptureEnabled = false;
    private static Intent mediaProjectionPermissionResultData;
    private static int mediaProjectionPermissionResultCode;
    // 로컬 뷰가 풀스크린 Renderer에 있는 경우 "True"
    private boolean isSwappedFeeds;

    // 제어
    private CallFragment callFragment;
    private HudFragment hudFragment;
    private CpuMonitor cpuMonitor;

    private Unbinder unbinder;
    @BindView(R.id.pip_video_view) SurfaceViewRenderer pipRenderer;
    @BindView(R.id.fullscreen_video_view) SurfaceViewRenderer fullscreenRenderer;

    private String roomId;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(this);

        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new UnhandledExceptionHandler(this));

        BusProvider.getInstance().register(this);

        /** 전체 화면 크기의 창 스타일 설정 */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());
        setContentView(R.layout.activity_call);

        iceConnected = false;
        signalingParameters = null;

        /** UI 컨트롤러 생성 */
        unbinder = ButterKnife.bind(this);
        callFragment = new CallFragment();
        hudFragment = new HudFragment();

        /** View 클릭시 Show/Hide 통화 컨트롤 Fragment */
        View.OnClickListener listener = view -> toggleCallControlFragmentVisibility();

        /** pipRender 클릭시 뷰 스왑 */
        pipRenderer.setOnClickListener(view -> setSwappedFeeds(!isSwappedFeeds));

        fullscreenRenderer.setOnClickListener(listener);
        remoteRenderers.add(remoteProxyRenderer);

        final Intent intent = getIntent();

        rootEglBase = EglBase.create();

        /** 피어 연결 클라이언트 생성 */
        peerConnectionClient = new PeerConnectionClient();

        /** 비디오 렌더러 생성 */
        pipRenderer.init(rootEglBase.getEglBaseContext(), null);
        pipRenderer.setScalingType(ScalingType.SCALE_ASPECT_FIT);
        String saveRemoteVideoToFile = intent.getStringExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);

        // saveRemoteVideoToFile가 설정되면 원격에서 파일로 비디오 저장
        if (saveRemoteVideoToFile != null) {
            int videoOutWidth = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
            int videoOutHeight = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
            try {
                videoFileRenderer = new VideoFileRenderer(saveRemoteVideoToFile, videoOutWidth,
                        videoOutHeight, peerConnectionClient.getRenderContext());
                remoteRenderers.add(videoFileRenderer);
            } catch (IOException e) {
                throw new RuntimeException(
                        "Failed to open video file for output: " + saveRemoteVideoToFile, e);
            }
        }
        fullscreenRenderer.init(rootEglBase.getEglBaseContext(), null);
        fullscreenRenderer.setScalingType(ScalingType.SCALE_ASPECT_FILL);

        pipRenderer.setZOrderMediaOverlay(true);
        pipRenderer.setEnableHardwareScaler(true /* enabled */);
        fullscreenRenderer.setEnableHardwareScaler(true /* enabled */);

        /** 전체 화면에서 로컬 피드로 시작하고 전화가 연결되면 pip로 변경 */
        setSwappedFeeds(true /* isSwappedFeeds */);

        Uri roomUri = intent.getData();
        if (roomUri == null) {
            Log.e(TAG, "Didn't get any URL in intent!");
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        /** Intent 파라미터들 */
        roomId = intent.getStringExtra(EXTRA_ROOMID);
        Log.d(TAG, "Room ID: " + roomId);
        if (roomId == null || roomId.length() == 0) {
            Log.e(TAG, "Incorrect room ID in intent!");
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        boolean loopback = intent.getBooleanExtra(EXTRA_LOOPBACK, false);
        boolean tracing = intent.getBooleanExtra(EXTRA_TRACING, false);

        int videoWidth = intent.getIntExtra(EXTRA_VIDEO_WIDTH, 0);
        int videoHeight = intent.getIntExtra(EXTRA_VIDEO_HEIGHT, 0);

        screencaptureEnabled = intent.getBooleanExtra(EXTRA_SCREENCAPTURE, false);
        // 캡쳐 포맷이 screencapture에 지정되어 있지 않으면 화면 해상도를 사용
        if (screencaptureEnabled && videoWidth == 0 && videoHeight == 0) {
            DisplayMetrics displayMetrics = getDisplayMetrics();
            videoWidth = displayMetrics.widthPixels;
            videoHeight = displayMetrics.heightPixels;
        }
        DataChannelParameters dataChannelParameters = null;
        if (intent.getBooleanExtra(EXTRA_DATA_CHANNEL_ENABLED, false)) {
            dataChannelParameters = new DataChannelParameters(intent.getBooleanExtra(EXTRA_ORDERED, true),
                    intent.getIntExtra(EXTRA_MAX_RETRANSMITS_MS, -1),
                    intent.getIntExtra(EXTRA_MAX_RETRANSMITS, -1), intent.getStringExtra(EXTRA_PROTOCOL),
                    intent.getBooleanExtra(EXTRA_NEGOTIATED, false), intent.getIntExtra(EXTRA_ID, -1));
        }

        peerConnectionParameters =
                new PeerConnectionParameters(intent.getBooleanExtra(EXTRA_VIDEO_CALL, true), loopback,
                        tracing, videoWidth, videoHeight, intent.getIntExtra(EXTRA_VIDEO_FPS, 0),
                        intent.getIntExtra(EXTRA_VIDEO_BITRATE, 0), intent.getStringExtra(EXTRA_VIDEOCODEC),
                        intent.getBooleanExtra(EXTRA_HWCODEC_ENABLED, true),
                        intent.getBooleanExtra(EXTRA_FLEXFEC_ENABLED, false),
                        intent.getIntExtra(EXTRA_AUDIO_BITRATE, 0), intent.getStringExtra(EXTRA_AUDIOCODEC),
                        intent.getBooleanExtra(EXTRA_NOAUDIOPROCESSING_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_AECDUMP_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_OPENSLES_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AEC, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AGC, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_NS, false),
                        intent.getBooleanExtra(EXTRA_ENABLE_LEVEL_CONTROL, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, false), dataChannelParameters);
        commandLineRun = intent.getBooleanExtra(EXTRA_CMDLINE, false);
        int runTimeMs = intent.getIntExtra(EXTRA_RUNTIME, 0);

        Log.d(TAG, "VIDEO_FILE: '" + intent.getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA) + "'");

        // 연결 클라이언트 생성
        // 방 이름이 IP 인 경우 DirectRTCClient를 사용하고 그렇지 않으면 표준 WebSocketRTCClient를 사용
        if (loopback || !DirectRTCClient.IP_PATTERN.matcher(roomId).matches()) {
            appRtcClient = new WebSocketRTCClient(this);
        } else {
            Log.i(TAG, "Using DirectRTCClient because room name looks like an IP.");
            appRtcClient = new DirectRTCClient(this);
        }

        // 연결 파라미터들 생성
        String urlParameters = intent.getStringExtra(EXTRA_URLPARAMETERS);
        roomConnectionParameters =
                new RoomConnectionParameters(roomUri.toString(), roomId, loopback, urlParameters);

        // CPU 모니터 생성
        if (cpuMonitor.isSupported()) {
            cpuMonitor = new CpuMonitor(this);
            hudFragment.setCpuMonitor(cpuMonitor);
        }

        // 인텐트 인수들 프레그 먼트로 보내기
        callFragment.setArguments(intent.getExtras());
        hudFragment.setArguments(intent.getExtras());
        // Call Activity 및 HUD Fragment를 활성화하고 통화를 시작
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.call_fragment_container, callFragment);
        ft.add(R.id.hud_fragment_container, hudFragment);
        ft.commit();

        if (commandLineRun && runTimeMs > 0) {
            (new Handler()).postDelayed(() -> disconnect(), runTimeMs);
        }

        if (loopback) {
            PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
            options.networkIgnoreMask = 0;
            peerConnectionClient.setPeerConnectionFactoryOptions(options);
        }
        peerConnectionClient.createPeerConnectionFactory(
                getApplicationContext(), peerConnectionParameters, CallActivity.this);

        if (screencaptureEnabled)
            startScreenCapture();
        else
            startCall();

        /** 영상 통화방이 셋팅 되었고 영상통화 상대방에게 알림(Netty) */
        if(intent.getStringExtra("type").equals("sender")) {
            MessageHolder holder = new MessageHolder();
            holder.setSign(REQUEST);
            holder.setType(CONN_WEBRTC);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("roomID", roomId);
                User otherUser = (User) intent.getSerializableExtra("otherUser");
                jsonObject.put("userId", otherUser.getUserId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String body = jsonObject.toString();
            holder.setBody(body);
            NettyClient.getInstance().sendMsgToServer(holder, null);
        }
    }

    /**------------------------------------------------------------------
     생명주기 ==> onStart()
     ------------------------------------------------------------------*/
    @Override
    public void onStart() {
        super.onStart();
        activityRunning = true;
        // 스크린 캡쳐를 위해 비디오가 일시 중지되지 않았음
        if (peerConnectionClient != null && !screencaptureEnabled) {
            peerConnectionClient.startVideoSource();
        }
        if (cpuMonitor != null) {
            cpuMonitor.resume();
        }
    }

    /**------------------------------------------------------------------
     생명주기 ==> onStop()
     ------------------------------------------------------------------*/
    @Override
    public void onStop() {
        super.onStop();
        activityRunning = false;
        // screencapture를 사용할 때 동영상을 멈추지 않아야 사용자가 다른 앱을 원격쪽에 보여 줄 수 있음
        if (peerConnectionClient != null && !screencaptureEnabled) {
            peerConnectionClient.stopVideoSource();
        }
        if (cpuMonitor != null) {
            cpuMonitor.pause();
        }
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    protected void onDestroy() {
        AppManager.getAppManager().removeActivity(this);

        Thread.setDefaultUncaughtExceptionHandler(null);
        disconnect();
        if (logToast != null) {
            logToast.cancel();
        }
        activityRunning = false;
        unbinder.unbind();
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    /** 기기 해상도 가져오기 */
    @TargetApi(17)
    private DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager =
                (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics;
    }
    @TargetApi(19)
    private static int getSystemUiVisibility() {
        int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        return flags;
    }
    @TargetApi(21)
    private void startScreenCapture() {
        MediaProjectionManager mediaProjectionManager =
                (MediaProjectionManager) getApplication().getSystemService(
                        Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE);
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this) && getIntent().getBooleanExtra(EXTRA_CAMERA2, true);
    }

    private boolean captureToTexture() {
        return getIntent().getBooleanExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, false);
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // 먼저, 앞면을 향한 카메라를 찾음
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // 전면 카메라가 보이지 않으면 다른것 시도
        Logging.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    @TargetApi(21)
    private VideoCapturer createScreenCapturer() {
        if (mediaProjectionPermissionResultCode != Activity.RESULT_OK) {
            reportError("User didn't give permission to capture the screen.");
            return null;
        }
        return new ScreenCapturerAndroid(
                mediaProjectionPermissionResultData, new MediaProjection.Callback() {
            @Override
            public void onStop() {
                reportError("User revoked permission to capture the screen.");
            }
        });
    }

    /** CallFragment.OnCallEvents 인터페이스 구현 */
    @Override
    public void onCallHangUp() {
        disconnect();
    }

    @Override
    public void onCameraSwitch() {
        if (peerConnectionClient != null) {
            peerConnectionClient.switchCamera();
        }
    }

    @Override
    public void onVideoScalingSwitch(ScalingType scalingType) {
        fullscreenRenderer.setScalingType(scalingType);
    }

    @Override
    public void onCaptureFormatChange(int width, int height, int framerate) {
        if (peerConnectionClient != null) {
            peerConnectionClient.changeCaptureFormat(width, height, framerate);
        }
    }

    @Override
    public boolean onToggleMic() {
        if (peerConnectionClient != null) {
            micEnabled = !micEnabled;
            peerConnectionClient.setAudioEnabled(micEnabled);
        }
        return micEnabled;
    }

    private void toggleCallControlFragmentVisibility() {
        if (!iceConnected || !callFragment.isAdded()) {
            return;
        }
        // 통화 제어 Fragment Show / hide
        callControlFragmentVisible = !callControlFragmentVisible;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (callControlFragmentVisible) {
            ft.show(callFragment);
            ft.show(hudFragment);
        } else {
            ft.hide(callFragment);
            ft.hide(hudFragment);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void startCall() {
        if (appRtcClient == null) {
            Log.e(TAG, "AppRTC client is not allocated for a call.");
            return;
        }
        callStartedTimeMs = System.currentTimeMillis();

        // 방 연결 생성
        appRtcClient.connectToRoom(roomConnectionParameters);

        // 오디오 관리자 생성
        audioManager = AppRTCAudioManager.create(getApplicationContext());
        // 가능한 최상의 VoIP 성능을 위해 기존 오디오 설정을 저장하고 오디오 모드를 MODE_IN_COMMUNICATION으로 변경
        Log.d(TAG, "Starting the audio manager...");
        // 사용 가능한 오디오 장치 수가 변경 될 때마다 호출
        audioManager.start((audioDevice, availableAudioDevices) -> onAudioManagerDevicesChanged(audioDevice, availableAudioDevices));
    }

    // UI Thread로 부터 호출 되어야 함
    private void callConnected() {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        Log.i(TAG, "Call connected: delay=" + delta + "ms");
        if (peerConnectionClient == null || isError) {
            Log.w(TAG, "Call is connected in closed or error state");
            return;
        }
        // 통계 콜백 사용
        peerConnectionClient.enableStatsEvents(true, STAT_CALLBACK_PERIOD);
        setSwappedFeeds(false /* isSwappedFeeds */);
    }

    // 오디오 관리자가 오디오 장치 변경을보고 할 때 호출
    private void onAudioManagerDevicesChanged(
            final AudioDevice device, final Set<AudioDevice> availableDevices) {
        Log.d(TAG, "onAudioManagerDevicesChanged: " + availableDevices + ", "
                + "selected: " + device);
        // TODO(henrika): add callback handler.
    }

    // 원격 자원의 연결을 끊고, 로컬 자원을 처분하고 종료
    private void disconnect() {
        activityRunning = false;
        remoteProxyRenderer.setTarget(null);
        localProxyVideoSink.setTarget(null);
        if (appRtcClient != null) {
            appRtcClient.disconnectFromRoom();
            appRtcClient = null;
        }
        if (pipRenderer != null) {
            pipRenderer.release();
            pipRenderer = null;
        }
        if (videoFileRenderer != null) {
            videoFileRenderer.release();
            videoFileRenderer = null;
        }
        if (fullscreenRenderer != null) {
            fullscreenRenderer.release();
            fullscreenRenderer = null;
        }
        if (peerConnectionClient != null) {
            peerConnectionClient.close();
            peerConnectionClient = null;
        }
        if (audioManager != null) {
            audioManager.stop();
            audioManager = null;
        }
        if (iceConnected && !isError) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private void disconnectWithErrorMessage(final String errorMessage) {
        if (commandLineRun || !activityRunning) {
            Log.e(TAG, "Critical error: " + errorMessage);
            disconnect();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getText(R.string.channel_error_title))
                    .setMessage(errorMessage)
                    .setCancelable(false)
                    .setNeutralButton(R.string.ok,
                            (dialog, id) -> {
                                dialog.cancel();
                                disconnect();
                            })
                    .create()
                    .show();
        }
    }

    private void reportError(final String description) {
        runOnUiThread(() -> {
            if (!isError) {
                isError = true;
                disconnectWithErrorMessage(description);
            }
        });
    }

    private VideoCapturer createVideoCapturer() {
        final VideoCapturer videoCapturer;
        String videoFileAsCamera = getIntent().getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA);
        if (videoFileAsCamera != null) {
            try {
                videoCapturer = new FileVideoCapturer(videoFileAsCamera);
            } catch (IOException e) {
                reportError("Failed to open video file for emulated camera");
                return null;
            }
        } else if (screencaptureEnabled) {
            return createScreenCapturer();
        } else if (useCamera2()) {
            if (!captureToTexture()) {
                reportError(getString(R.string.camera2_texture_only_error));
                return null;
            }

            Logging.d(TAG, "Creating capturer using camera2 API.");
            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
        } else {
            Logging.d(TAG, "Creating capturer using camera1 API.");
            videoCapturer = createCameraCapturer(new Camera1Enumerator(captureToTexture()));
        }
        if (videoCapturer == null) {
            reportError("Failed to open camera");
            return null;
        }
        return videoCapturer;
    }

    private void setSwappedFeeds(boolean isSwappedFeeds) {
        Logging.d(TAG, "setSwappedFeeds: " + isSwappedFeeds);
        this.isSwappedFeeds = isSwappedFeeds;
        localProxyVideoSink.setTarget(isSwappedFeeds ? fullscreenRenderer : pipRenderer);
        remoteProxyRenderer.setTarget(isSwappedFeeds ? pipRenderer : fullscreenRenderer);
        fullscreenRenderer.setMirror(isSwappedFeeds);
        pipRenderer.setMirror(!isSwappedFeeds);
    }

    /** 모든 콜백은 websocket 신호 루퍼 스레드에서 호출 */
    private void onConnectedToRoomInternal(final SignalingParameters params) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;

        signalingParameters = params;
        Log.d(TAG, "Creating peer connection, delay=" + delta + "ms");
        VideoCapturer videoCapturer = null;
        if (peerConnectionParameters.videoCallEnabled) {
            videoCapturer = createVideoCapturer();
        }
        peerConnectionClient.createPeerConnection(rootEglBase.getEglBaseContext(), localProxyVideoSink,
                remoteRenderers, videoCapturer, signalingParameters);

        if (signalingParameters.initiator) {
            Log.d(TAG, "Creating OFFER...");
            peerConnectionClient.createOffer();
        } else {
            if (params.offerSdp != null) {
                peerConnectionClient.setRemoteDescription(params.offerSdp);
                Log.d(TAG, "Creating ANSWER...");
                peerConnectionClient.createAnswer();
            }
            if (params.iceCandidates != null) {
                for (IceCandidate iceCandidate : params.iceCandidates) {
                    peerConnectionClient.addRemoteIceCandidate(iceCandidate);
                }
            }
        }
    }

    @Override
    public void onConnectedToRoom(final SignalingParameters params) {
        runOnUiThread(() -> onConnectedToRoomInternal(params));
    }

    @Override
    public void onRemoteDescription(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(() -> {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
                return;
            }
            Log.d(TAG, "Received remote " + sdp.type + ", delay=" + delta + "ms");
            peerConnectionClient.setRemoteDescription(sdp);
            if (!signalingParameters.initiator) {
                Log.d(TAG, "Creating ANSWER...");
                peerConnectionClient.createAnswer();
            }
        });
    }

    @Override
    public void onRemoteIceCandidate(final IceCandidate candidate) {
        runOnUiThread(() -> {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received ICE candidate for a non-initialized peer connection.");
                return;
            }
            peerConnectionClient.addRemoteIceCandidate(candidate);
        });
    }

    @Override
    public void onRemoteIceCandidatesRemoved(final IceCandidate[] candidates) {
        runOnUiThread(() -> {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received ICE candidate removals for a non-initialized peer connection.");
                return;
            }
            peerConnectionClient.removeRemoteIceCandidates(candidates);
        });
    }

    @Override
    public void onChannelClose() {
        runOnUiThread(() -> {
            Log.d(TAG, "Remote end hung up; dropping PeerConnection");
            disconnect();
        });
    }

    @Override
    public void onChannelError(final String description) {
        reportError(description);
    }

    @Override
    public void onLocalDescription(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(() -> {
            if (appRtcClient != null) {
                Log.d(TAG, "Sending " + sdp.type + ", delay=" + delta + "ms");
                if (signalingParameters.initiator) {
                    appRtcClient.sendOfferSdp(sdp);
                } else {
                    appRtcClient.sendAnswerSdp(sdp);
                }
            }
            if (peerConnectionParameters.videoMaxBitrate > 0) {
                Log.d(TAG, "Set video maximum bitrate: " + peerConnectionParameters.videoMaxBitrate);
                peerConnectionClient.setVideoMaxBitrate(peerConnectionParameters.videoMaxBitrate);
            }
        });
    }

    @Override
    public void onIceCandidate(final IceCandidate candidate) {
        runOnUiThread(() -> {
            if (appRtcClient != null) {
                appRtcClient.sendLocalIceCandidate(candidate);
            }
        });
    }

    @Override
    public void onIceCandidatesRemoved(final IceCandidate[] candidates) {
        runOnUiThread(() -> {
            if (appRtcClient != null) {
                appRtcClient.sendLocalIceCandidateRemovals(candidates);
            }
        });
    }

    @Override
    public void onIceConnected() {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(() -> {
            Log.d(TAG, "ICE connected, delay=" + delta + "ms");
            iceConnected = true;
            callConnected();
        });
    }

    @Override
    public void onIceDisconnected() {
        runOnUiThread(() -> {
            Log.d(TAG, "ICE disconnected");
            iceConnected = false;
            disconnect();
        });
    }

    @Override
    public void onPeerConnectionClosed() {
    }

    @Override
    public void onPeerConnectionStatsReady(final StatsReport[] reports) {
        runOnUiThread(() -> {
            if (!isError && iceConnected) {
                hudFragment.updateEncoderStatistics(reports);
            }
        });
    }

    @Override
    public void onPeerConnectionError(final String description) {
        reportError(description);
    }

    /**------------------------------------------------------------------
     구독이벤트 ==> Netty에서 이벤트 왔을 때 ==> 메시지 받거나 콜백
     ------------------------------------------------------------------*/
    @Subscribe
    public void nettyEvent (Events.nettyEvent event) {
        MessageHolder holder = event.getMessageHolder();

        if(holder.getType() == DISCONN_WEBRTC)
            finish();
    }

}
