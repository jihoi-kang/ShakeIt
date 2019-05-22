package com.example.kjh.shakeit.webrtc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.webrtc.utils.AppRTCUtils;

import org.webrtc.ThreadUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * AppRTC 중 소리와 관련된 부분 관리
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 21. AM 1:02
 **/
public class AppRTCAudioManager {

    private static final String TAG = "AppRTCAudioManager";
    private static final String SPEAKERPHONE_AUTO = "auto";
    private static final String SPEAKERPHONE_TRUE = "true";
    private static final String SPEAKERPHONE_FALSE = "false";

    /** 현재 지원하는 오디오 장치들 */
    public enum AudioDevice {
        SPEAKER_PHONE,
        WIRED_HEADSET,
        EARPIECE,
        BLUETOOTH,
        NONE
    }

    /** 오디오 관리 상태 */
    public enum AudioManagerState {
        UNINITIALIZED,
        PREINITIALIZED,
        RUNNING,
    }

    /** 오디오 장치 변경 콜백 이벤트 */
    public interface AudioManagerEvents {
        void onAudioDeviceChanged(
                AudioDevice selectedAudioDevice,
                Set<AudioDevice> availableAudioDevices
        );
    }

    private final Context apprtcContext;
    private AudioManager audioManager;

    private AudioManagerEvents audioManagerEvents;
    private AudioManagerState amState;
    private int savedAudioMode = AudioManager.MODE_INVALID;
    private boolean savedIsSpeakerPhoneOn = false;
    private boolean savedIsMicrophoneMute = false;
    private boolean hasWiredHeadset = false;

    /** 초기 오디오 장치 */
    private AudioDevice defaultAudioDevice;
    /** 선택된 오디오 장치 */
    private AudioDevice selectedAudioDevice;
    /** 사용자가 선택한 오디오 장치 */
    private AudioDevice userSelectedAudioDevice;
    /** 스피커폰 설정 */
    private final String useSpeakerphone;
    /** 근접 센서 객체로 cm 단위의 물체 근접도 측정 */
    private AppRTCProximitySensor proximitySensor = null;

    /** 사용 가능한 오디오 장치 목록 */
    private Set<AudioDevice> audioDevices = new HashSet<>();

    /** 유선 헤드셋 브로드캐스트 리시버 */
    private BroadcastReceiver wiredHeadsetReceiver;

    /** 음성 포커스 변경 콜백 리스너 */
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;

    /** 센서 상태가 변경 될 때 호출
     *  ex. 가까이 --> 멀리
     *      멀리 --> 가까이 */
    private void onProximitySensorChangedState() {
        if(!useSpeakerphone.equals(SPEAKERPHONE_AUTO))
            return;

        // 근접 센서는 2개 이상의 사용 가능한 오디오 장치가 있을 경우에만 활성화
        if(audioDevices.size() == 2 && audioDevices.contains(AudioDevice.EARPIECE)
                && audioDevices.contains(AudioDevice.SPEAKER_PHONE)) {
            if(proximitySensor.sensorReportsNearState()) {
                setAudioDeviceInternal(AudioDevice.EARPIECE);
            } else {
                setAudioDeviceInternal(AudioDevice.SPEAKER_PHONE);
            }
        }
    }

    /** 유선 헤드셋 가용성의 변경을 처리하는 브로드 캐스트 */
    private class WiredHeadsetReceiver extends BroadcastReceiver {
        private static final int STATE_UNPLUGGED = 0;
        private static final int STATE_PLUGGED = 1;
        private static final int HAS_NO_MIC = 0;
        private static final int HAS_MIC = 1;

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra("state", STATE_UNPLUGGED);
            int microphone = intent.getIntExtra("microphone", HAS_NO_MIC);
            String name = intent.getStringExtra("name");
            Log.d(TAG, "WiredHeadsetReceiver.onReceive" + AppRTCUtils.getThreadInfo() + ": "
                    + "a=" + intent.getAction() + ", s="
                    + (state == STATE_UNPLUGGED ? "unplugged" : "plugged") + ", m="
                    + (microphone == HAS_MIC ? "mic" : "no mic") + ", n=" + name + ", sb="
                    + isInitialStickyBroadcast());
            hasWiredHeadset = (state == STATE_PLUGGED);
            updateAudioDeviceState();
        }
    }

    static AppRTCAudioManager create(Context context) {
        return new AppRTCAudioManager(context);
    }

    private AppRTCAudioManager(Context context) {
        Log.d(TAG, "ctor");
        ThreadUtils.checkIsOnMainThread();
        apprtcContext = context;
        audioManager = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE));
        wiredHeadsetReceiver = new WiredHeadsetReceiver();
        amState = AudioManagerState.UNINITIALIZED;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        useSpeakerphone = sharedPreferences.getString(context.getString(R.string.pref_speakerphone_key),
                context.getString(R.string.pref_speakerphone_default));
        Log.d(TAG, "useSpeakerphone: " + useSpeakerphone);
        if(useSpeakerphone.equals(SPEAKERPHONE_FALSE))
            defaultAudioDevice = AudioDevice.EARPIECE;
        else
            defaultAudioDevice = AudioDevice.SPEAKER_PHONE;

        // 근접 센서 초기화
        proximitySensor = AppRTCProximitySensor.create(
                context,
                // 상태 변화 감지 이벤트
                this::onProximitySensorChangedState
        );

        Log.d(TAG, "defaultAudioDevice: " + defaultAudioDevice);
        AppRTCUtils.logDeviceInfo(TAG);
    }

    @SuppressWarnings("deprecation")
    public void start(AudioManagerEvents audioManagerEvents) {
        Log.d(TAG, "start");
        ThreadUtils.checkIsOnMainThread();
        if(amState == AudioManagerState.RUNNING) {
            Log.e(TAG, "AudioManager is already active");
            return;
        }

        Log.d(TAG, "AudioManager starts...");
        this.audioManagerEvents = audioManagerEvents;
        amState = AudioManagerState.RUNNING;

        // stop() 메서드가 호출될 때 현재 오디오 상태 저장
        savedAudioMode = audioManager.getMode();
        savedIsSpeakerPhoneOn = audioManager.isSpeakerphoneOn();
        savedIsMicrophoneMute = audioManager.isMicrophoneMute();
        hasWiredHeadset = hasWiredHeadset();

        // 오디오 포커스가 변경 되었을 때 호출 되는 메서드
        // 포커스 획득 여부, 손실 등등 알 수 있음
        audioFocusChangeListener = focusChange -> {
            final String typeOfChange;
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    typeOfChange = "AUDIOFOCUS_GAIN";
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                    typeOfChange = "AUDIOFOCUS_GAIN_TRANSIENT";
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE:
                    typeOfChange = "AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE";
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                    typeOfChange = "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK";
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    typeOfChange = "AUDIOFOCUS_LOSS";
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    typeOfChange = "AUDIOFOCUS_LOSS_TRANSIENT";
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    typeOfChange = "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK";
                    break;
                default:
                    typeOfChange = "AUDIOFOCUS_INVALID";
                    break;
            }
            Log.d(TAG, "onAudioFocusChange: " + typeOfChange);
        };

        // 오디오 재생 포커스를 요청하고 리스너 등록
        int result = audioManager.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_VOICE_CALL,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        );
        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            Log.d(TAG, "Audio focus request granted for VOICE_CALL streams");
        else
            Log.e(TAG, "Audio focus request failed");

        // MODE_IN_COMMUNICATION을 기본 모드로 하기를 권장
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

        // WebRTC 콜 할 때에 마이스 음소거 비 활성화
        setMicrophoneMute(false);

        // 초기 장치 상태 셋팅
        userSelectedAudioDevice = AudioDevice.NONE;
        selectedAudioDevice = AudioDevice.NONE;
        audioDevices.clear();

        // 오디오 장치 상태 변경
        updateAudioDeviceState();

        // 유선 헤드셋 추가 및 제거 관련 브로드 캐스트 등록
        registerReceiver(wiredHeadsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        Log.d(TAG, "AudioManager started");
    }

    @SuppressWarnings("deprecation")
    public void stop() {
        Log.d(TAG, "stop");
        ThreadUtils.checkIsOnMainThread();
        if (amState != AudioManagerState.RUNNING) {
            Log.e(TAG, "Trying to stop AudioManager in incorrect state: " + amState);
            return;
        }
        amState = AudioManagerState.UNINITIALIZED;

        unregisterReceiver(wiredHeadsetReceiver);

        // 이전에 저장된 오디오 상태 복원
        setSpeakerphoneOn(savedIsSpeakerPhoneOn);
        setMicrophoneMute(savedIsMicrophoneMute);
        audioManager.setMode(savedAudioMode);

        audioManager.abandonAudioFocus(audioFocusChangeListener);
        audioFocusChangeListener = null;
        Log.d(TAG, "Abandoned audio focus for VOICE_CALL streams");

        if (proximitySensor != null) {
            proximitySensor.stop();
            proximitySensor = null;
        }

        audioManagerEvents = null;
        Log.d(TAG, "AudioManager stopped");
    }

    /** 현재 활성화 된 오디오 디바이스 상태 변경 */
    private void setAudioDeviceInternal(AudioDevice device) {
        Log.d(TAG, "setAudioDeviceInternal(device=" + device + ")");
        AppRTCUtils.assertIsTrue(audioDevices.contains(device));

        switch (device) {
            case SPEAKER_PHONE:
                setSpeakerphoneOn(true);
                break;
            case EARPIECE:
                setSpeakerphoneOn(false);
                break;
            case WIRED_HEADSET:
                setSpeakerphoneOn(false);
                break;
            default:
                Log.e(TAG, "Invalid audio device selection");
                break;
        }
        selectedAudioDevice = device;
    }

    /** 기본 오디오 디바이스 변경 */
    public void setDefaultAudioDevice(AudioDevice defaultDevice) {
        ThreadUtils.checkIsOnMainThread();
        switch (defaultDevice) {
            case SPEAKER_PHONE:
                defaultAudioDevice = defaultDevice;
                break;
            case EARPIECE:
                if (hasEarpiece()) {
                    defaultAudioDevice = defaultDevice;
                } else {
                    defaultAudioDevice = AudioDevice.SPEAKER_PHONE;
                }
                break;
            default:
                Log.e(TAG, "Invalid default audio device selection");
                break;
        }
        Log.d(TAG, "setDefaultAudioDevice(device=" + defaultAudioDevice + ")");
        updateAudioDeviceState();
    }

    /** 현재 활성화 중인 오디오 디바이스 변경 */
    public void selectAudioDevice(AudioDevice device) {
        ThreadUtils.checkIsOnMainThread();
        if (!audioDevices.contains(device)) {
            Log.e(TAG, "Can not select " + device + " from available " + audioDevices);
        }
        userSelectedAudioDevice = device;
        updateAudioDeviceState();
    }

    /** 현재 이용 할 수 있는 오디오 장치 목록 */
    public Set<AudioDevice> getAudioDevices() {
        ThreadUtils.checkIsOnMainThread();
        return Collections.unmodifiableSet(new HashSet<>(audioDevices));
    }

    /** 현재 선택된 오디오 디바이스 */
    public AudioDevice getSelectedAudioDevice() {
        ThreadUtils.checkIsOnMainThread();
        return selectedAudioDevice;
    }

    /** 브로드 캐스트를 등록하기 위한 핼퍼 */
    private void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        apprtcContext.registerReceiver(receiver, filter);
    }

    /** 브로드 캐스트를 해제하기 위한 핼퍼 */
    private void unregisterReceiver(BroadcastReceiver receiver) {
        apprtcContext.unregisterReceiver(receiver);
    }

    /** 스피커 폰 모드로 셋팅 */
    private void setSpeakerphoneOn(boolean on) {
        boolean wasOn = audioManager.isSpeakerphoneOn();
        if (wasOn == on) {
            return;
        }
        audioManager.setSpeakerphoneOn(on);
    }

    /** 마이크 음소거 상태로 셋팅 */
    private void setMicrophoneMute(boolean on) {
        boolean wasMuted = audioManager.isMicrophoneMute();
        if (wasMuted == on) {
            return;
        }
        audioManager.setMicrophoneMute(on);
    }

    /** 현재 이어폰 상태 */
    private boolean hasEarpiece() {
        return apprtcContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    /** 유선 해드셋이 연결되어 있는지 확인 */
    @Deprecated
    private boolean hasWiredHeadset() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return audioManager.isWiredHeadsetOn();
        } else {
            final AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
            for (AudioDeviceInfo device : devices) {
                final int type = device.getType();
                if (type == AudioDeviceInfo.TYPE_WIRED_HEADSET) {
                    Log.d(TAG, "hasWiredHeadset: found wired headset");
                    return true;
                } else if (type == AudioDeviceInfo.TYPE_USB_DEVICE) {
                    Log.d(TAG, "hasWiredHeadset: found USB audio device");
                    return true;
                }
            }
            return false;
        }
    }

    /** 이용 가능한 오디오 장치 목록을 업데이트하고 새로운 디바이스 선택 */
    public void updateAudioDeviceState() {
        ThreadUtils.checkIsOnMainThread();
        Log.d(TAG, "Device status: "
                + "available=" + audioDevices + ", "
                + "selected=" + selectedAudioDevice + ", "
                + "user selected=" + userSelectedAudioDevice);

        // 이용가능한 오디오 디바이스 목록 업데이트
        Set<AudioDevice> newAudioDevices = new HashSet<>();

        if(hasWiredHeadset) {
            // 유선 헤드셋일 경우
            newAudioDevices.add(AudioDevice.WIRED_HEADSET);
        } else {
            // 스피커폰인 경우
            newAudioDevices.add(AudioDevice.SPEAKER_PHONE);
            if(hasEarpiece())
                newAudioDevices.add(AudioDevice.EARPIECE);
        }

        // 장치 목록 변경되면 true로 셋팅
        boolean audioDeviceSetUpdated = !audioDevices.equals(newAudioDevices);

        // 기존 오디오 장치 업데이트
        audioDevices = newAudioDevices;

        if(hasWiredHeadset && userSelectedAudioDevice == AudioDevice.SPEAKER_PHONE) {
            // 스피커 폰을 선택한 다음 유선 헤드셋을 선택한 경우
            userSelectedAudioDevice = AudioDevice.WIRED_HEADSET;
        }
        if (!hasWiredHeadset && userSelectedAudioDevice == AudioDevice.WIRED_HEADSET) {
            // 유선 헤드셋을 선택한 다음 스피커 폰을 선택한 경우
            userSelectedAudioDevice = AudioDevice.SPEAKER_PHONE;
        }

        // 선택한 오디오 장치 업데이트
        final AudioDevice newAudioDevice;

        if(hasWiredHeadset) {
            // 유선 헤드셋에 연결 된 경우 오디오 장치로 사용
            newAudioDevice = AudioDevice.WIRED_HEADSET;
        } else {
            // 스피커폰을 오디오 장치로 사용
            newAudioDevice = defaultAudioDevice;
        }

        // 변경 사항이 있을시 새로운 장치로 변경
        if(newAudioDevice != selectedAudioDevice || audioDeviceSetUpdated) {
            // 필요 장치 전환
            setAudioDeviceInternal(newAudioDevice);
            Log.d(TAG, "New device status: "
                    + "available=" + audioDevices + ", "
                    + "selected=" + newAudioDevice);
            if(audioManagerEvents != null) {
                // 오디오 장치의 변경이 있음을 알림
                audioManagerEvents.onAudioDeviceChanged(selectedAudioDevice, audioDevices);
            }
        }
        Log.d(TAG, "--- updateAudioDeviceState done");
    }

}
