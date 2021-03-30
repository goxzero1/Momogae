package com.example.momogae.MyInfo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.momogae.Login.SharedPreference;
import com.example.momogae.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyInfoActivity extends AppCompatActivity {

    final int PICK_FROM_ALBUM = 1;
    String userID;

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private TextView profileUserID;
    private ImageView profileImage;

    private StorageReference mStorage;
    private FirebaseFirestore firestore;
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

        FirebaseStorage.getInstance().getReference(userID+"/profile").child("profileImage").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
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


        // FRAGMENT
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    new MyInfoFragment(),
                    //new MyPostsFragment(),
                    //new MyTopPostsFragment()
            };
            private final String[] mFragmentNames = new String[] {
                    getString(R.string.heading_my_info),
                    getString(R.string.heading_my_posts),
                    getString(R.string.heading_my_top_posts)
            };
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }
            @Override
            public int getCount() {
                return mFragments.length;
            }
            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };




        // NavigationView


    }





}