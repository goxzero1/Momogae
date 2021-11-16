package com.example.momogae.Board;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserModel {

    public String userID;
    public String email;
    public String name;
    public String Password;
    public String Phone;
    public String RegistrationDate;
    public String token;
    public String userMsg;

    public UserModel() {}

    public UserModel(String userID, String email) {
        this.userID = userID;
        this.email = email;
        this.name= name;
        this.Password = Password;
        this.Phone = Phone;
        this.RegistrationDate = RegistrationDate;
        this.token = token;
        this.userMsg = userMsg;
    }

}