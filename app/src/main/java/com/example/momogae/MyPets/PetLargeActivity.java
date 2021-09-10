package com.example.momogae.MyPets;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.momogae.Diary.DiaryActivity;
import com.example.momogae.Login.SharedPreference;
import com.example.momogae.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import static com.example.momogae.MyPets.MyPetActivity.pet_data;

public class PetLargeActivity extends AppCompatActivity {
    final int REQUEST_GET_SINGLE_FILE = 1;
    int flagImage=0;

    StorageReference mStorage;
    private ImageView profileImage;

    private static TextView species, name, firstdate, age, gender, neutralization, bestFriend;
    String petKey, userID, petName;
    public static Button edit_btn, delete_btn, diary_btn;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        petName = intent.getStringExtra("petName");
        setContentView(R.layout.pet_full_size);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userID = SharedPreference.getAttribute(getApplicationContext(), "userID");

        mStorage = FirebaseStorage.getInstance().getReference();
        profileImage = (ImageView) findViewById(R.id.pet_profile);
        FirebaseStorage.getInstance().getReference("pet/" + userID + "/" + petName + "/profile").child("profileImage").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Data for "images/island.jpg" is returns, use this as needed
                Glide.with(profileImage.getContext()).load(uri).into(profileImage);
            }
        });

        species = (TextView) findViewById(R.id.pet_species);
        name = (TextView) findViewById(R.id.pet_name);
        firstdate = (TextView) findViewById(R.id.pet_firstdate);
        age = (TextView) findViewById(R.id.pet_age);
        gender = (TextView) findViewById(R.id.pet_gender);
        neutralization = (TextView) findViewById(R.id.pet_neutralization);
        bestFriend = (TextView) findViewById(R.id.pet_BFF);


        species.setText(pet_data.get(position).getPetSpecies());
        name.setText(pet_data.get(position).getPetName());
        firstdate.setText(pet_data.get(position).getPetFirstDate());
        age.setText(pet_data.get(position).getPetAge());
        gender.setText(pet_data.get(position).getPetGender());
        neutralization.setText(pet_data.get(position).getPetNeutralization());
        bestFriend.setText(pet_data.get(position).getPetBff());

        userID = SharedPreference.getAttribute(getApplicationContext(), "userID");

        edit_btn = (Button) findViewById(R.id.edit_pet_btn);
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit_intent = new Intent(PetLargeActivity.this, PetEditActivity.class);
                edit_intent.putExtra("position", position);
                edit_intent.putExtra("petName", pet_data.get(position).getPetName());
                startActivity(edit_intent);
            }
        });

        delete_btn = (Button) findViewById(R.id.delete_pet_btn);
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/pet/" + userID + "/" + petKey, null);
                databaseReference.updateChildren(childUpdates);
                System.out.println("done deleting");
                finish();
            }
        });

        diary_btn = (Button) findViewById(R.id.diary_btn);
        diary_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PetLargeActivity.this, DiaryActivity.class);
                intent.putExtra("petName", name.getText().toString());
                startActivity(intent);
            }
        });

    }


}