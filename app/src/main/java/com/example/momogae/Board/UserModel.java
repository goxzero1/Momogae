package com.example.momogae.Board;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserModel {

    public String userID;
    public String email;

    public UserModel() {}

    public UserModel(String userID, String email) {
        this.userID = userID;
        this.email = email;
    }

}