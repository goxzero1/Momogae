package com.example.momogae.Board;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class SharePostsFragment extends PostListFragment {
    @Override
    public Query getQuery(DatabaseReference databaseReference, int order) {

        Query shareQuery = databaseReference.getRef().child("posts/" + getBoardType())
                .limitToFirst(100);
        return shareQuery;
    }

    @Override
    public String getBoardType() {
        return BoardType.SHARE.name().toLowerCase();
    }
}
