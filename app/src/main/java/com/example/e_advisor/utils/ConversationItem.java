package com.example.e_advisor.utils;

public class ConversationItem {
    private String userId;
    private String name;
    private String lastMessage;
    private int unreadCount;

    public ConversationItem(String userId, String name, String lastMessage, int unreadCount) {
        this.userId = userId;
        this.name = name;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }
}

