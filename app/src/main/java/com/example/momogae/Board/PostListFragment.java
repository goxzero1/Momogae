package com.example.momogae.Board;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momogae.Login.SharedPreference;
import com.example.momogae.Main.models.Post;
import com.example.momogae.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public abstract class PostListFragment extends Fragment {

    final int POST = 0;
    private static final String TAG = "PostListFragment";
    private DatabaseReference mDatabase;
    private PostAdapter mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    public PostListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_posts, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler = rootView.findViewById(R.id.messagesList);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        initRecyclerAdapter();
    }

    private void initRecyclerAdapter() {
        mAdapter = new PostAdapter(getUid());

        Query postsQuery = getQuery(mDatabase, POST);
        postsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fetchPost(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mAdapter.setItemClickListener(new PostAdapter.ItemClickListener() {
            @Override
            public void onClick(int position, Post post) {
                // Launch PostDetailActivity
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, post.key);
                intent.putExtra(PostDetailActivity.EXTRA_TYPE, getBoardType());

                startActivity(intent);
            }
        });

        mRecycler.setAdapter(mAdapter);
    }

    private void fetchPost(DataSnapshot snapshot) {
        Log.e(TAG, "snapshot ==> " + snapshot);
        ArrayList<Post> items = new ArrayList<>();
        for (DataSnapshot item : snapshot.getChildren()) {
            Post temp = item.getValue(Post.class);
            temp.key = item.getKey();
            items.add(temp);
        }
        mAdapter.setPosts(items);
    }

    private void fetchSearchPost(DataSnapshot snapshot, String query) {
        Log.e(TAG, "snapshot ==> " + snapshot);
        ArrayList<Post> items = new ArrayList<>();
        for (DataSnapshot item : snapshot.getChildren()) {
            Post temp = item.getValue(Post.class);
            if (temp.body.contains(query)) {
                temp.key = item.getKey();
                items.add(temp);
            }
        }
        mAdapter.setPosts(items);
    }

    @Override
    public void onStart() {
        super.onStart();
//        if (mAdapter != null) {
//            mAdapter.startListening();
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (mAdapter != null) {
//            mAdapter.stopListening();
//        }
    }

    public String getUid() {
        return SharedPreference.getAttribute(getContext(),"userID");
    }


    public abstract Query getQuery(DatabaseReference databaseReference, int order);
    public abstract String getBoardType();

    public void updateUi(String query) {

        if (query.isEmpty()) {
            Query typeQuery = getQuery(mDatabase, POST);
            typeQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    fetchPost(snapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return;
        }

        Query searchQuery = getQuery(mDatabase, POST);
        searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fetchSearchPost(snapshot, query);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}