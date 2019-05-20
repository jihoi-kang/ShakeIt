package com.example.kjh.shakeit.main.chat.contract;

import com.example.kjh.shakeit.data.User;

import java.util.ArrayList;

public interface AddChatContract {

    interface View {

        ArrayList<User> getInvitedFriends();
        void setInviteButton(ArrayList<User> invitedFriends);

    }

    interface Presenter {

        void onItemClick(User user, boolean isChecked);

    }

    interface Model {

    }

}
