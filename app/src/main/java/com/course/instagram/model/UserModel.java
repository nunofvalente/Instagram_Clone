package com.course.instagram.model;

import com.google.firebase.database.Exclude;

public class UserModel {

    private String id;
    private String name;
    private String email;
    private String password;

    public UserModel() {
    }


    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(String id) {
        this.id = id;
    }
}
