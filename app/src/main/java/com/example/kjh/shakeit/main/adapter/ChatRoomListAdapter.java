package com.example.kjh.shakeit.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.chat.ChatActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatRoomListAdapter extends RecyclerView.Adapter<ChatRoomListAdapter.ViewHolder> {

    private ArrayList<ChatRoom> rooms;
    private Context context;
    private User user;

    public ChatRoomListAdapter(Context context, ArrayList<ChatRoom> rooms, User user) {
        this.context = context;
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
        /** 채팅방 제목 셋팅 */
        String title = "";
        for(int i = 0; i < participants.size(); i++){
            if(i == (participants.size() - 1))
                title += participants.get(i).getName();
            else
                title += participants.get(i).getName() + ", ";
        }

        // TODO: 2019. 5. 8. 단체 채팅방일시 이미지 처리해줘야함
        String imageUrl = participants.get(0).getImageUrl();

        ChatHolder lastMessage = rooms.get(position).getChatHolder();

        String lastMessageContent = lastMessage.getMessageContent();
        String lastMessageTime = lastMessage.getSended_at();

        int memberCount = rooms.get(position).getMemberCount();
        int unreadCount = rooms.get(position).getUnreadCount();

        holder.inputName.setText(title);

        holder.lastMessageContentTxt.setText(lastMessageContent);

        holder.lastMessageTimeTxt.setText(lastMessageTime);

        if(memberCount > 2) {
            holder.memberCountTxt.setText("" + memberCount);
            holder.memberCountTxt.setVisibility(View.VISIBLE);
        } else {
            holder.memberCountTxt.setVisibility(View.GONE);
        }

        if(imageUrl == null || imageUrl.equals("")) {
            holder.profileImage.setImageResource(R.drawable.ic_basic_profile);
        } else {
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.profileImage);
        }

        if(unreadCount > 0) {
            holder.unreadCountTxt.setText(unreadCount);
            holder.unreadCountTxt.setVisibility(View.VISIBLE);
        } else {
            holder.unreadCountTxt.setVisibility(View.GONE);
        }

        holder.container.setOnClickListener(view -> {
            ChatRoom room = rooms.get(position);

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("room", room);
            intent.putExtra("user", user);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

}
