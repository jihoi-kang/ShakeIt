package com.example.kjh.shakeit.main.chat.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.ReadHolder;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.fcm.FcmGenerator;
import com.example.kjh.shakeit.main.chat.contract.ChatContract;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.utils.Serializer;
import com.example.kjh.shakeit.utils.StrUtil;
import com.example.kjh.shakeit.utils.TimeManager;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_CAMERA;
import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_GALLERY;
import static com.example.kjh.shakeit.main.chat.ChatActivity.chatActHandler;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.CONN_WEBRTC;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.DELIVERY;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.IMAGE;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.MESSAGE;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.UPDATE_UNREAD;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.WIRE;

public class ChatPresenter implements ChatContract.Presenter {

    private final String TAG = ChatPresenter.class.getSimpleName();

    private ChatContract.View view;
    private ChatContract.Model model;

    private ArrayList<ChatHolder> chats;

    public ChatPresenter(ChatContract.View view, ChatContract.Model model) {
        this.view = view;
        this.model = model;

        chats = new ArrayList<>();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    public void onCreate() {
        BusProvider.getInstance().register(this);

        User user = view.getUser();
        ChatRoom room = view.getChatRoom();

        /** 채팅 목록 */
        getChatList();

        /** 읽지않은 메시지 읽었다고 알림 */
        model.updateUnreadChat(user.getUserId(), room);
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
    }

    /**------------------------------------------------------------------
     메서드 ==> 텍스트 메시지 전송 로직
     ------------------------------------------------------------------*/
    @Override
    public void onClickSend() {
        String content = view.getInputContent();
        User user = view.getUser();
        ChatRoom room = view.getChatRoom();
        String time = TimeManager.nowTime();

        // 채팅 메시지가 비어 있으면 종료
        if(StrUtil.isBlank(content))
            return;

        /** 참가자들의 아이디들 => JSONArray */
        JSONArray unreadUsers = new JSONArray();
        for(int index = 0; index < room.getParticipants().size(); index++)
            unreadUsers.put(room.getParticipants().get(index).getUserId());

        room.setChatHolder(
                new ChatHolder(0, room.getRoomId(), user.getUserId(), "text", content, time, unreadUsers.toString(), true)
        );
        String body = Serializer.serialize(room);

        // Netty
        model.sendMessage(body);

        ChatRoom newRoom = new ChatRoom();
        try {
            newRoom = room.copy();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        // Fcm
        for(User targetUser : newRoom.getParticipants()) {
            ChatRoom finalNewRoom = newRoom;
            model.getUser(targetUser.getUserId(), new ResultCallback() {
                @Override
                public void onSuccess(String body) {
                    User otherUser = Serializer.deserialize(body, User.class);
                    // 서버의 Token 상태 확인
                    if (StrUtil.isBlank(otherUser.getDeviceToken()))
                        return;

                    // 참가자들 변경
                    for(int idx = 0; idx < finalNewRoom.getParticipants().size(); idx++) {
                        if(finalNewRoom.getParticipants().get(idx).getUserId() == targetUser.getUserId()) {
                            try {
                                JSONArray jsonArray = new JSONArray(finalNewRoom.getChatHolder().getUnreadUsers());
                                for(int index = 0; index < jsonArray.length(); index++){
                                    if(jsonArray.getInt(index) == targetUser.getUserId()) {
                                        jsonArray.remove(index);
                                        jsonArray.put(user.getUserId());
                                    }

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            finalNewRoom.getParticipants().set(idx, user);
                        }
                    }

                    // FCM 전송
                    FcmGenerator.postRequest(otherUser.getDeviceToken(), "알림", Serializer.serialize(finalNewRoom).replaceAll("\"", "\'"));
                }

                @Override
                public void onFailure(String errorMsg) {

                }
            });
        }

        view.clearInputContent();
    }

    /**------------------------------------------------------------------
     메서드 ==> 이미지 메시지 전송 로직
     ------------------------------------------------------------------*/
    @Override
    public void sendImage(String path) {
        User user = view.getUser();
        ChatRoom room = view.getChatRoom();
        String time = TimeManager.nowTime();

        /** 참가자들의 아이디들 => JSONArray */
        JSONArray unreadUsers = new JSONArray();
        for(int index = 0; index < room.getParticipants().size(); index++)
            unreadUsers.put(room.getParticipants().get(index).getUserId());

        /** 이미지 업로드 후 프로필 업데이트 */
        model.uploadImage(user.getUserId(), path, new ResultCallback() {
            @Override
            public void onSuccess(String body) {
                JSONObject jsonObject;
                String imageUrl = "";
                try {
                    jsonObject = new JSONObject(body);
                    imageUrl = jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                room.setChatHolder(
                        new ChatHolder(0, room.getRoomId(), user.getUserId(), "image", imageUrl, time, unreadUsers.toString(), true)
                );
                String nettyBody = Serializer.serialize(room);

                model.sendImage(nettyBody);
            }

            @Override
            public void onFailure(String errorMsg) {}
        });
    }

    /**------------------------------------------------------------------
     메서드 ==> 채팅 목록
     ------------------------------------------------------------------*/
    private void getChatList() {
        ChatRoom room = view.getChatRoom();
        chats.clear();

        model.getChatList(room.getRoomId(), new ResultCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONArray jsonArray = new JSONArray(body);
                    for(int index = 0; index < jsonArray.length(); index++) {
                        ChatHolder holder = Serializer.deserialize(jsonArray.getString(index), ChatHolder.class);
                        chats.add(holder);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                view.showChatList(chats);
            }
            @Override
            public void onFailure(String errorMsg) {}
        });
    }

    /**------------------------------------------------------------------
     메서드 ==> 텍스트 외 메시지를 보내려고 add 눌렀을 때
     ------------------------------------------------------------------*/
    @Override
    public void onClickAttach() {
        view.showSelectType();
    }

    /**------------------------------------------------------------------
     메서드 ==> 영상통화 연결
     ------------------------------------------------------------------*/
    @Override
    public void toCall(String roomID) {
        TedPermission.with(view.getContext())
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        String type = "receiver";
                        String finalRoomID = roomID;
                        if(StrUtil.isBlank(finalRoomID)) {
                            int random = new Random().nextInt(99999999);
                            finalRoomID = "shake" + random;
                            type = "sender";
                        }
                        view.connectToRoom(
                                finalRoomID,
                                false,
                                false,
                                false,
                                0,
                                type
                        );
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                    }
                })
                .setDeniedTitle(R.string.permission_denied_title)
                .setDeniedMessage(R.string.permission_denied_message)
                .setGotoSettingButtonText(R.string.tedpermission_setting)
                .setPermissions(Manifest.permission.INTERNET, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                .check();
    }

    /**------------------------------------------------------------------
     메서드 ==> 갤러리
     ------------------------------------------------------------------*/
    @Override
    public void onClickGallery() {
        /** 권한 확인 */
        TedPermission.with(view.getContext())
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        ((Activity)view.getContext()).startActivityForResult(intent, REQUEST_CODE_GALLERY);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {}
                })
                .setDeniedTitle(R.string.permission_denied_title)
                .setDeniedMessage(R.string.permission_denied_message)
                .setGotoSettingButtonText(R.string.tedpermission_setting)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    /**------------------------------------------------------------------
     메서드 ==> 카메라
     ------------------------------------------------------------------*/
    @Override
    public void onClickCamera() {
        /** 권한 확인 */
        TedPermission.with(view.getContext())
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(view.getContext(), "com.example.kjh.shakeit.fileprovider", view.getFile()));
                        ((Activity)view.getContext()).startActivityForResult(intent, REQUEST_CODE_CAMERA);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                    }
                })
                .setDeniedTitle(R.string.permission_denied_title)
                .setDeniedMessage(R.string.permission_denied_message)
                .setGotoSettingButtonText(R.string.tedpermission_setting)
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    /**------------------------------------------------------------------
     구독이벤트 ==> Netty에서 이벤트 왔을 때 ==> 메시지 받거나 콜백
     ------------------------------------------------------------------*/
    @Subscribe
    public void nettyEvent (Events.nettyEvent event) {
        MessageHolder holder = event.getMessageHolder();

        if(holder.getType() == MESSAGE || holder.getType() == IMAGE || holder.getType() == WIRE) {
            ChatRoom room = Serializer.deserialize(holder.getBody(), ChatRoom.class);

            /** 채팅방 처음 생성되고 채팅 메시지 보냈을 때 */
            if(view.getChatRoom().getRoomId() == 0)
                view.setChatRoomId(room.getRoomId());

            if(view.getChatRoom().getRoomId() != room.getRoomId())
                return;

            if(holder.getSign() == DELIVERY)
                model.updateUnreadChat(view.getUser().getUserId(), view.getChatRoom());

            chats.add(room.getChatHolder());
        } else if(holder.getType() == UPDATE_UNREAD) {
            ReadHolder readHolder = Serializer.deserialize(holder.getBody(), ReadHolder.class);

            /** 읽은 사용자들을 UnreadArray에서 제거 */
            for(int index = 0; index < readHolder.getChatIds().size(); index++) {
                for(int chatsIdx = 0; chatsIdx < chats.size(); chatsIdx++) {
                    if(chats.get(chatsIdx).getChatId() == readHolder.getChatIds().get(index)) {
                        ChatHolder chatHolder = chats.get(chatsIdx);

                        JSONArray unreadUsers = null;
                        try {
                            unreadUsers = new JSONArray(chatHolder.getUnreadUsers());
                            for(int unreadIdx = 0; unreadIdx < unreadUsers.length(); unreadIdx++) {
                                if(unreadUsers.getInt(unreadIdx) == readHolder.getUserId()) {
                                    unreadUsers.remove(unreadIdx);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        chatHolder.setUnreadUsers(unreadUsers.toString());
                        chats.set(chatsIdx, chatHolder);
                    }
                }
            }
        } else if(holder.getType() == CONN_WEBRTC) {
            String roomID = null;
            try {
                JSONObject jsonObject = new JSONObject(holder.getBody());
                roomID = jsonObject.getString("roomID");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            view.goCallWaitActivity(roomID);
            return;
        } else
            return;

        Message msg = chatActHandler.obtainMessage();
        msg.obj = chats;
        chatActHandler.sendMessage(msg);
    }

    /**------------------------------------------------------------------
     구독이벤트 ==> 프로필 변경시 발생
     ------------------------------------------------------------------*/
    @Subscribe
    public void getUpdateProfileInfo(Events.updateProfileEvent event) {
        view.setUser(event.getUser());
    }

}
