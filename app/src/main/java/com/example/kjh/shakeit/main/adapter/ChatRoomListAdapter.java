package com.example.kjh.shakeit.main.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.chat.ChatActivity;
import com.example.kjh.shakeit.utils.ImageCombiner;
import com.example.kjh.shakeit.utils.ImageLoaderUtil;
import com.example.kjh.shakeit.utils.StrUtil;
import com.example.kjh.shakeit.utils.TimeManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_MAIN_TO_CHAT;

/**
 * 채팅방 아답터
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 10. PM 2:29
 **/
public class ChatRoomListAdapter extends RecyclerView.Adapter<ChatRoomListAdapter.ViewHolder> {

    private final String TAG = ChatRoomListAdapter.class.getSimpleName();

    private static ArrayList<ChatRoom> rooms = new ArrayList<>();
    private User user;
    private Activity activity;

    long tempTime;

    public ChatRoomListAdapter(Activity activity, ArrayList<ChatRoom> rooms, User user) {
        this.activity = activity;
        this.rooms = rooms;
        this.user = user;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profile_image) ImageView profileImage;
        @BindView(R.id.name) TextView inputName;
        @BindView(R.id.last_message_content) TextView lastMessageContentTxt;
        @BindView(R.id.last_message_time) TextView lastMessageTimeTxt;
        @BindView(R.id.unread_count) TextView unreadCountTxt;
        @BindView(R.id.member_count) TextView memberCountTxt;
        @BindView(R.id.container) LinearLayout container;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ChatRoomListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chatroom_list, parent, false);

        ChatRoomListAdapter.ViewHolder holder = new ChatRoomListAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ChatRoomListAdapter.ViewHolder holder, int position) {
        ArrayList<User> participants = rooms.get(position).getParticipants();

        /** 채팅방 사진 셋팅 */
        Thread thread = new Thread(() -> {
            Bitmap resultImage;
            if(participants.size() == 1) {
                resultImage = makeBitmap(participants.get(0).getImageUrl());
            } else if(participants.size() == 2) {
                resultImage = ImageCombiner.combine(
                        makeBitmap(participants.get(0).getImageUrl()),
                        makeBitmap(participants.get(1).getImageUrl())
                );
            } else if(participants.size() == 3) {
                resultImage = ImageCombiner.combine(
                        makeBitmap(participants.get(0).getImageUrl()),
                        makeBitmap(participants.get(1).getImageUrl()),
                        makeBitmap(participants.get(2).getImageUrl())
                );
            } else {
                resultImage = ImageCombiner.combine(
                        makeBitmap(participants.get(0).getImageUrl()),
                        makeBitmap(participants.get(1).getImageUrl()),
                        makeBitmap(participants.get(2).getImageUrl()),
                        makeBitmap(participants.get(3).getImageUrl())
                );
            }

            Bitmap finalResultImage = resultImage;
            activity.runOnUiThread(() -> holder.profileImage.setImageBitmap(finalResultImage));
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /** 채팅방 제목 셋팅 */
        String title = "";
        for(int index = 0; index < participants.size(); index++)
            title += (index == (participants.size() - 1)) ? participants.get(index).getName() : participants.get(index).getName() + ", ";

        if(title.length() > 13) {
            title = title.substring(0, 13);
            title += "...";
        }

        ChatHolder lastMessage = rooms.get(position).getChatHolder();

        String lastMessageType = lastMessage.getMessageType();
        String lastMessageContent = lastMessage.getMessageContent();
        String lastMessageTime = lastMessage.getSended_at();

        if(lastMessageType.equals("image"))
            lastMessageContent = "사진";

        int memberCount = rooms.get(position).getMemberCount();
        int unreadCount = rooms.get(position).getUnreadCount();

        holder.inputName.setText(title);

        holder.lastMessageContentTxt.setText(lastMessageContent);

        if(TimeManager.nowdate().equals(lastMessageTime.substring(0, 10))){
            holder.lastMessageTimeTxt.setText(lastMessageTime.substring(11, 16));
        } else
            holder.lastMessageTimeTxt.setText(lastMessageTime.substring(5, 10));

        if(memberCount > 2) {
            holder.memberCountTxt.setText("" + memberCount);
            holder.memberCountTxt.setVisibility(View.VISIBLE);
        } else
            holder.memberCountTxt.setVisibility(View.GONE);

        if(unreadCount > 0) {
            holder.unreadCountTxt.setText("" + unreadCount);
            holder.unreadCountTxt.setVisibility(View.VISIBLE);
        } else
            holder.unreadCountTxt.setVisibility(View.GONE);

        holder.container.setOnClickListener(view -> {
            long currentTime = System.currentTimeMillis();
            if(tempTime + 1000 > currentTime)
                return;

            tempTime = currentTime;

            ChatRoom room = rooms.get(position);

            Intent intent = new Intent(activity, ChatActivity.class);
            intent.putExtra("room", room);
            intent.putExtra("user", user);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ((BitmapDrawable)holder.profileImage.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] imageByteArray = stream.toByteArray();
            intent.putExtra("imageArray", imageByteArray);

            /** 채팅방목록 화면에서 채팅방 화면으로 넘어 갈때 단체 채팅시 참여자 중 한명과 1:1 채팅을 할 수 있음 */
            if(memberCount > 2)
                activity.startActivityForResult(intent, REQUEST_CODE_MAIN_TO_CHAT);
            else
                activity.startActivity(intent);


        });
    }

    /**------------------------------------------------------------------
     메서드 ==> 프로필이미지 Bitmap 반환
     ------------------------------------------------------------------*/
    private Bitmap makeBitmap(String url) {
        Bitmap bitmap;
        if(StrUtil.isBlank(url))
            bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_basic_profile);
        else
            bitmap = ImageLoaderUtil.getBitmap(url);

        return bitmap;
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public static ArrayList<ChatRoom> getChatRooms() {
        return rooms;
    }

    public void changeList(ArrayList<ChatRoom> rooms) {
        this.rooms = rooms;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
