package com.example.momogae.Board;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momogae.MainActivity.models.Post;
import com.example.momogae.R;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private ArrayList<Post> posts;
    private String mUid;
    private ItemClickListener mItemClickListener;
    private StarClickListener mStarClickListener;

    public PostAdapter(String uid) {
        this.mUid = uid;
    }

    public PostAdapter(ArrayList<Post> posts, String uid) {
        this.mUid = uid;
        this.posts = posts;
    }

    interface ItemClickListener {
        void onClick(int position, Post post);
    }

    interface StarClickListener {
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
        Log.e("TAG", "items => " + post.title );
//        Log.e("TAG", "ref => " + postRef );
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onClick(position, post);
            }
        });

        if (post.stars.containsKey(mUid)) {
            holder.starView.setImageResource(R.drawable.ic_star_black_24dp);
        } else {
            holder.starView.setImageResource(R.drawable.ic_star_border_black_24dp);
        }

        holder.bindToPost(post, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStarClickListener.onClick(position, post);
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

    public void setStarClickListener(StarClickListener mStarClickListener) {
        this.mStarClickListener = mStarClickListener;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }
}


//public class PostAdapter extends FirebaseRecyclerAdapter<Post, PostViewHolder> {
//
//    private ArrayList<Post> posts;
//    private String mUid;
//    private ItemClickListener mItemClickListener;
//    private StarClickListener mStarClickListener;
//
//    /**
//     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
//     * {@link FirebaseRecyclerOptions} for configuration options.
//     *
//     * @param options
//     */
//    public PostAdapter(String uid, @NonNull FirebaseRecyclerOptions<Post> options) {
//        super(options);
//        this.mUid = uid;
//    }
//
//    public PostAdapter(ArrayList<Post> posts, String uid, @NonNull FirebaseRecyclerOptions<Post> options) {
//        super(options);
//        this.mUid = uid;
//        this.posts = posts;
//    }
//
//    interface ItemClickListener {
//        void onClick(int position, String postKey);
//    }
//
//    interface StarClickListener {
//        void onClick(int position, Post post, String key);
//    }
//
//    @NonNull
//    @Override
//    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        return new PostViewHolder(inflater.inflate(R.layout.item_post, parent, false));
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull PostViewHolder postViewHolder, int position, @NonNull Post post) {
//        Log.e("TAG", "items => " + post.title );
//        final DatabaseReference postRef = getRef(position);
//        Log.e("TAG", "ref => " + postRef );
//
//        // Set click listener for the whole post view
//        final String postKey = postRef.getKey();
//
//        postViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mItemClickListener.onClick(position, postKey);
//            }
//        });
//
//        if (post.stars.containsKey(mUid)) {
//            postViewHolder.starView.setImageResource(R.drawable.ic_star_black_24dp);
//        } else {
//            postViewHolder.starView.setImageResource(R.drawable.ic_star_border_black_24dp);
//        }
//
//        postViewHolder.bindToPost(post, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mStarClickListener.onClick(position, post, postRef.getKey());
//            }
//        });
//    }
//
//    public void setItemClickListener(ItemClickListener mItemClickListener) {
//        this.mItemClickListener = mItemClickListener;
//    }
//
//    public void setStarClickListener(StarClickListener mStarClickListener) {
//        this.mStarClickListener = mStarClickListener;
//    }
//
//    public void setPosts(ArrayList<Post> posts) {
//        this.posts = posts;
//        notifyDataSetChanged();
//    }
//}
