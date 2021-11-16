package com.example.momogae.Board;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momogae.R;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private ArrayList<PostModel> postModels;
    private String mUid;
    private ItemClickListener mItemClickListener;

    public PostAdapter(String uid) {
        this.mUid = uid;
    }

    interface ItemClickListener {
        void onClick(int position, PostModel postModel);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new PostViewHolder(inflater.inflate(R.layout.item_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        PostModel postModel = postModels.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onClick(position, postModel);
            }
        });

        holder.bindToPost(postModel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onClick(position, postModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postModels != null ? postModels.size() : 0;
    }

    public void setItemClickListener(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void setPostModels(ArrayList<PostModel> postModels) {
        this.postModels = postModels;
        notifyDataSetChanged();
    }
}
