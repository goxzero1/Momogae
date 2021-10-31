package com.example.momogae.Board;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class RecentPostsFragment extends PostListFragment {

    public RecentPostsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference, int order) {
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        Query recentPostsQuery = databaseReference.child("posts/" + getBoardType())
                .limitToFirst(100);
        // [END recent_posts_query]

        return recentPostsQuery;
    }

    @Override
    public String getBoardType() {
        return BoardType.FREE.name().toLowerCase();
    }
}