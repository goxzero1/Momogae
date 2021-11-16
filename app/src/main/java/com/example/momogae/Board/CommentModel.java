package com.example.momogae.Board;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class CommentModel {

    public String uid;
    public String author;
    public String text;

    public CommentModel() {}

    public CommentModel(String uid, String author, String text) {
        this.uid = uid;
        this.author = uid;
        this.text = text;
    }

}
