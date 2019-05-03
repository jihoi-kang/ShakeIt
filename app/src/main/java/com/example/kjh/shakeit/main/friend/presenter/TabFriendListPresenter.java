package com.example.kjh.shakeit.main.friend.presenter;

import android.util.Log;

import com.example.kjh.shakeit.callback.ResultCallback;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.friend.contract.TabFriendListContract;
import com.example.kjh.shakeit.utils.Serializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TabFriendListPresenter implements TabFriendListContract.Presenter {

    private final String TAG = TabFriendListPresenter.class.getSimpleName();

    private TabFriendListContract.View view;
    private TabFriendListContract.Model model;

    private ArrayList<User> friendList;

    public TabFriendListPresenter(TabFriendListContract.View view, TabFriendListContract.Model model) {
        this.view = view;
        this.model = model;

        friendList = new ArrayList<>();
    }

    /**------------------------------------------------------------------
     메서드 ==> 친구목록 요청해서 가져오기
     ------------------------------------------------------------------*/
    @Override
    public void getFriendList() {
        friendList.clear();

        User user = view.getUser();

        friendList.add(user);

        model.getFriendList(user.get_id(), new ResultCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONObject jsonObject = new JSONObject(body);

                    JSONArray jsonArray = jsonObject.getJSONArray("message");

                    for(int i = 0; i < jsonArray.length(); i++) {
                        String jsonString = jsonArray.getJSONObject(i).toString();

                        User friend = Serializer.deserialize(jsonString, User.class);
                        friendList.add(friend);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setFriendList();
            }

            @Override
            public void onFailure(String errorMsg) {
                Log.d(TAG, "onFailure => " + errorMsg);
            }
        });
    }

    /**------------------------------------------------------------------
     메서드 ==> 가져온 친구 목록 셋
     ------------------------------------------------------------------*/
    @Override
    public void setFriendList() {
        friendList.remove(0);
        friendList.add(0, view.getUser());

        view.showFriendList(friendList);
    }
}
