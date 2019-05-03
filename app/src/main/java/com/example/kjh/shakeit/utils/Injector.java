package com.example.kjh.shakeit.utils;

import com.example.kjh.shakeit.login.contract.EmailLoginContract;
import com.example.kjh.shakeit.login.contract.MainContract;
import com.example.kjh.shakeit.login.contract.SignUpContract;
import com.example.kjh.shakeit.login.model.EmailLoginModel;
import com.example.kjh.shakeit.login.model.MainModel;
import com.example.kjh.shakeit.login.model.SignUpModel;
import com.example.kjh.shakeit.main.friend.model.AddFriendModel;
import com.example.kjh.shakeit.main.friend.contract.TabFriendListContract;
import com.example.kjh.shakeit.main.friend.model.TabFriendListModel;
import com.example.kjh.shakeit.main.more.contract.UpdateProfileContract;
import com.example.kjh.shakeit.main.more.model.UpdateProfileModel;

/**
 * 모델을 주입 할 때 사용
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

    public static AddFriendModel provideAddFriendModel() {
        return new AddFriendModel();
    }
}
