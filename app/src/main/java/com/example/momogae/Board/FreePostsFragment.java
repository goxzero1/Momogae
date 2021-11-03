package com.example.momogae.Board;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class FreePostsFragment extends PostListFragment {
    @Override
    public Query getQuery(DatabaseReference databaseReference, int order) {

        Query freePostsQuery = databaseReference.child("posts/" + getBoardType())
                .limitToFirst(100);
        return freePostsQuery;
    }

    @Override
    public String getBoardType() {
        return BoardType.FREE.name().toLowerCase();
    }
}