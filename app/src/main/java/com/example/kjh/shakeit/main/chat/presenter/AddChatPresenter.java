package com.example.kjh.shakeit.main.chat.presenter;

import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.chat.contract.AddChatContract;

import java.util.ArrayList;

public class AddChatPresenter implements AddChatContract.Presenter {

    private final String TAG = AddChatPresenter.class.getSimpleName();

    private AddChatContract.View view;
    private AddChatContract.Model model;

    public AddChatPresenter(AddChatContract.View view, AddChatContract.Model model) {
        this.view = view;
        this.model = model;
    }

    /**------------------------------------------------------------------
     메서드 ==> 아이템 클릭시 초대할 사람 Array 저장
     ------------------------------------------------------------------*/
    @Override
    public void onItemClick(User user, boolean isChecked) {
        ArrayList<User> invitedFriends = view.getInvitedFriends();

        /** 초대할 대상 Array 업데이트 */
        if(isChecked) {
            invitedFriends.add(user);
        } else {
            for (int index = 0; index < invitedFriends.size(); index++) {
                if(invitedFriends.get(index).getUserId() == user.getUserId())
                    invitedFriends.remove(index);
            }
        }

        view.setInviteButton(invitedFriends);
    }

}
