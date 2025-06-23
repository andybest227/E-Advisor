package com.example.e_advisor.resquest_objects;

public class CreateTipsRequest {
    String title, content;
    String [] tags;

    public CreateTipsRequest(String title, String content, String[] tags) {
        this.title = title;
        this.content = content;
        this.tags = tags;
    }
}
