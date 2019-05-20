package com.example.kjh.shakeit.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.etc.OnItemClickListener;
import com.example.kjh.shakeit.main.chat.AddChatActivity;
import com.example.kjh.shakeit.main.friend.ProfileDetailActivity;
import com.example.kjh.shakeit.main.friend.TabFriendListFragment;
import com.example.kjh.shakeit.utils.ImageLoaderUtil;
import com.example.kjh.shakeit.utils.StrUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 친구 목록 탭에서 친구 목록 아답터
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 2. PM 9:21
 **/
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    private ArrayList<User> users;
    private Context context;
    private String fromType;
    private OnItemClickListener listener;

    public FriendListAdapter(Context context, ArrayList<User> users, String fromType, OnItemClickListener listener) {
        this.context = context;
        this.users = users;
        this.fromType = fromType;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profile_image) ImageView profileImage;
        @BindView(R.id.name) TextView inputName;
        @BindView(R.id.status_message) TextView inputStatusMessage;
        @BindView(R.id.container) LinearLayout container;
        @BindView(R.id.select) CheckBox selectBox;

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
        if(fromType.equals(TabFriendListFragment.class.getSimpleName())) {
            /** 자기 자신 */
            if(position == 0)
                holder.inputName.setText(name + " (me)");

            holder.inputStatusMessage.setText(statusMessage);

            holder.container.setOnClickListener(view -> {
                Intent intent = new Intent(context, ProfileDetailActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

            holder.selectBox.setVisibility(View.GONE);
        }
        /** AddChatActivity에서 사용할 경우 ==> 채팅방 만들때 대상 선택 목록 */
        else if(fromType.equals(AddChatActivity.class.getSimpleName())) {
            holder.inputStatusMessage.setVisibility(View.GONE);

            holder.container.setOnClickListener(view -> {
                if(holder.selectBox.isChecked())
                    holder.selectBox.setChecked(false);
                else
                    holder.selectBox.setChecked(true);

                listener.onItemClick(user, holder.selectBox.isChecked());
            });

        }


    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
