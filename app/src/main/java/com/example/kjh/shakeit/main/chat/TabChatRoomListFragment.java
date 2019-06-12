package com.example.kjh.shakeit.main.chat;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.MainActivity;
import com.example.kjh.shakeit.main.adapter.ChatRoomListAdapter;
import com.example.kjh.shakeit.main.chat.contract.TabChatRoomListContract;
import com.example.kjh.shakeit.main.chat.presenter.TabChatRoomListPresenter;
import com.example.kjh.shakeit.utils.Injector;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 채팅방 목록 탭
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 27. PM 5:32
 **/
public class TabChatRoomListFragment extends Fragment implements TabChatRoomListContract.View {

    private final String TAG = TabChatRoomListFragment.class.getSimpleName();

    private static TabChatRoomListFragment instance = null;

    private TabChatRoomListContract.Presenter presenter;

    private View view;
    private User user;

    private Unbinder unbinder;
    @BindView(R.id.chatroom_list) RecyclerView chatRoomListView;

    private ChatRoomListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<ChatRoom> rooms = new ArrayList<>();

    public static Handler chatRoomFragHandler;

    public static TabChatRoomListFragment getInstance() {
        if(instance == null)
            instance = new TabChatRoomListFragment();

        return instance;
    }

    /**------------------------------------------------------------------
     생명주기 ==> onAttach()
     ------------------------------------------------------------------*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        user = ((MainActivity)getActivity()).getUser();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new TabChatRoomListPresenter(this, Injector.provideChatRoomListModel());
        presenter.onCreate();

        chatRoomFragHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ArrayList<ChatRoom> roomList = (ArrayList<ChatRoom>) msg.obj;
                showChatRoomList(roomList);
            }
        };

    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreateView()
     ------------------------------------------------------------------*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat_room_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        /** Chat Room List RecyclerView */
        chatRoomListView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        chatRoomListView.setLayoutManager(layoutManager);

        adapter = new ChatRoomListAdapter(getActivity(), rooms, user);
        chatRoomListView.setAdapter(adapter);

        return view;
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroyView()
     ------------------------------------------------------------------*/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;

        // 아답터에 변경된 User 정보 업데이트
        adapter.setUser(user);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showChatRoomList(ArrayList<ChatRoom> roomList) {
        rooms = roomList;

        // 아답터에 변경된 채팅방 정보들 업데이트
        adapter.changeList(rooms);
        adapter.notifyDataSetChanged();
    }
}
