package com.example.kjh.shakeit.main.friend;

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
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.MainActivity;
import com.example.kjh.shakeit.main.adapter.FriendListAdapter;
import com.example.kjh.shakeit.main.friend.contract.TabFriendListContract;
import com.example.kjh.shakeit.main.friend.presenter.TabFriendListPresenter;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.utils.Injector;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 친구목록 탭
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 27. PM 5:33
 **/
public class TabFriendListFragment extends Fragment implements TabFriendListContract.View {

    private final String TAG = TabFriendListFragment.class.getSimpleName();

    private TabFriendListContract.Presenter presenter;

    private static TabFriendListFragment instance = null;

    private Unbinder unbinder;
    @BindView(R.id.friend_list) RecyclerView friendListView;

    private View view;
    private User user;

    private FriendListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public static TabFriendListFragment getInstance() {
        if(instance == null)
            instance = new TabFriendListFragment();

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
        presenter = new TabFriendListPresenter(this, Injector.provideFriendListModel());

        BusProvider.getInstance().register(this);

        /** 친구목록 가져오기 */
        presenter.getFriendList();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreateView()
     ------------------------------------------------------------------*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        /** Friend List RecyclerView */
        friendListView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        friendListView.setLayoutManager(layoutManager);

        presenter.setFriendList();

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
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void showFriendList(ArrayList<User> friendList) {
        adapter = new FriendListAdapter(getActivity(), friendList, TabFriendListFragment.class.getSimpleName(), null);
        friendListView.setAdapter(adapter);
    }

    @Override
    public User getUser() {
        return user;
    }

    /**------------------------------------------------------------------
     구독이벤트 ==> 프로필 변경시 발생
     ------------------------------------------------------------------*/
    @Subscribe
    public void getUpdateProfileInfo(Events.updateProfileEvent event) {
        user = event.getUser();
    }

    /**------------------------------------------------------------------
     구독이벤트 ==> 친구 추가시 발생
     ------------------------------------------------------------------*/
    @Subscribe
    public void getMessage(Events.noticeEventStr event){
        if(event.getMessage().equals("addFriend")) {
            presenter.getFriendList();
            presenter.setFriendList();
        }
    }

}

