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
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.friend.ProfileDetailActivity;

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

    public FriendListAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profile_image) ImageView profileImage;
        @BindView(R.id.name) TextView inputName;
        @BindView(R.id.status_message) TextView inputStatusMessage;
        @BindView(R.id.container) LinearLayout container;

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
        String imageUrl = users.get(position).getImage_url();
        String name = users.get(position).getName();
        String statusMessage = users.get(position).getStatus_message();

        holder.inputName.setText(name);

        if(position == 0) {
            holder.inputName.setText(name + " (me)");
        }

        holder.inputStatusMessage.setText(statusMessage);

        if(imageUrl == null || imageUrl.equals("")) {
            holder.profileImage.setImageResource(R.drawable.ic_basic_profile);
        } else {
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.profileImage);
        }

        holder.container.setOnClickListener(view -> {
            User user = users.get(position);

            Intent intent = new Intent(context, ProfileDetailActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("position", position);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
