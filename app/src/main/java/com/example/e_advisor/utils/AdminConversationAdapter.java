package com.example.e_advisor.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_advisor.R;

import java.util.List;

public class AdminConversationAdapter extends RecyclerView.Adapter<AdminConversationAdapter.ViewHolder> {

    public interface OnUserClickListener {
        void onUserClick(String userId);
    }

    private final Context context;
    private final List<ConversationItem> conversationList;
    private final OnUserClickListener listener;

    public AdminConversationAdapter(Context context, List<ConversationItem> conversationList, OnUserClickListener listener) {
        this.context = context;
        this.conversationList = conversationList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, lastMessage, unreadBadge;
        View container;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            lastMessage = itemView.findViewById(R.id.last_message);
            unreadBadge = itemView.findViewById(R.id.unread_badge);
            container = itemView;
        }
    }

    @NonNull
    @Override
    public AdminConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminConversationAdapter.ViewHolder holder, int position) {
        ConversationItem item = conversationList.get(position);

        holder.userName.setText(item.getName());
        holder.lastMessage.setText(item.getLastMessage());

        if (item.getUnreadCount() > 0) {
            holder.unreadBadge.setVisibility(View.VISIBLE);
            holder.unreadBadge.setText(String.valueOf(item.getUnreadCount()));
        } else {
            holder.unreadBadge.setVisibility(View.GONE);
        }

        holder.container.setOnClickListener(v -> listener.onUserClick(item.getUserId()));
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }
}

