package com.example.kjh.shakeit.main.friend.presenter;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.friend.contract.ProfileDetailContract;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileDetailPresenter implements ProfileDetailContract.Presenter {

    private ProfileDetailContract.View view;
    private ProfileDetailContract.Model model;

    public ProfileDetailPresenter(ProfileDetailContract.View view, ProfileDetailContract.Model model) {
        this.view = view;
        this.model = model;
    }

    /**------------------------------------------------------------------
     메서드 ==> 친구인지 확인
     ------------------------------------------------------------------*/
    @Override
    public void checkIsFriend() {
        User user = view.getUser();
        User friend = view.getFriend();

        model.isFriend(user.getUserId(), friend.getUserId(), new ResultCallback() {
            @Override
            public void onSuccess(String body) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(body);
                    view.setIsFriend(jsonObject.getBoolean("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {}
        });
    }

    /**------------------------------------------------------------------
     메서드 ==> 친구 추가
     ------------------------------------------------------------------*/
    @Override
    public void addFriend() {
        User user = view.getUser();
        User friend = view.getFriend();

        model.addFriend(user.getUserId(), friend.getUserId(), new ResultCallback() {
            @Override
            public void onSuccess(String body) {
                /** 친구 추가 성공했음을 알림(친구목록 갱신을 위해) */
                Events.noticeEventStr event = new Events.noticeEventStr("addFriend");
                BusProvider.getInstance().post(event);

                view.showAfterAddFriend();
            }

            @Override
            public void onFailure(String errorMsg) {}
        });
    }
}
