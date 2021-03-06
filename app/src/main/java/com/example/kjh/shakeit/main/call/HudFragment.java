package com.example.kjh.shakeit.main.call;

import android.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.webrtc.CpuMonitor;
import com.example.kjh.shakeit.webrtc.PeerConnectionClient;
import com.squareup.otto.Subscribe;

import org.webrtc.StatsReport;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.kjh.shakeit.app.Constant.EXTRA_DISPLAY_HUD;
import static com.example.kjh.shakeit.app.Constant.EXTRA_VIDEO_CALL;

/**
 * 사용자가 컨트롤 할 수 없는 Fragment 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 27. PM 8:51
 **/
public class HudFragment extends Fragment {

    private Unbinder unbinder;
    @BindView(R.id.encoder_stat_call) TextView encoderStatView;
    @BindView(R.id.hud_stat_bwe) TextView hudViewBwe;
    @BindView(R.id.hud_stat_connection) TextView hudViewConnection;
    @BindView(R.id.hud_stat_video_send) TextView hudViewVideoSend;
    @BindView(R.id.hud_stat_video_recv) TextView hudViewVideoRecv;
    @BindView(R.id.button_toggle_debug) ImageButton toggleDebugButton;
    @BindView(R.id.chronometer) Chronometer chronometerView;
    @BindView(R.id.name) TextView nameTxt;
    @BindView(R.id.calling) TextView callingTxt;

    private boolean videoCallEnabled;
    private boolean displayHud;
    private volatile boolean isRunning;
    private CpuMonitor cpuMonitor;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreateView()
     ------------------------------------------------------------------*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View controlView = inflater.inflate(R.layout.fragment_hud, container, false);

        /** Create UI Controls */
        unbinder = ButterKnife.bind(this, controlView);

        return controlView;
    }

    /**------------------------------------------------------------------
     생명주기 ==> onStart()
     ------------------------------------------------------------------*/
    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        // 이름 셋팅
        User otherUser = (User) args.getSerializable("otherUser");
        nameTxt.setText("" + otherUser.getName());
        if (args != null) {
            videoCallEnabled = args.getBoolean(EXTRA_VIDEO_CALL, true);
            displayHud = args.getBoolean(EXTRA_DISPLAY_HUD, false);
        }
        int visibility = displayHud ? View.VISIBLE : View.INVISIBLE;
        encoderStatView.setVisibility(visibility);
        toggleDebugButton.setVisibility(visibility);
        hudViewsSetProperties(View.INVISIBLE);
        isRunning = true;
    }

    /**------------------------------------------------------------------
     생명주기 ==> onStop()
     ------------------------------------------------------------------*/
    @Override
    public void onStop() {
        isRunning = false;
        super.onStop();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    public void onDestroy() {
        super.onDestroy();

        chronometerView.stop();

        unbinder.unbind();

        BusProvider.getInstance().unregister(this);
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 토글 디버그
     ------------------------------------------------------------------*/
    @OnClick(R.id.button_toggle_debug)
    void onClickToggleDebug() {
        if (displayHud) {
            int visibility =
                    (hudViewBwe.getVisibility() == View.VISIBLE) ? View.INVISIBLE : View.VISIBLE;
            hudViewsSetProperties(visibility);
        }
    }

    public void setCpuMonitor(CpuMonitor cpuMonitor) {
        this.cpuMonitor = cpuMonitor;
    }

    private void hudViewsSetProperties(int visibility) {
        hudViewBwe.setVisibility(visibility);
        hudViewConnection.setVisibility(visibility);
        hudViewVideoSend.setVisibility(visibility);
        hudViewVideoRecv.setVisibility(visibility);
        hudViewBwe.setTextSize(TypedValue.COMPLEX_UNIT_PT, 5);
        hudViewConnection.setTextSize(TypedValue.COMPLEX_UNIT_PT, 5);
        hudViewVideoSend.setTextSize(TypedValue.COMPLEX_UNIT_PT, 5);
        hudViewVideoRecv.setTextSize(TypedValue.COMPLEX_UNIT_PT, 5);
    }

    private Map<String, String> getReportMap(StatsReport report) {
        Map<String, String> reportMap = new HashMap<>();
        for (StatsReport.Value value : report.values) {
            reportMap.put(value.name, value.value);
        }
        return reportMap;
    }

    public void updateEncoderStatistics(final StatsReport[] reports) {
        if (!isRunning || !displayHud) {
            return;
        }
        StringBuilder encoderStat = new StringBuilder(128);
        StringBuilder bweStat = new StringBuilder();
        StringBuilder connectionStat = new StringBuilder();
        StringBuilder videoSendStat = new StringBuilder();
        StringBuilder videoRecvStat = new StringBuilder();
        String fps = null;
        String targetBitrate = null;
        String actualBitrate = null;

        for (StatsReport report : reports) {
            if (report.type.equals("ssrc") && report.id.contains("ssrc") && report.id.contains("send")) {
                // Send video statistics.
                Map<String, String> reportMap = getReportMap(report);
                String trackId = reportMap.get("googTrackId");
                if (trackId != null && trackId.contains(PeerConnectionClient.VIDEO_TRACK_ID)) {
                    fps = reportMap.get("googFrameRateSent");
                    videoSendStat.append(report.id).append("\n");
                    for (StatsReport.Value value : report.values) {
                        String name = value.name.replace("goog", "");
                        videoSendStat.append(name).append("=").append(value.value).append("\n");
                    }
                }
            } else if (report.type.equals("ssrc") && report.id.contains("ssrc")
                    && report.id.contains("recv")) {
                // Receive video statistics.
                Map<String, String> reportMap = getReportMap(report);
                // Check if this stat is for video track.
                String frameWidth = reportMap.get("googFrameWidthReceived");
                if (frameWidth != null) {
                    videoRecvStat.append(report.id).append("\n");
                    for (StatsReport.Value value : report.values) {
                        String name = value.name.replace("goog", "");
                        videoRecvStat.append(name).append("=").append(value.value).append("\n");
                    }
                }
            } else if (report.id.equals("bweforvideo")) {
                // BWE statistics.
                Map<String, String> reportMap = getReportMap(report);
                targetBitrate = reportMap.get("googTargetEncBitrate");
                actualBitrate = reportMap.get("googActualEncBitrate");

                bweStat.append(report.id).append("\n");
                for (StatsReport.Value value : report.values) {
                    String name = value.name.replace("goog", "").replace("Available", "");
                    bweStat.append(name).append("=").append(value.value).append("\n");
                }
            } else if (report.type.equals("googCandidatePair")) {
                // Connection statistics.
                Map<String, String> reportMap = getReportMap(report);
                String activeConnection = reportMap.get("googActiveConnection");
                if (activeConnection != null && activeConnection.equals("true")) {
                    connectionStat.append(report.id).append("\n");
                    for (StatsReport.Value value : report.values) {
                        String name = value.name.replace("goog", "");
                        connectionStat.append(name).append("=").append(value.value).append("\n");
                    }
                }
            }
        }
        hudViewBwe.setText(bweStat.toString());
        hudViewConnection.setText(connectionStat.toString());
        hudViewVideoSend.setText(videoSendStat.toString());
        hudViewVideoRecv.setText(videoRecvStat.toString());

        if (videoCallEnabled) {
            if (fps != null) {
                encoderStat.append("Fps:  ").append(fps).append("\n");
            }
            if (targetBitrate != null) {
                encoderStat.append("Target BR: ").append(targetBitrate).append("\n");
            }
            if (actualBitrate != null) {
                encoderStat.append("Actual BR: ").append(actualBitrate).append("\n");
            }
        }

        if (cpuMonitor != null) {
            encoderStat.append("CPU%: ")
                    .append(cpuMonitor.getCpuUsageCurrent())
                    .append("/")
                    .append(cpuMonitor.getCpuUsageAverage())
                    .append(". Freq: ")
                    .append(cpuMonitor.getFrequencyScaleAverage());
        }
        encoderStatView.setText(encoderStat.toString());
    }

    /**------------------------------------------------------------------
     메서드 ==> 통화 시간 기록
     ------------------------------------------------------------------*/
    private void countTime() {
        getActivity().runOnUiThread(() -> {
            chronometerView.setBase(SystemClock.elapsedRealtime());
            chronometerView.start();
            chronometerView.setVisibility(View.VISIBLE);

            chronometerView.setOnChronometerTickListener(cArg -> {
                long time = SystemClock.elapsedRealtime() - cArg.getBase();

                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                String hh = h < 10 ? "0" + h : h + "";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
                cArg.setText(hh + ":" + mm + ":" + ss);
            });
        });

    }

    /**------------------------------------------------------------------
     구독이벤트 ==> 통화 시작 되었음을 알림
     ------------------------------------------------------------------*/
    @Subscribe
    public void getCallStartEvent(Events.webRTCEvent event) {
        String message = event.getMessage();

        if(message.equals("start")) {
            /** 영상통화 대기 할 때에 보여준 View 숨기기  */
            getActivity().runOnUiThread(() -> {
                nameTxt.setVisibility(View.GONE);
                callingTxt.setVisibility(View.GONE);
                chronometerView.setVisibility(View.VISIBLE);
            });

            countTime();
        }
    }

}
