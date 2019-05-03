package com.example.kjh.shakeit.utils;

import com.example.kjh.shakeit.beforelogin.contract.EmailLoginContract;
import com.example.kjh.shakeit.beforelogin.contract.MainContract;
import com.example.kjh.shakeit.beforelogin.contract.SignUpContract;
import com.example.kjh.shakeit.beforelogin.model.EmailLoginModel;
import com.example.kjh.shakeit.beforelogin.model.MainModel;
import com.example.kjh.shakeit.beforelogin.model.SignUpModel;
import com.example.kjh.shakeit.friend.AddFriendModel;
import com.example.kjh.shakeit.main.contract.TabFriendListContract;
import com.example.kjh.shakeit.main.model.TabFriendListModel;
import com.example.kjh.shakeit.profile.UpdateProfileContract;
import com.example.kjh.shakeit.profile.UpdateProfileModel;

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

    public static com.example.kjh.shakeit.main.contract.MainContract.Model provideAfterLoginMainModel() {
        return new com.example.kjh.shakeit.main.model.MainModel();
    }

    public static TabFriendListContract.Model provideFriendListModel() {
        return new TabFriendListModel();
    }

    public static AddFriendModel provideAddFriendModel() {
        return new AddFriendModel();
    }
}
