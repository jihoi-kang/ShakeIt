package com.example.kjh.shakeit.main.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.adapter.ChatListAdapter;
import com.example.kjh.shakeit.main.chat.contract.ChatContract;
import com.example.kjh.shakeit.main.chat.presenter.ChatPresenter;
import com.example.kjh.shakeit.utils.Injector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ChatActivity extends AppCompatActivity implements ChatContract.View {

    private final String TAG = ChatActivity.class.getSimpleName();

    private ChatContract.Presenter presenter;

    private User user;
    private ChatRoom room;

    private Unbinder unbinder;
    @BindView(R.id.inputContent) EditText inputContent;
    @BindView(R.id.chat_list) RecyclerView chatListView;

    private ChatListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        unbinder = ButterKnife.bind(this);

        presenter = new ChatPresenter(this, Injector.provideChatModel());

        Intent intent = getIntent();
        room = (ChatRoom) intent.getSerializableExtra("room");
        user = (User) intent.getSerializableExtra("user");

        presenter.onCreate();

        /** Chat List RecyclerView */
//        chatListView.setHasFixedSize(true);
//
//        layoutManager = new LinearLayoutManager(this);
//        chatListView.setLayoutManager(layoutManager);
//
//        adapter = new ChatListAdapter(this, chatList);
//        chatListView.setAdapter(adapter);
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
}
