package com.example.kjh.shakeit.main.chat;

import android.content.Context;
import android.os.Bundle;
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
import com.example.kjh.shakeit.utils.MyDividerItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 채팅룸 목록 탭
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 27. PM 5:32
 **/
public class TabChatRoomListFragment extends Fragment implements TabChatRoomListContract.View {

    private static TabChatRoomListFragment instance = null;

    private TabChatRoomListContract.Presenter presenter;

    private View view;
    private User user;

    private Unbinder unbinder;
    @BindView(R.id.chatroom_list) RecyclerView chatRoomListView;

    private ChatRoomListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

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

        presenter.getChatRoomList();
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

        chatRoomListView.addItemDecoration(new MyDividerItemDecoration(getActivity()));

        presenter.setChatRoomList();

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

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void showChatRoomList(ArrayList<ChatRoom> roomList) {
        adapter = new ChatRoomListAdapter(getActivity(), roomList, user);
        chatRoomListView.setAdapter(adapter);
    }
}
