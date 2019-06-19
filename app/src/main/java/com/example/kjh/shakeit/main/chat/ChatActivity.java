package com.example.kjh.shakeit.main.chat;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.app.App;
import com.example.kjh.shakeit.app.AppManager;
import com.example.kjh.shakeit.cash.WireCashActivity;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.adapter.ChatListAdapter;
import com.example.kjh.shakeit.main.adapter.FriendListAdapter;
import com.example.kjh.shakeit.main.call.CallActivity;
import com.example.kjh.shakeit.main.call.CallWaitActivity;
import com.example.kjh.shakeit.main.chat.contract.ChatContract;
import com.example.kjh.shakeit.main.chat.presenter.ChatPresenter;
import com.example.kjh.shakeit.main.more.ImageFilterActivity;
import com.example.kjh.shakeit.utils.FileUtil;
import com.example.kjh.shakeit.utils.ImageCombiner;
import com.example.kjh.shakeit.utils.ImageLoaderUtil;
import com.example.kjh.shakeit.utils.Injector;
import com.example.kjh.shakeit.utils.StrUtil;
import com.example.kjh.shakeit.utils.ToastGenerator;
import com.soundcloud.android.crop.Crop;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rebus.bottomdialog.BottomDialog;

import static com.example.kjh.shakeit.app.Constant.AEC_DUMP;
import static com.example.kjh.shakeit.app.Constant.AUDIO_CODEC;
import static com.example.kjh.shakeit.app.Constant.AUDIO_START_BITRATE;
import static com.example.kjh.shakeit.app.Constant.CAPTURE_QUALITY_SLIDER;
import static com.example.kjh.shakeit.app.Constant.CAPTURE_TO_TEXTURE;
import static com.example.kjh.shakeit.app.Constant.DATA_CHANNEL_ENABLED;
import static com.example.kjh.shakeit.app.Constant.DEFAULT_CAMERA_FPS;
import static com.example.kjh.shakeit.app.Constant.DISABLE_BUILT_IN_AEC;
import static com.example.kjh.shakeit.app.Constant.DISABLE_BUILT_IN_AGC;
import static com.example.kjh.shakeit.app.Constant.DISABLE_BUILT_IN_NS;
import static com.example.kjh.shakeit.app.Constant.DISABLE_WEBRTC_AGC_AND_HPE;
import static com.example.kjh.shakeit.app.Constant.DISPLAY_HUD;
import static com.example.kjh.shakeit.app.Constant.ENABLE_LEVEL_CONTROL;
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
import static com.example.kjh.shakeit.app.Constant.EXTRA_DISPLAY_HUD;
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
import static com.example.kjh.shakeit.app.Constant.EXTRA_VIDEOCODEC;
import static com.example.kjh.shakeit.app.Constant.EXTRA_VIDEO_BITRATE;
import static com.example.kjh.shakeit.app.Constant.EXTRA_VIDEO_CALL;
import static com.example.kjh.shakeit.app.Constant.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED;
import static com.example.kjh.shakeit.app.Constant.EXTRA_VIDEO_FILE_AS_CAMERA;
import static com.example.kjh.shakeit.app.Constant.EXTRA_VIDEO_FPS;
import static com.example.kjh.shakeit.app.Constant.EXTRA_VIDEO_HEIGHT;
import static com.example.kjh.shakeit.app.Constant.EXTRA_VIDEO_WIDTH;
import static com.example.kjh.shakeit.app.Constant.FLEXFEC_ENABLED;
import static com.example.kjh.shakeit.app.Constant.HD_VIDEO_HEIGHT;
import static com.example.kjh.shakeit.app.Constant.HD_VIDEO_WIDTH;
import static com.example.kjh.shakeit.app.Constant.HW_CODEC;
import static com.example.kjh.shakeit.app.Constant.ID;
import static com.example.kjh.shakeit.app.Constant.MAX_RETR;
import static com.example.kjh.shakeit.app.Constant.MAX_RETR_MS;
import static com.example.kjh.shakeit.app.Constant.NEGOTIATED;
import static com.example.kjh.shakeit.app.Constant.NO_AUDIO_PROCESSING;
import static com.example.kjh.shakeit.app.Constant.ORDERED;
import static com.example.kjh.shakeit.app.Constant.PROTOCOL;
import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_CAMERA;
import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_CHAT_TO_CALL_WAIT;
import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_CHAT_TO_PROFILE_DETAIL;
import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_GALLERY;
import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_UPDATE_PROFILE_TO_IMAGE_FILTER;
import static com.example.kjh.shakeit.app.Constant.REQUEST_CONNECTION;
import static com.example.kjh.shakeit.app.Constant.ROOM_URL;
import static com.example.kjh.shakeit.app.Constant.TRACING;
import static com.example.kjh.shakeit.app.Constant.USE_CAMERA_2;
import static com.example.kjh.shakeit.app.Constant.USE_OPENSLES;
import static com.example.kjh.shakeit.app.Constant.USE_SCREEN_CAPTURE;
import static com.example.kjh.shakeit.app.Constant.VIDEO_CALL_ENABLED;
import static com.example.kjh.shakeit.app.Constant.VIDEO_CODEC;
import static com.example.kjh.shakeit.app.Constant.VIDEO_START_BITRATE;

/**
 * 채팅 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 6. 12. PM 5:38
 **/
public class ChatActivity extends AppCompatActivity implements ChatContract.View {

    private final String TAG = ChatActivity.class.getSimpleName();

    private ChatContract.Presenter presenter;

    private User user;
    private ChatRoom room;
    private Intent intent;

    private File file;
    private String path;
    private int tempOrientation = 0;

    private Unbinder unbinder;
    @BindView(R.id.inputContent) EditText inputContent;
    @BindView(R.id.chat_list) RecyclerView chatListView;
    @BindView(R.id.title) TextView titleTxt;
    @BindView(R.id.profile_image) ImageView profileImage;
    @BindView(R.id.send) ImageView sendImage;
    @BindView(R.id.dl_root) DrawerLayout dlRoot;
    @BindView(R.id.fl_right_side) FrameLayout rightSideLayout;
    @BindView(R.id.participants) RecyclerView participantListView;

    private ChatListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FriendListAdapter participantListAdapter;
    private RecyclerView.LayoutManager participantListLayoutManager;

    private ArrayList<ChatHolder> chats = new ArrayList<>();

    public static Handler chatActHandler;

    /** WebRTC 관련 변수 */
    private static boolean commandLineRun = false;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        unbinder = ButterKnife.bind(this);

        intent = getIntent();
        room = (ChatRoom) intent.getSerializableExtra("room");
        user = (User) intent.getSerializableExtra("user");
        App.getApplication().setRoomId(room.getRoomId());

        presenter = new ChatPresenter(this, Injector.provideChatModel());

        /** 채팅목록 RecyclerView */
        chatListView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        chatListView.setLayoutManager(layoutManager);

        adapter = new ChatListAdapter(this, chats, room);
        chatListView.setAdapter(adapter);

        /** 참여자목록 RecyclerView */
        participantListView.setHasFixedSize(true);

        participantListLayoutManager = new LinearLayoutManager(this);
        participantListView.setLayoutManager(participantListLayoutManager);

        ArrayList<User> participants = (ArrayList<User>) room.getParticipants().clone();
        participants.add(0, user);

        participantListAdapter = new FriendListAdapter(this, participants, ChatActivity.class.getSimpleName(), null);
        participantListView.setAdapter(participantListAdapter);

        presenter.onCreate();

        chatActHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ArrayList<ChatHolder> chats = (ArrayList<ChatHolder>) msg.obj;
                showChatList(chats);
            }
        };
    }

    /**------------------------------------------------------------------
     생명주기 ==> onResume()
     ------------------------------------------------------------------*/
    @Override
    protected void onResume() {
        super.onResume();

        presenter.onResume();

        /** 대표사진 셋팅 */
        if(intent.getByteArrayExtra("imageArray") != null) {
            byte[] imageByteArray = intent.getByteArrayExtra("imageArray");
            profileImage.setImageBitmap(BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length));
        } else {
            new Thread(() -> {
                Bitmap resultImage;
                if(room.getParticipants().size() == 1) {
                    resultImage = makeBitmap(room.getParticipants().get(0).getImageUrl());
                } else if(room.getParticipants().size() == 2) {
                    resultImage = ImageCombiner.combine(
                            makeBitmap(room.getParticipants().get(0).getImageUrl()),
                            makeBitmap(room.getParticipants().get(1).getImageUrl())
                    );
                } else if(room.getParticipants().size() == 3) {
                    resultImage = ImageCombiner.combine(
                            makeBitmap(room.getParticipants().get(0).getImageUrl()),
                            makeBitmap(room.getParticipants().get(1).getImageUrl()),
                            makeBitmap(room.getParticipants().get(2).getImageUrl())
                    );
                } else {
                    resultImage = ImageCombiner.combine(
                            makeBitmap(room.getParticipants().get(0).getImageUrl()),
                            makeBitmap(room.getParticipants().get(1).getImageUrl()),
                            makeBitmap(room.getParticipants().get(2).getImageUrl()),
                            makeBitmap(room.getParticipants().get(3).getImageUrl())
                    );
                }

                Bitmap finalResultImage = resultImage;
                runOnUiThread(() -> profileImage.setImageBitmap(finalResultImage));
            }).start();
        }

        /** 채팅 제목 셋팅 */
        String title = "";
        for(int index = 0; index < room.getParticipants().size(); index++)
            title += (index == (room.getParticipants().size() - 1)) ? room.getParticipants().get(index).getName() : room.getParticipants().get(index).getName() + ", ";

        if(title.length() > 13) {
            title = title.substring(0, 13);
            title += "...";
        }

        titleTxt.setText(title);

        /** 소프트 키보드 올라오면 채팅목록 마지막에 포커스 */
        KeyboardVisibilityEvent.setEventListener(this, isOpen -> chatListView.scrollToPosition(chats.size() - 1));
    }

    /**------------------------------------------------------------------
     생명주기 ==> onStop()
     ------------------------------------------------------------------*/
    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    protected void onDestroy() {
        AppManager.getAppManager().removeActivity(this);
        App.getApplication().setRoomId(0);

        super.onDestroy();
        unbinder.unbind();
        presenter.onDestroy();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 소프트키 뒤로가기
     ------------------------------------------------------------------*/
    @Override
    public void onBackPressed() {
        if(dlRoot.isDrawerOpen(rightSideLayout))
            dlRoot.closeDrawer(rightSideLayout);
        else
            super.onBackPressed();

    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 전송
     ------------------------------------------------------------------*/
    @OnClick(R.id.send)
    void onClickSend() {
        presenter.onClickSend();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 하단 왼쪽 + 누를시
     ------------------------------------------------------------------*/
    @OnClick(R.id.attach)
    void onClickAttach() {
        presenter.onClickAttach();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 뒤로가기
     ------------------------------------------------------------------*/
    @OnClick(R.id.back)
    void onClickBack() {
        finish();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 메뉴
     ------------------------------------------------------------------*/
    @OnClick(R.id.menu)
    void onClickMenu() {
        dlRoot.openDrawer(rightSideLayout);
    }

    /**------------------------------------------------------------------
     콜백이벤트 ==> onActivityResult()
     ------------------------------------------------------------------*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;

        /** CallWaitActivity 에서 돌아 왔을 때 */
        if(requestCode == REQUEST_CODE_CHAT_TO_CALL_WAIT)
            presenter.toCall(data.getStringExtra("roomID"));

        /** ProfileDetailActivity 에서 1:1채팅 눌렀을 때 */
        else if(requestCode == REQUEST_CODE_CHAT_TO_PROFILE_DETAIL) {
            Intent intent = new Intent();
            intent.putExtra("friend", data.getSerializableExtra("friend"));
            setResult(RESULT_OK, intent);
            finish();
        }
        /** 카메라 찍은후 */
        else if(requestCode == REQUEST_CODE_CAMERA) {
            Intent media_scan_intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            media_scan_intent.setData(Uri.fromFile(file));
            sendBroadcast(media_scan_intent);

            try {
                ExifInterface ei = new ExifInterface(file.getAbsolutePath());
                tempOrientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Crop.of(Uri.fromFile(file), Uri.fromFile(file)).asSquare().start(this);
        }
        /** 갤러리에서 선택한 후 */
        else if(requestCode == REQUEST_CODE_GALLERY) {
            path = uriToPath(data.getData());

            Intent toImageFilterGallery = new Intent(ChatActivity.this, ImageFilterActivity.class);
            toImageFilterGallery.putExtra("path", path);
            startActivityForResult(toImageFilterGallery, REQUEST_CODE_UPDATE_PROFILE_TO_IMAGE_FILTER);
        }
        /** 이미지 크롭한 후 */
        else if(requestCode == Crop.REQUEST_CROP) {
            path = file.getAbsolutePath();

            Intent toImageFilterCamera = new Intent(ChatActivity.this, ImageFilterActivity.class);
            toImageFilterCamera.putExtra("path", path);
            toImageFilterCamera.putExtra("orientation", tempOrientation);
            startActivityForResult(toImageFilterCamera, REQUEST_CODE_UPDATE_PROFILE_TO_IMAGE_FILTER);
        }
        /** 이미지 필터 씌운 후 */
        else if(requestCode == REQUEST_CODE_UPDATE_PROFILE_TO_IMAGE_FILTER) {
            path = uriToPath(data.getParcelableExtra("uri"));
            presenter.sendImage(path);
        }

    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public ChatRoom getChatRoom() {
        return room;
    }

    @Override
    public String getInputContent() {
        return inputContent.getText().toString();
    }

    @Override
    public void clearInputContent() {
        inputContent.setText("");
    }

    @Override
    public void setChatRoomId(int roomId) {
        room.setRoomId(roomId);
        App.getApplication().setRoomId(room.getRoomId());
    }

    @Override
    public void showSelectType() {
        BottomDialog dialog = new BottomDialog(this);
        dialog.canceledOnTouchOutside(true);
        dialog.cancelable(true);
        /** 개인채팅인지 다중채팅인지에 따라 메뉴 아이템 다르게 셋팅 */
        if(room.getParticipants().size() == 1)
            dialog.inflateMenu(R.menu.menu_attach_direct);
        else
            dialog.inflateMenu(R.menu.menu_attach_multi);

        dialog.setOnItemSelectedListener(id -> {
            switch (id) {
                /** 카메라 */
                case R.id.action_camera:
                    file = FileUtil.create();
                    presenter.onClickCamera();
                    return true;
                /** 갤러리 */
                case R.id.action_gallery:
                    presenter.onClickGallery();
                    return true;
                /** 영상통화 */
                case R.id.action_call:
                    presenter.toCall(null);
                    return true;
                /** 송금 */
                case R.id.action_wire:
                    Intent intent = new Intent(ChatActivity.this, WireCashActivity.class);
                    intent.putExtra("user", user);
                    intent.putExtra("otherUser", room.getParticipants().get(0));
                    intent.putExtra("from", ChatActivity.class.getSimpleName());
                    intent.putExtra("room", room);
                    startActivity(intent);
                    return true;
                default:
                    return false;
            }
        });
        dialog.show();
    }

    @Override
    public void showChatList(ArrayList<ChatHolder> holders) {
        chats = holders;

        adapter.changeList(chats);
        adapter.notifyDataSetChanged();
        chatListView.scrollToPosition(chats.size() - 1);
    }

    @Override
    public void showMessageForFailure() {
        ToastGenerator.show(R.string.msg_for_failure);
    }

    @Override
    public void goCallWaitActivity(String roomID) {
        Intent intent = new Intent(ChatActivity.this, CallWaitActivity.class);
        intent.putExtra("roomID", roomID);
        intent.putExtra("otherUser", room.getParticipants().get(0));
        startActivityForResult(intent, REQUEST_CODE_CHAT_TO_CALL_WAIT);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public File getFile() {
        if(file == null)
            file = FileUtil.create();

        return file;
    }

    /**------------------------------------------------------------------
     메서드 ==> 영상통화 방과 연결
     ------------------------------------------------------------------*/
    @Override
    public void connectToRoom(String roomId, boolean commandLineRun, boolean loopback,
                               boolean useValuesFromIntent, int runTimeMs, String type) {
        this.commandLineRun = commandLineRun;

        if (loopback)
            roomId = Integer.toString((new Random()).nextInt(100000000));

        // Start AppRTCMobile activity.
        Log.d(TAG, "Connecting to room " + roomId + " at URL " + ROOM_URL);
        Uri uri = Uri.parse(ROOM_URL);
        Intent intent = new Intent(ChatActivity.this, CallActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("otherUser", room.getParticipants().get(0));
        intent.putExtra("type", type);
        intent.setData(uri);
        intent.putExtra(EXTRA_ROOMID, roomId);
        intent.putExtra(EXTRA_LOOPBACK, loopback);
        intent.putExtra(EXTRA_VIDEO_CALL, VIDEO_CALL_ENABLED);
        intent.putExtra(EXTRA_SCREENCAPTURE, USE_SCREEN_CAPTURE);
        intent.putExtra(EXTRA_CAMERA2, USE_CAMERA_2);
        intent.putExtra(EXTRA_VIDEO_WIDTH, HD_VIDEO_WIDTH);
        intent.putExtra(EXTRA_VIDEO_HEIGHT, HD_VIDEO_HEIGHT);
        intent.putExtra(EXTRA_VIDEO_FPS, DEFAULT_CAMERA_FPS);
        intent.putExtra(EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, CAPTURE_QUALITY_SLIDER);
        intent.putExtra(EXTRA_VIDEO_BITRATE, VIDEO_START_BITRATE);
        intent.putExtra(EXTRA_VIDEOCODEC, VIDEO_CODEC);
        intent.putExtra(EXTRA_HWCODEC_ENABLED, HW_CODEC);
        intent.putExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, CAPTURE_TO_TEXTURE);
        intent.putExtra(EXTRA_FLEXFEC_ENABLED, FLEXFEC_ENABLED);
        intent.putExtra(EXTRA_NOAUDIOPROCESSING_ENABLED, NO_AUDIO_PROCESSING);
        intent.putExtra(EXTRA_AECDUMP_ENABLED, AEC_DUMP);
        intent.putExtra(EXTRA_OPENSLES_ENABLED, USE_OPENSLES);
        intent.putExtra(EXTRA_DISABLE_BUILT_IN_AEC, DISABLE_BUILT_IN_AEC);
        intent.putExtra(EXTRA_DISABLE_BUILT_IN_AGC, DISABLE_BUILT_IN_AGC);
        intent.putExtra(EXTRA_DISABLE_BUILT_IN_NS, DISABLE_BUILT_IN_NS);
        intent.putExtra(EXTRA_ENABLE_LEVEL_CONTROL, ENABLE_LEVEL_CONTROL);
        intent.putExtra(EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, DISABLE_WEBRTC_AGC_AND_HPE);
        intent.putExtra(EXTRA_AUDIO_BITRATE, AUDIO_START_BITRATE);
        intent.putExtra(EXTRA_AUDIOCODEC, AUDIO_CODEC);
        intent.putExtra(EXTRA_DISPLAY_HUD, DISPLAY_HUD);
        intent.putExtra(EXTRA_TRACING, TRACING);
        intent.putExtra(EXTRA_CMDLINE, commandLineRun);
        intent.putExtra(EXTRA_RUNTIME, runTimeMs);

        intent.putExtra(EXTRA_DATA_CHANNEL_ENABLED, DATA_CHANNEL_ENABLED);

        if (DATA_CHANNEL_ENABLED) {
            intent.putExtra(EXTRA_ORDERED, ORDERED);
            intent.putExtra(EXTRA_MAX_RETRANSMITS_MS, MAX_RETR_MS);
            intent.putExtra(EXTRA_MAX_RETRANSMITS, MAX_RETR);
            intent.putExtra(EXTRA_PROTOCOL, PROTOCOL);
            intent.putExtra(EXTRA_NEGOTIATED, NEGOTIATED);
            intent.putExtra(EXTRA_ID, ID);
        }

        if (useValuesFromIntent) {
            if (getIntent().hasExtra(EXTRA_VIDEO_FILE_AS_CAMERA)) {
                String videoFileAsCamera =
                        getIntent().getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA);
                intent.putExtra(EXTRA_VIDEO_FILE_AS_CAMERA, videoFileAsCamera);
            }

            if (getIntent().hasExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)) {
                String saveRemoteVideoToFile =
                        getIntent().getStringExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);
                intent.putExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE, saveRemoteVideoToFile);
            }

            if (getIntent().hasExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH)) {
                int videoOutWidth =
                        getIntent().getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
                intent.putExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, videoOutWidth);
            }

            if (getIntent().hasExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT)) {
                int videoOutHeight =
                        getIntent().getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
                intent.putExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, videoOutHeight);
            }
        }

        startActivityForResult(intent, REQUEST_CONNECTION);
    }

    /**------------------------------------------------------------------
     메서드 ==> 프로필이미지 Bitmap 반환
     ------------------------------------------------------------------*/
    private Bitmap makeBitmap(String url) {
        Bitmap bitmap;
        if(StrUtil.isBlank(url))
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_basic_profile);
        else
            bitmap = ImageLoaderUtil.getBitmap(url);

        return bitmap;
    }

    /**------------------------------------------------------------------
     메서드 ==> Uri값을 통해 Path 얻기
     ------------------------------------------------------------------*/
    private String uriToPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
