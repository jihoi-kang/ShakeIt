package com.example.kjh.shakeit.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.cash.ChooseFriendActivity;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.chat.AddChatActivity;
import com.example.kjh.shakeit.main.chat.ChatActivity;
import com.example.kjh.shakeit.main.friend.ProfileDetailActivity;
import com.example.kjh.shakeit.main.friend.TabFriendListFragment;
import com.example.kjh.shakeit.utils.ImageLoaderUtil;
import com.example.kjh.shakeit.utils.StrUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_CHAT_TO_PROFILE_DETAIL;
import static com.example.kjh.shakeit.app.Constant.REQUEST_CODE_FRIEND_LIST_TO_PROFILE_DETAIL;

/**
 * 친구 목록 탭에서 친구 목록 아답터
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 2. PM 9:21
 *
 * TabFriendListFragment.class,
 * AddChatActivity.class,
 * ChatActivity.class,
 * ChooseFriendActivity
 * 위 네개의 클래스에서 사용
 **/
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    private final String TAG = FriendListAdapter.class.getSimpleName();

    private ArrayList<User> users;
    private Context context;
    private String from;
    private OnItemClickListener listener;

    private Drawable uncheckedRadioDrawable, checkedRadioDrawable;

    private int targetPosition = -1;

    public FriendListAdapter(Context context, ArrayList<User> users, String from, OnItemClickListener listener) {
        this.context = context;
        this.users = users;
        this.from = from;
        this.listener = listener;

        if(from.equals(AddChatActivity.class.getSimpleName())
                || from.equals(ChooseFriendActivity.class.getSimpleName())) {
            uncheckedRadioDrawable = context.getResources().getDrawable(
                    R.drawable.ic_outline_radio_button_unchecked_black_48dp);

            checkedRadioDrawable = context.getResources().getDrawable(
                    R.drawable.ic_radio_button_checked_black_48dp);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profile_image) ImageView profileImage;
        @BindView(R.id.name) TextView inputName;
        @BindView(R.id.status_message) TextView inputStatusMessage;
        @BindView(R.id.container) LinearLayout container;
        @BindView(R.id.select) ImageView selectBox;
        @BindView(R.id.line) View line;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_list, parent, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);

        String imageUrl = users.get(position).getImageUrl();
        String name = users.get(position).getName();
        String statusMessage = users.get(position).getStatusMessage();

        holder.inputName.setText(name);

        if(StrUtil.isBlank(imageUrl))
            holder.profileImage.setImageResource(R.drawable.ic_basic_profile);
        else
            ImageLoaderUtil.display(context, holder.profileImage, imageUrl);

        /** TabFriendListFragment에서 사용할 경우 ==> 친구목록 */
        if(from.equals(TabFriendListFragment.class.getSimpleName())) {
            /** 자기 자신 */
            if(position == 0) {
                holder.profileImage.getLayoutParams().width += 40;
                holder.profileImage.getLayoutParams().height += 40;

                holder.inputName.setText(name + " (me)");
                holder.line.setVisibility(View.VISIBLE);
            }

            holder.inputStatusMessage.setText(statusMessage);

            holder.container.setOnClickListener(view -> {
                Intent intent = new Intent(context, ProfileDetailActivity.class);
                intent.putExtra("user", users.get(0));
                intent.putExtra("friend", user);
                intent.putExtra("position", position);
                intent.putExtra("from", TabFriendListFragment.class.getSimpleName());
                ((Activity)context).startActivityForResult(intent, REQUEST_CODE_FRIEND_LIST_TO_PROFILE_DETAIL);
            });

            holder.selectBox.setVisibility(View.GONE);
        }
        /** AddChatActivity에서 사용할 경우 ==> 채팅방 만들때 대상 선택 목록 */
        else if(from.equals(AddChatActivity.class.getSimpleName())) {
            holder.inputStatusMessage.setVisibility(View.GONE);

            holder.container.setOnClickListener(view -> {
                boolean flag;
                if(holder.selectBox.getDrawable() == checkedRadioDrawable){
                    holder.selectBox.setImageDrawable(uncheckedRadioDrawable);
                    flag = false;
                } else {
                    holder.selectBox.setImageDrawable(checkedRadioDrawable);
                    flag = true;
                }

                /** 대상 선택 및 해제 */
                listener.onItemClick(user, flag);
            });
        }
        /** ChatActivity에서 사용할 경우 ==> 참여자목록 */
        else if(from.equals(ChatActivity.class.getSimpleName())) {
            holder.selectBox.setVisibility(View.GONE);
            holder.inputStatusMessage.setVisibility(View.GONE);
            holder.inputName.setTextSize(12);

            if(position == 0)
                holder.inputName.setText(name + " (me)");

            holder.container.setOnClickListener(view -> {
                Intent intent = new Intent(context, ProfileDetailActivity.class);
                intent.putExtra("user", users.get(0));
                intent.putExtra("friend", user);
                intent.putExtra("position", position);
                intent.putExtra("from", ChatActivity.class.getSimpleName());
                intent.putExtra("size", getItemCount());
                ((Activity)context).startActivityForResult(intent, REQUEST_CODE_CHAT_TO_PROFILE_DETAIL);
            });
        }
        /** ChooseFriendActivity에서 사용할 경우 */
        else if(from.equals(ChooseFriendActivity.class.getSimpleName())) {
            holder.inputStatusMessage.setVisibility(View.GONE);

            if(user.isFlag())
                holder.selectBox.setImageDrawable(checkedRadioDrawable);
            else
                holder.selectBox.setImageDrawable(uncheckedRadioDrawable);

            holder.container.setOnClickListener(view -> {
                // 모든 친구목록 중 오직 하나의 친구만 선택할 수 있음
                if(user.isFlag()){
                    user.setFlag(false);
                } else {
                    for(int idx = 0; idx < getItemCount(); idx++) {
                        User target = users.get(idx);
                        if(target.isFlag()) {
                            target.setFlag(false);
                            users.set(idx, target);
                        }
                    }
                    user.setFlag(true);
                }

                users.set(position, user);
                notifyDataSetChanged();

                /** 대상 선택 및 해제 */
                listener.onItemClick(user, user.isFlag());
            });
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
