package com.example.e_advisor.resquest_objects;

public class UserRegData {
    String name;
    String email;
    String password;
    String academicLevel;
    String [] interests;

    public UserRegData(String name, String email, String password, String academicLevel, String[] interests) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.academicLevel = academicLevel;
        this.interests = interests;
    }
}
