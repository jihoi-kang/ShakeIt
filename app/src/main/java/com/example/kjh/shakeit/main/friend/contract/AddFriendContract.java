package com.example.kjh.shakeit.main.friend.contract;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.User;

public interface AddFriendContract {

    interface View {

        String getInputEmail();
        void showMessageForIncorrectEmail();
        void showMessageForNoResult();
        void showMessageForFailure();
        User getUser();
        void showFriendInfo(int _id, String name, String imageUrl, int isFriend);
        void hideSoftKeyboard();
        void showLoadingDialog();
        void hideLoadingDialog();
        int getFriendId();
        void showAfterFriend();
        void showMessageForFailureMySelf();

    }

    interface Presenter {

        void onClickSearch();
        void onClickAddFriend();

    }

    interface Model {

        void getUserByEmail(int _id, String email, ResultCallback callback);
        void addFriend(int id, int friendId, ResultCallback callback);

    }

}
