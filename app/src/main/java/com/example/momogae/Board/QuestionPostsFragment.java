package com.example.momogae.Board;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class QuestionPostsFragment extends PostListFragment {
    @Override
    public Query getQuery(DatabaseReference databaseReference, int order) {

        Query questionQuery = databaseReference.getRef().child("posts/" + getBoardType())
                .limitToFirst(10);
        return questionQuery;
    }

    @Override
    public String getBoardType() {
        return BoardType.QUESTION.name().toLowerCase();
    }
}
