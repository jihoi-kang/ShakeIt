package com.example.kjh.shakeit.utils;

import com.example.kjh.shakeit.cash.contract.ChargeContract;
import com.example.kjh.shakeit.cash.contract.WireCashContract;
import com.example.kjh.shakeit.cash.model.ChargeModel;
import com.example.kjh.shakeit.cash.model.WireCashModel;
import com.example.kjh.shakeit.login.contract.EmailLoginContract;
import com.example.kjh.shakeit.login.contract.MainContract;
import com.example.kjh.shakeit.login.contract.SignUpContract;
import com.example.kjh.shakeit.login.model.EmailLoginModel;
import com.example.kjh.shakeit.login.model.MainModel;
import com.example.kjh.shakeit.login.model.SignUpModel;
import com.example.kjh.shakeit.main.chat.contract.AddChatContract;
import com.example.kjh.shakeit.main.chat.contract.ChatContract;
import com.example.kjh.shakeit.main.chat.contract.TabChatRoomListContract;
import com.example.kjh.shakeit.main.chat.model.AddChatModel;
import com.example.kjh.shakeit.main.chat.model.ChatModel;
import com.example.kjh.shakeit.main.chat.model.TabChatRoomListModel;
import com.example.kjh.shakeit.main.friend.contract.AddFriendContract;
import com.example.kjh.shakeit.main.friend.contract.ProfileDetailContract;
import com.example.kjh.shakeit.main.friend.contract.ShakeContract;
import com.example.kjh.shakeit.main.friend.contract.TabFriendListContract;
import com.example.kjh.shakeit.main.friend.model.AddFriendModel;
import com.example.kjh.shakeit.main.friend.model.ProfileDetailModel;
import com.example.kjh.shakeit.main.friend.model.ShakeModel;
import com.example.kjh.shakeit.main.friend.model.TabFriendListModel;
import com.example.kjh.shakeit.main.more.contract.UpdateProfileContract;
import com.example.kjh.shakeit.main.more.model.UpdateProfileModel;

/**
 * 모델(M)을 주입 할 때 사용
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 26. PM 5:54
 **/
public class Injector {

    public static EmailLoginContract.Model provideEmailLoginModel() {
        return new EmailLoginModel();
    }
    public static MainContract.Model provideBeforeLoginMainModel() {
        return new MainModel();
    }

    public static SignUpContract.Model provideSignUpModel() {
        return new SignUpModel();
    }

    public static UpdateProfileContract.Model provideUpdateProfileModel() {
        return new UpdateProfileModel();
    }

    public static com.example.kjh.shakeit.main.MainContract.Model provideAfterLoginMainModel() {
        return new com.example.kjh.shakeit.main.MainModel();
    }

    public static TabFriendListContract.Model provideFriendListModel() {
        return new TabFriendListModel();
    }

    public static AddFriendContract.Model provideAddFriendModel() {
        return new AddFriendModel();
    }

    public static TabChatRoomListContract.Model provideChatRoomListModel() {
        return new TabChatRoomListModel();
    }

    public static ChatContract.Model provideChatModel() {
        return new ChatModel();
    }

    public static AddChatContract.Model provideAddChatModel() {
        return new AddChatModel();
    }

    public static ShakeContract.Model provideShakeModel() {
        return new ShakeModel();
    }

    public static ProfileDetailContract.Model provideProfileDetailModel() {
        return new ProfileDetailModel();
    }

    public static ChargeContract.Model provideChargeModel() {
        return new ChargeModel();
    }

    public static WireCashContract.Model provideWireCashModel() {
        return new WireCashModel();
    }
}
