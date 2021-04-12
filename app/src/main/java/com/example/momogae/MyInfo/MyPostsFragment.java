package com.example.momogae.MyInfo;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyPostsFragment extends PostListFragment {



    public MyPostsFragment() {}


    public Query getQuery(DatabaseReference databaseReference, int order) {
        // All my posts
        return databaseReference.child("user-posts")
                .child(getUid());
    }

}