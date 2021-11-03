package com.example.momogae.MainActivity.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String userID;
    public String email;

    public User() {}

    public User(String userID, String email) {
        this.userID = userID;
        this.email = email;
    }

}