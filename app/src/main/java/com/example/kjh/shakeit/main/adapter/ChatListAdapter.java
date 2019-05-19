package com.example.kjh.shakeit.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.utils.ShareUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 채팅 아답터
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 10. PM 2:29
 **/
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private final String TAG = ChatListAdapter.class.getSimpleName();

    private static final int OTHER_MSG = 1;
    private static final int MINE_MSG = 2;

    private ArrayList<ChatHolder> chats;
    private Context context;
    private ChatRoom room;

    public ChatListAdapter(Context context, ArrayList<ChatHolder> chats, ChatRoom room) {
        this.context = context;
        this.chats = chats;
        this.room = room;
    }

    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case MINE_MSG:
                return new MineViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_mine, parent, false));
            case OTHER_MSG:
                return new OtherViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_other, parent, false));
            default:
                return null;
        }
    }

    /**------------------------------------------------------------------
     메서드 ==> 아이템 유형 식별
     ------------------------------------------------------------------*/
    @Override
    public int getItemViewType(int position) {
        int user_id = chats.get(position).getUserId();
        boolean isMine = (user_id == ShareUtil.getPreferInt("userId"));
        return isMine ? MINE_MSG : OTHER_MSG;
    }

    @Override
    public void onBindViewHolder(ChatListAdapter.ViewHolder holder, int position) {
        if (chats == null || chats.get(position) == null) return;

        boolean haveToShowTime = false;

        if (position == 0) {
            haveToShowTime = true;
        } else {
            if (!chats.get(position).getSended_at().substring(0, 10)
                    .equals(chats.get(position - 1).getSended_at().substring(0, 10))) {
                haveToShowTime = true;
            }
        }

        int viewType = getItemViewType(position);

        ChatHolder chatHolder = chats.get(position);
        ChatHolder preChatHolder = null;
        if (position > 0)
            preChatHolder = chats.get(position - 1);

        switch (viewType) {
            case MINE_MSG: setMineValue((MineViewHolder) holder, chatHolder, haveToShowTime); break;
            case OTHER_MSG: setOtherValue((OtherViewHolder) holder, chatHolder, preChatHolder, haveToShowTime); break;
        }

    }

    private void setMineValue(MineViewHolder holder, ChatHolder chatHolder, boolean haveToShowTime) {
        if(haveToShowTime){
            holder.timeLayout.setVisibility(View.VISIBLE);
            holder.time.setText(chatHolder.getSended_at().substring(0, 10));
        } else {
            holder.timeLayout.setVisibility(View.GONE);
        }
        holder.sendedTimeTxt.setText(chatHolder.getSended_at().substring(11, 16));
        holder.messageContent.setText(chatHolder.getMessageContent());

        try {
            JSONArray jsonArray = new JSONArray(chatHolder.getUnreadUsers());
            if(jsonArray.length() == 0) {
                holder.unreadCountTxt.setVisibility(View.GONE);
            } else {
                holder.unreadCountTxt.setVisibility(View.VISIBLE);
                holder.unreadCountTxt.setText("" + jsonArray.length());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setOtherValue(OtherViewHolder holder, ChatHolder chatHolder, ChatHolder preChatHolder, boolean haveToShowTime) {
        if(haveToShowTime){
            holder.timeLayout.setVisibility(View.VISIBLE);
            holder.time.setText(chatHolder.getSended_at().substring(0, 10));
        } else {
            holder.timeLayout.setVisibility(View.GONE);
        }

        /** 단체채팅일 때에만 이름 표시 */
        if(room.getParticipants().size() > 1) {
            holder.name.setVisibility(View.VISIBLE);
            for (User user : room.getParticipants()) {
                if (user.getUserId() == chatHolder.getUserId()){
                    if(preChatHolder != null && preChatHolder.getUserId() == chatHolder.getUserId())
                        holder.name.setVisibility(View.GONE);
                    else
                        holder.name.setText("" + user.getName());
                }

            }
        } else {
            holder.name.setVisibility(View.GONE);
        }

        holder.sendedTimeTxt.setText(chatHolder.getSended_at().substring(11, 16));
        holder.messageContent.setText(chatHolder.getMessageContent());

        try {
            JSONArray jsonArray = new JSONArray(chatHolder.getUnreadUsers());
            if(jsonArray.length() == 0) {
                holder.unreadCountTxt.setVisibility(View.GONE);
            } else {
                holder.unreadCountTxt.setVisibility(View.VISIBLE);
                holder.unreadCountTxt.setText("" + jsonArray.length());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void changeList(ArrayList<ChatHolder> chats) {
        this.chats = chats;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class MineViewHolder extends ViewHolder {

        @BindView(R.id.ll_time) LinearLayout timeLayout;
        @BindView(R.id.time) TextView time;
        @BindView(R.id.message_content) TextView messageContent;
        @BindView(R.id.sendedTime) TextView sendedTimeTxt;
        @BindView(R.id.unreadCount) TextView unreadCountTxt;

        public MineViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public class OtherViewHolder extends ViewHolder {

        @BindView(R.id.ll_time) LinearLayout timeLayout;
        @BindView(R.id.time) TextView time;
        @BindView(R.id.message_content) TextView messageContent;
        @BindView(R.id.sendedTime) TextView sendedTimeTxt;
        @BindView(R.id.unreadCount) TextView unreadCountTxt;
        @BindView(R.id.name) TextView name;
        @BindView(R.id.ll_read) LinearLayout readLayout;

        public OtherViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

}
