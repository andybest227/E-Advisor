package com.example.e_advisor.resquest_objects;

public class CreateCourseRequestObject {
    private String title, description;
    private String[] tags;

    public CreateCourseRequestObject(String title, String description, String[] tags) {
        this.title = title;
        this.description = description;
        this.tags = tags;
    }
}
