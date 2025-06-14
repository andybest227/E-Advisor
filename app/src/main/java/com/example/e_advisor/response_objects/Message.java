package com.example.e_advisor.response_objects;

public class Message {
    private String _id;
    private String senderId;
    private String receiverId;
    private String content;
    private String createdAt;
    private String updatedAt;

    // Constructor
    public Message(String _id, String senderId, String receiverId, String content, String createdAt, String updatedAt) {
        this._id = _id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Message(String senderId, String content, String createdAt) {
        this.senderId = senderId;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Empty constructor for Gson or Firebase
    public Message() {}

    // Getters and Setters
    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
