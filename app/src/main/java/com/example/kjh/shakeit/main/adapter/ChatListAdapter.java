package com.example.kjh.shakeit.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.utils.ShareUtil;

import java.util.ArrayList;

// TODO: 2019. 5. 8. 채팅 목록 아답터(기능 구현해야 함)
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private static final int OTHER_MSG = 1;
    private static final int MINE_MSG = 2;

    private ArrayList<ChatHolder> chats;
    private Context context;

    public ChatListAdapter(Context context, ArrayList<ChatHolder> chats) {
        this.context = context;
        this.chats = chats;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

//    public class MineViewHolder extends ViewHolder {
//
//        @BindView(R.id.tv_time) TextView tvTime;
//        @BindView(R.id.ll_time) LinearLayout llTime;
//        @BindView(R.id.iv_mine_face) ImageView ivMineFace;
//        @BindView(R.id.progressBar) ProgressBar progressBar;
//        @BindView(R.id.tv_message_content) TextView tvMessageContent;
//        @BindView(R.id.iv_mine_image) ImageView ivMineImage;
//
//        public MineViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }

//    public class OtherViewHolder extends ViewHolder {
//
//        @BindView(R.id.tv_time) TextView tvTime;
//        @BindView(R.id.ll_time) LinearLayout llTime;
//        @BindView(R.id.iv_mine_face) ImageView ivMineFace;
//        @BindView(R.id.tv_message_content) TextView tvMessageContent;
//        @BindView(R.id.iv_mine_image) ImageView ivMineImage;
//
//        public OtherViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }

    @Override
    public int getItemViewType(int position) {
        long user_id = chats.get(position).getUserId();
        boolean isMine = (user_id == ShareUtil.getPreferInt("userId"));
        return isMine ? MINE_MSG : OTHER_MSG;
    }

    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
//            case MINE_MSG:
//                return new MineViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_mine, parent, false));
//            case OTHER_MSG:
//                return new OtherViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_other, parent, false));
//            default:
//                return null;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ChatListAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }



}
