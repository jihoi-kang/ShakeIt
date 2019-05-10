package com.example.kjh.shakeit.main.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.adapter.ChatListAdapter;
import com.example.kjh.shakeit.main.chat.contract.ChatContract;
import com.example.kjh.shakeit.main.chat.presenter.ChatPresenter;
import com.example.kjh.shakeit.utils.ImageLoaderUtil;
import com.example.kjh.shakeit.utils.Injector;
import com.example.kjh.shakeit.utils.Serializer;
import com.example.kjh.shakeit.utils.StrUtil;
import com.example.kjh.shakeit.utils.ToastGenerator;

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
    @BindView(R.id.title) TextView title;
    @BindView(R.id.profile_image) ImageView profileImage;
    @BindView(R.id.btn_send) Button sendBtn;

    private ChatListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<ChatHolder> chatList = new ArrayList<>();

    public static Handler chatActHandler;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        unbinder = ButterKnife.bind(this);

        presenter = new ChatPresenter(this, Injector.provideChatModel());
        presenter.onCreate();

        intent = getIntent();

        /** 채팅목록 RecyclerView */
        chatListView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        chatListView.setLayoutManager(layoutManager);

        adapter = new ChatListAdapter(this, chatList);
        chatListView.setAdapter(adapter);
    }

    /**------------------------------------------------------------------
     생명주기 ==> onResume()
     ------------------------------------------------------------------*/
    @Override
    protected void onResume() {
        super.onResume();
        room = (ChatRoom) intent.getSerializableExtra("room");
        user = (User) intent.getSerializableExtra("user");
        String imageUrl = intent.getStringExtra("imageUrl");

        /** 셋팅 */
        title.setText(intent.getStringExtra("title"));

        if(StrUtil.isBlank(imageUrl))
            profileImage.setImageResource(R.drawable.ic_basic_profile);
        else
            ImageLoaderUtil.display(this, profileImage, imageUrl);

        chatActHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                notifyChat(msg.getData().getString("result"));
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

    /**------------------------------------------------------------------
     메서드 ==> 소켓 서버에서 채팅 데이터 받을 시 채팅 추가
     ------------------------------------------------------------------*/
    @Override
    public void notifyChat(String body) {
        MessageHolder holder = Serializer.deserialize(body, MessageHolder.class);
        ChatHolder chatHolder = Serializer.deserialize(holder.getBody(), ChatHolder.class);

        chatList.add(chatHolder);
        adapter.notifyDataSetChanged();
        chatListView.scrollToPosition(chatList.size() - 1);
    }

    @Override
    public void clearInputContent() {
        runOnUiThread(() -> inputContent.setText(""));
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
    public void showMessageForFailure() {
        ToastGenerator.show(R.string.msg_for_failure);
    }
}
