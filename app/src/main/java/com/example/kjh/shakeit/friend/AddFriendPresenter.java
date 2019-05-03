package com.example.kjh.shakeit.friend;

import android.util.Log;

import com.example.kjh.shakeit.callback.ResultCallback;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.utils.Validator;

import org.json.JSONException;
import org.json.JSONObject;

public class AddFriendPresenter implements AddFriendContract.Presenter {

    private AddFriendContract.View view;
    private AddFriendContract.Model model;

    public AddFriendPresenter(AddFriendContract.View view, AddFriendContract.Model model) {
        this.view = view;
        this.model = model;
    }

    /**------------------------------------------------------------------
     메서드 ==> 검색했을 때 로직
     ------------------------------------------------------------------*/
    @Override
    public void onClickSearch() {
        Log.e("add", "onClickSearch");

        String email = view.getInputEmail();
        User user = view.getUser();

        if(!Validator.isValidEmail(email)){
            view.showMessageForIncorrectEmail();
            return;
        }

        view.hideSoftKeyboard();

        model.getUserByEmail(user.get_id(), email, new ResultCallback() {
            @Override
            public void onSuccess(String body) {

                if(body == null) {
                    view.showMessageForNoResult();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(body);

                        view.showFriendInfo(
                                jsonObject.getInt("_id"),
                                jsonObject.getString("name"),
                                jsonObject.getString("image_url"),
                                jsonObject.getInt("is_friend")
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(String errorMsg) {
                view.showMessageForFailure();
            }
        });
    }

    /**------------------------------------------------------------------
     메서드 ==> 친구 추가 눌렀을 때 로직
     ------------------------------------------------------------------*/
    @Override
    public void onClickAddFriend() {
        User user = view.getUser();

        view.showLoadingDialog();

        model.addFriend(user.get_id(), view.getFriendId(), new ResultCallback() {
            @Override
            public void onSuccess(String body) {
                view.hideLoadingDialog();
                view.showAfterFriend();

                Events.refreshEvent event = new Events.refreshEvent("addFriend");
                BusProvider.getInstance().post(event);
            }

            @Override
            public void onFailure(String errorMsg) {
                view.hideLoadingDialog();
                view.showMessageForFailure();
            }
        });

    }
}
