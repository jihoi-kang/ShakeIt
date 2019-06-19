package com.example.kjh.shakeit.cash.presenter;

import android.app.Activity;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.cash.contract.WireCashContract;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.utils.Serializer;
import com.example.kjh.shakeit.utils.ShareUtil;
import com.example.kjh.shakeit.utils.TimeManager;

import org.json.JSONArray;

import java.util.ArrayList;

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
        ChatRoom room = view.getChatRoom();
        String time = TimeManager.nowTime();

        /** 사용자가 가진 포인트보다 많이 송금할 수 없음 */
        if(view.getAmount() > view.getUser().getCash()) {
            view.showMessageForLackOfPoint();
            return;
        }

        /** Netty */
        // 참가자들
        JSONArray unreadUsers = new JSONArray();
        unreadUsers.put(otherUser.getUserId());
        room.setChatHolder(
                new ChatHolder(0, room.getRoomId(), user.getUserId(), "point", String.valueOf(amount), time, unreadUsers.toString(), true)
        );
        room.setUnreadCount(1);
        String body = Serializer.serialize(room);

        model.sendMessage(body);

        /** 유저 변경사항 저장(포인트) */
        user.setCash(user.getCash() - amount);
        ShareUtil.setPreferInt("cash", user.getCash() - amount);

        Events.updateProfileEvent event = new Events.updateProfileEvent(user);
        BusProvider.getInstance().post(event);

        view.showMessageForSuccess();

        ((Activity)view.getContext()).setResult(RESULT_OK);
        ((Activity)view.getContext()).finish();
    }

    /**------------------------------------------------------------------
     메서드 ==> 해당하는 채팅방을 가져오는 로직
     ------------------------------------------------------------------*/
    @Override
    public void getChatRoom() {
        User user = view.getUser();
        User otherUser = view.getOtherUser();

        model.getChatRoom(user.getUserId(), otherUser.getUserId(), new ResultCallback() {
            @Override
            public void onSuccess(String body) {
                if(body.equals("null")) {
                    ChatRoom room = new ChatRoom();
                    room.setRoomId(0);
                    room.setMemberCount(2);
                    ArrayList<User> participants = new ArrayList<>();
                    participants.add(otherUser);
                    room.setParticipants(participants);
                    view.setChatRoom(room);
                } else
                    view.setChatRoom(Serializer.deserialize(body, ChatRoom.class));
            }

            @Override
            public void onFailure(String errorMsg) {}
        });
    }

}
