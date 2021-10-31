package com.example.momogae.MainActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.momogae.Abandoned.AbandonedActivity;
import com.example.momogae.Board.BoardActivity;
import com.example.momogae.Chat.SplashActivity;
import com.example.momogae.Login.SharedPreference;
import com.example.momogae.Map.MapActivity;
import com.example.momogae.MyInfo.MyInfoActivity;
import com.example.momogae.MyPets.MyPetActivity;
import com.example.momogae.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;


public class Main extends AppCompatActivity {
    private StorageReference mStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button imageButton1 = (Button) findViewById(R.id.butt1);
        Button imageButton2 = (Button) findViewById(R.id.butt2);
        Button imageButton3 = (Button) findViewById(R.id.butt3);
        Button imageButton4 = (Button) findViewById(R.id.butt4);
        Button imageButton5 = (Button) findViewById(R.id.buttonTop);
        CircleImageView profilecircleimage = (CircleImageView)  findViewById((R.id.profileImg2));

        mStorage = FirebaseStorage.getInstance().getReference();
        String userID = SharedPreference.getAttribute(getApplicationContext(), "userID");


        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BoardActivity.class);
                startActivity(intent);
            }

        });

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                startActivity(intent);
            }

        });

        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPetActivity.class);
                startActivity(intent);
            }

        });

        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }

        });

        imageButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AbandonedActivity.class);
                startActivity(intent);
            }

        });

        FirebaseStorage.getInstance().getReference(userID+"/profile").child("profileImage").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Glide.with(getApplicationContext()).load(R.drawable.ic_user)
                        .into(profilecircleimage);
            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(profilecircleimage);
            }
        });


        profilecircleimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyInfoActivity.class);
                startActivity(intent);
            }

        });
    }
}