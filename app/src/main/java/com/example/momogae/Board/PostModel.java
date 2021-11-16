package com.example.momogae.Board;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class PostModel {

    public String key;
    public String uid;
    public String author;
    public String title;
    public String body;
    public String type;

    public PostModel() {}

    public PostModel(String uid, String author, String title, String body, String type) {
        this.uid = uid;
        this.author = uid;
        this.title = title;
        this.body = body;
        this.type = type;
    }

    public PostModel(String key, String uid, String author, String title, String body, String type) {
        this.key = key;
        this.uid = uid;
        this.author = uid;
        this.title = title;
        this.body = body;
        this.type = type;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", uid);
        result.put("title", title);
        result.put("body", body);
        result.put("type", type);

        return result;
    }
}