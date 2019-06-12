package com.example.kjh.shakeit.cash.presenter;

import android.app.Activity;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.cash.contract.WireCashContract;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.data.WireHolder;
import com.example.kjh.shakeit.fcm.FcmGenerator;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.utils.Serializer;
import com.example.kjh.shakeit.utils.StrUtil;

import static android.app.Activity.RESULT_OK;

public class WireCashPresenter implements WireCashContract.Presenter {

    private WireCashContract.View view;
    private WireCashContract.Model model;

    public WireCashPresenter(WireCashContract.View view, WireCashContract.Model model) {
        this.view = view;
        this.model = model;
    }

    /**------------------------------------------------------------------
     메서드 ==> 송금 로직
     ------------------------------------------------------------------*/
    @Override
    public void onClickWire() {
        User user = view.getUser();
        User otherUser = view.getOtherUser();
        int amount = view.getAmount();

        /** 사용자가 가진 포인트보다 많이 송금할 수 없음 */
        if(view.getAmount() > view.getUser().getCash()) {
            view.showMessageForLackOfPoint();
            return;
        }

        // 송금
        model.wire(user.getUserId(), otherUser.getUserId(), amount, new ResultCallback() {
            @Override
            public void onSuccess(String body) {
                user.setCash(user.getCash() - amount);
                Events.updateProfileEvent event = new Events.updateProfileEvent(user);
                BusProvider.getInstance().post(event);

                // Token 정보 알기 위해 사용자 현재 정보 얻어오기
                model.getUser(otherUser.getUserId(), new ResultCallback() {
                    @Override
                    public void onSuccess(String body) {
                        User otherUser = Serializer.deserialize(body, User.class);
                        // 서버의 Token 상태 확인
                        if(StrUtil.isBlank(otherUser.getDeviceToken()))
                            return;

                        // Netty 전송
                        String nettyBody = Serializer.serialize(new WireHolder(user.getUserId(), otherUser.getUserId(), amount));
                        model.sendMessage(nettyBody);

                        // FCM 전송
                        FcmGenerator.postRequest(otherUser.getDeviceToken(),"알림", user.getName() + "님이 포인트를 보냈어요!");
                    }

                    @Override
                    public void onFailure(String errorMsg) {}
                });

                view.showMessageForSucces();

                ((Activity)view.getContext()).setResult(RESULT_OK);
                ((Activity)view.getContext()).finish();
            }

            @Override
            public void onFailure(String errorMsg) {}
        });

    }
}
