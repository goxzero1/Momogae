package com.example.momogae.MyPets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momogae.Login.SharedPreference;
import com.example.momogae.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyPetActivity extends AppCompatActivity  {

    private FloatingActionButton fab_add;
    public static ArrayList<PetModel> pet_Model_data;
    RecyclerView recyclerView;
    private DatabaseReference petDB;
    public static String userID;
    PetViewAdapter adapter;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userID = SharedPreference.getAttribute(getApplicationContext(), "userID");
        pet_Model_data = new ArrayList<PetModel>();
        setContentView(R.layout.activity_my_pet);
        mStorage = FirebaseStorage.getInstance().getReference();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.myPetRecyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new PetViewAdapter(pet_Model_data, MyPetActivity.this); //petViewAdpater 연결
        recyclerView.setAdapter(adapter);

        petDB = FirebaseDatabase.getInstance().getReference("/pet/" + userID);
        String sort_column_name = "petName";
        Query sortbyName = petDB.orderByChild(sort_column_name);
        sortbyName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pet_Model_data.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    PetModel get = postSnapshot.getValue(PetModel.class);
                    pet_Model_data.add(get);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab_add = (FloatingActionButton) findViewById(R.id.fabNewPet);
        fab_add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent write_intent = new Intent(MyPetActivity.this, PetWriteActivity.class);
                write_intent.putExtra("userID", userID);
                startActivity(write_intent);
            }
        });

    }
}