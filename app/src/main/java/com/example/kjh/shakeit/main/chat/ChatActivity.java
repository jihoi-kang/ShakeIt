package com.example.kjh.shakeit.main.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.adapter.ChatListAdapter;
import com.example.kjh.shakeit.main.chat.contract.ChatContract;
import com.example.kjh.shakeit.main.chat.presenter.ChatPresenter;
import com.example.kjh.shakeit.utils.ImageCombiner;
import com.example.kjh.shakeit.utils.ImageLoaderUtil;
import com.example.kjh.shakeit.utils.Injector;
import com.example.kjh.shakeit.utils.StrUtil;
import com.example.kjh.shakeit.utils.ToastGenerator;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rebus.bottomdialog.BottomDialog;

public class ChatActivity extends AppCompatActivity implements ChatContract.View {

    private final String TAG = ChatActivity.class.getSimpleName();

    private ChatContract.Presenter presenter;

    private User user;
    private ChatRoom room;
    private Intent intent;

    private Unbinder unbinder;
    @BindView(R.id.inputContent) EditText inputContent;
    @BindView(R.id.chat_list) RecyclerView chatListView;
    @BindView(R.id.title) TextView titleTxt;
    @BindView(R.id.profile_image) ImageView profileImage;
    @BindView(R.id.btn_send) Button sendBtn;
    @BindView(R.id.chatRootLayout) ViewGroup chatRootLayout;
    @BindView(R.id.video_call) ImageView videoCall;

    private ChatListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<ChatHolder> chats = new ArrayList<>();

    public static Handler chatActHandler;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        unbinder = ButterKnife.bind(this);

        intent = getIntent();
        room = (ChatRoom) intent.getSerializableExtra("room");
        user = (User) intent.getSerializableExtra("user");

        presenter = new ChatPresenter(this, Injector.provideChatModel());

        /** 채팅목록 RecyclerView */
        chatListView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        chatListView.setLayoutManager(layoutManager);

        adapter = new ChatListAdapter(this, chats, room);
        chatListView.setAdapter(adapter);

        presenter.onCreate();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onResume()
     ------------------------------------------------------------------*/
    @Override
    protected void onResume() {
        super.onResume();

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

        titleTxt.setText(title);

        /** 영상통화 버튼 셋팅 */
        if(room.getParticipants().size() > 1)
            videoCall.setVisibility(View.GONE);

        /** 소프트 키보드 올라오면 채팅목록 마지막에 포커스 */
        KeyboardVisibilityEvent.setEventListener(this, isOpen -> chatListView.scrollToPosition(chats.size() - 1));

        chatActHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ArrayList<ChatHolder> chats = (ArrayList<ChatHolder>) msg.obj;
                showChatList(chats);
            }
        };
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbinder.unbind();

        presenter.onDestroy();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 전송
     ------------------------------------------------------------------*/
    @OnClick(R.id.btn_send)
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

    @OnClick(R.id.video_call)
    void onClickVideoCall() {

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
    }

    @Override
    public void showSelectType() {
        BottomDialog dialog = new BottomDialog(this);
        dialog.canceledOnTouchOutside(true);
        dialog.cancelable(true);
        dialog.inflateMenu(R.menu.menu_attach);
        dialog.setOnItemSelectedListener(id -> {
            switch (id) {
                /** 카메라 선택 */
                case R.id.action_camera:
                    Log.d(TAG, "카메라 선택");
                    return true;
                /** 갤러리 선택 */
                case R.id.action_gallery:
                    Log.d(TAG, "갤러리 선택");
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

}
