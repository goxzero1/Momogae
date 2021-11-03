package com.example.momogae.MyInfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.momogae.Login.SharedPreference;
import com.example.momogae.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class MyInfoActivity extends AppCompatActivity {

    final int PICK_FROM_ALBUM = 1;
    String userID;
    private TextView profileUserID;
    private ImageView profileImage;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_my_info);

        // for profile
        mStorage = FirebaseStorage.getInstance().getReference();
        userID = SharedPreference.getAttribute(getApplicationContext(), "userID");
        profileUserID = (TextView) findViewById(R.id.userID);
        profileUserID.setText(userID);
        profileImage = (ImageView) findViewById(R.id.profileImg);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });

        FirebaseStorage.getInstance().getReference(userID + "/profile").child("profileImage").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Glide.with(getApplicationContext()).load(R.drawable.ic_user)
                        .into(profileImage);
            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(profileImage);
            }
        });

    }


        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            //Date date = new Date(System.currentTimeMillis());
            super.onActivityResult(requestCode, resultCode, data);
            try {
                if (resultCode == RESULT_OK) {
                    if (requestCode == PICK_FROM_ALBUM) {
                        Uri selectedImageUri = data.getData();

                        // Set the image in ImageView
                        profileImage.setImageURI(selectedImageUri);

                        // [START upload_memory]
                        profileImage.setDrawingCacheEnabled(true);
                        profileImage.buildDrawingCache();

                        Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
                        ByteArrayOutputStream uploadStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, uploadStream);
                        byte[] bytes = uploadStream.toByteArray();

                        UploadTask uploadTask = mStorage.child(userID + "/profile/profileImage").putBytes(bytes);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Log.e("디버그", "업로드안됨*****************************");
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                // ...Log.e("디버그", "업로드됨!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11*****************************");}
                            }
                        });
                        // [END upload_memory]
                    }
                }
            } catch (Exception e) {
                Log.e("FileSelectorActivity", "File select error", e);
            }
        }
    }
