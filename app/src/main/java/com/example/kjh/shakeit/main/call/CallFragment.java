package com.example.kjh.shakeit.main.call;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.kjh.shakeit.R;

import org.webrtc.RendererCommon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.kjh.shakeit.app.Constant.*;

/**
 * 사용자가 컨트롤 할 수 있는 Fragment 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 27. PM 8:51
 **/
public class CallFragment extends Fragment {

    private Unbinder unbinder;
    @BindView(R.id.contact_name_call) TextView contactView;
    @BindView(R.id.button_call_disconnect) ImageButton disconnectButton;
    @BindView(R.id.button_call_switch_camera) ImageButton cameraSwitchButton;
    @BindView(R.id.button_call_scaling_mode) ImageButton videoScalingButton;
    @BindView(R.id.button_call_toggle_mic) ImageButton toggleMuteButton;
    @BindView(R.id.capture_format_text_call) TextView captureFormatText;
    @BindView(R.id.capture_format_slider_call) SeekBar captureFormatSlider;

    private OnCallEvents callEvents;
    private RendererCommon.ScalingType scalingType;
    private boolean videoCallEnabled = true;

    /** 통화 제어 인터페이스 */
    public interface OnCallEvents {

        void onCallHangUp();
        void onCameraSwitch();
        void onVideoScalingSwitch(RendererCommon.ScalingType scalingType);
        void onCaptureFormatChange(int width, int height, int framerate);
        boolean onToggleMic();

    }

    /**------------------------------------------------------------------
     생명주기 ==> onAttach()
     ------------------------------------------------------------------*/
    // TODO(sakal): Replace with onAttach(Context) once we only support API level 23+.
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callEvents = (OnCallEvents) activity;
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreateView()
     ------------------------------------------------------------------*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View controlView = inflater.inflate(R.layout.fragment_call, container, false);

        /** Create UI Controls */
        unbinder = ButterKnife.bind(this, controlView);

        scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;

        return controlView;
    }

    /**------------------------------------------------------------------
     생명주기 ==> onStart()
     ------------------------------------------------------------------*/
    @Override
    public void onStart() {
        super.onStart();

        boolean captureSliderEnabled = false;
        Bundle args = getArguments();
        if (args != null) {
            String contactName = args.getString(EXTRA_ROOMID);
            contactView.setText(contactName);
            videoCallEnabled = args.getBoolean(EXTRA_VIDEO_CALL, true);
            captureSliderEnabled = videoCallEnabled
                    && args.getBoolean(EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, false);
        }
        if (!videoCallEnabled) {
            cameraSwitchButton.setVisibility(View.INVISIBLE);
        }

        captureFormatText.setVisibility(View.GONE);
        captureFormatSlider.setVisibility(View.GONE);
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 연결 해제
     ------------------------------------------------------------------*/
    @OnClick(R.id.button_call_disconnect)
    void onClickDisconnect() {
        callEvents.onCallHangUp();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 카메라 전면 후면 스위치
     ------------------------------------------------------------------*/
    @OnClick(R.id.button_call_switch_camera)
    void onClickCameraSwitch() {
        callEvents.onCameraSwitch();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 스케일 모드
     ------------------------------------------------------------------*/
    @OnClick(R.id.button_call_scaling_mode)
    void onClickScalingMode() {
        if (scalingType == RendererCommon.ScalingType.SCALE_ASPECT_FILL) {
            videoScalingButton.setBackgroundResource(R.drawable.ic_action_full_screen);
            scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FIT;
        } else {
            videoScalingButton.setBackgroundResource(R.drawable.ic_action_return_from_full_screen);
            scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
        }
        callEvents.onVideoScalingSwitch(scalingType);
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 마이크 뮤트
     ------------------------------------------------------------------*/
    @OnClick(R.id.button_call_toggle_mic)
    void onClickToggleMic() {
        boolean enabled = callEvents.onToggleMic();
        toggleMuteButton.setAlpha(enabled ? 1.0f : 0.3f);
    }

}
