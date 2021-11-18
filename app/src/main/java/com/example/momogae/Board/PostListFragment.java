package com.example.momogae.Board;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momogae.Login.SharedPreference;
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
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
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

        mAdapter.setItemClickListener(new PostAdapter.ItemClickListener() { //포스트어댑터 리사이클러뷰 클릭시
            @Override
            public void onClick(int position, PostModel postModel) {
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postModel.key);
                intent.putExtra(PostDetailActivity.EXTRA_TYPE, getBoardType());

                startActivity(intent); //PostDetailActivity 시작
            }
        });

        mRecycler.setAdapter(mAdapter);
    }

    private void fetchPost(DataSnapshot snapshot) {

        ArrayList<PostModel> items = new ArrayList<>();
        for (DataSnapshot item : snapshot.getChildren()) {
            PostModel temp = item.getValue(PostModel.class);
            temp.key = item.getKey();
            items.add(temp);
        }
        mAdapter.setPostModels(items);
    }

    private void fetchSearchPost(DataSnapshot snapshot, String query) {
        ArrayList<PostModel> items = new ArrayList<>();
        for (DataSnapshot item : snapshot.getChildren()) {
            PostModel temp = item.getValue(PostModel.class);
            if (temp.body.contains(query)) {
                temp.key = item.getKey();
                items.add(temp); //query(키워드)발견시 어댑터에 추가
            }
        }
        mAdapter.setPostModels(items);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

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