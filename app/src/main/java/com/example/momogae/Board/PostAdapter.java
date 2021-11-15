package com.example.momogae.Board;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momogae.Main.models.Post;
import com.example.momogae.R;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private ArrayList<Post> posts;
    private String mUid;
    private ItemClickListener mItemClickListener;

    public PostAdapter(String uid) {
        this.mUid = uid;
    }

    interface ItemClickListener {
        void onClick(int position, Post post);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new PostViewHolder(inflater.inflate(R.layout.item_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        Post post = posts.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onClick(position, post);
            }
        });

        holder.bindToPost(post, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onClick(position, post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts != null ? posts.size() : 0;
    }

    public void setItemClickListener(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }
}
