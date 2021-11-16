package com.example.momogae.Board;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momogae.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public TextView bodyView;
    public ImageView profileView;
    public ImageView imageFlagView;
    private StorageReference mStorage;


    public PostViewHolder(View itemView) {
        super(itemView);

        mStorage = FirebaseStorage.getInstance().getReference();

        imageFlagView = itemView.findViewById(R.id.flagImage);
        imageFlagView.setVisibility(View.GONE);
        titleView = itemView.findViewById(R.id.postTitle);
        authorView = itemView.findViewById(R.id.postAuthor);
        bodyView = itemView.findViewById(R.id.postBody);
        profileView = itemView.findViewById(R.id.postAuthorPhoto);
    }

    public void bindToPost(PostModel postModel, View.OnClickListener starClickListener) {
        titleView.setText(postModel.title);
        authorView.setText(postModel.author);
        bodyView.setText(postModel.body);

        if(mStorage.child(postModel.author + "/profile/profileImage") != null){
            final long ONE_MEGABYTE = 1024 * 1024*1024;
            mStorage.child(postModel.author + "/profile/profileImage").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                    profileView.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    profileView.setImageResource(R.drawable.ic_user);
                }
            });
        }

        mStorage.child(postModel.author+"/"+ postModel.title).getDownloadUrl().addOnSuccessListener(new OnSuccessListener(){

            @Override
            public void onSuccess(Object o) {
                imageFlagView.setVisibility(View.VISIBLE);
                imageFlagView.setImageResource(R.drawable.ic_image_black_24dp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //imageFlagView.setVisibility(GONE);
            }
        });
    }
}