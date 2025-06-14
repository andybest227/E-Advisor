package com.example.e_advisor.response_objects;

import java.util.List;

public class MessageObject {
    private boolean success;
    List<Message> messages;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
