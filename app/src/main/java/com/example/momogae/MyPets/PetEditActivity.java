package com.example.momogae.MyPets;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.momogae.Login.SharedPreference;
import com.example.momogae.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.example.momogae.MyPets.MyPetActivity.pet_Model_data;

public class PetEditActivity extends AppCompatActivity {
    private static final int PICK_FROM_ALBUM = 1;
    int flagImage=0;

    StorageReference mStorage;
    private ImageView profileImage;

    public static EditText update_age, update_gender, update_about, update_neutralization;
    private static TextView edit_name;
    String userID;
    private Button save_btn;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        String petName = intent.getStringExtra("petName");
        setContentView(R.layout.pet_full_edit);
        userID = SharedPreference.getAttribute(getApplicationContext(), "userID");

        mStorage = FirebaseStorage.getInstance().getReference();
        profileImage = (ImageView) findViewById(R.id.pet_profile);
        FirebaseStorage.getInstance().getReference("pet/" + userID + "/" + petName + "/profile").child("profileImage").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Glide.with(getApplicationContext()).load(R.drawable.ic_pet_cafe).into(profileImage);
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });

        edit_name = (TextView) findViewById(R.id.edit_name);


        update_age = (EditText) findViewById(R.id.edit_age);
        update_gender = (EditText) findViewById(R.id.edit_gender);
        update_about = (EditText) findViewById(R.id.edit_about);
        update_neutralization = (EditText) findViewById(R.id.edit_neutralization);


        update_age.setText(pet_Model_data.get(position).getPetAge());
        update_gender.setText(pet_Model_data.get(position).getPetGender());
        update_about.setText(pet_Model_data.get(position).getPetAbout());
        update_neutralization.setText(pet_Model_data.get(position).getPetNeutralization());

        String name = pet_Model_data.get(position).getPetName();
        edit_name.setText(name);

        save_btn = (Button) findViewById(R.id.edit_save);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //수정 과정
                databaseReference.child("pet").child(userID).child(name).child("petAge").setValue(update_age.getText().toString());
                {

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.replace("petAge", update_age.getText().toString());
                    databaseReference.updateChildren(childUpdates);

                }

                databaseReference.child("pet").child(userID).child(name).child("petGender").setValue(update_gender.getText().toString());
                {

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.replace("petGender", update_gender.getText().toString());
                    databaseReference.updateChildren(childUpdates);

                }

                databaseReference.child("pet").child(userID).child(name).child("petAbout").setValue(update_about.getText().toString());
                {

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.replace("petAbout", update_about.getText().toString());
                    databaseReference.updateChildren(childUpdates);

                }

                databaseReference.child("pet").child(userID).child(name).child("petNeutralization").setValue(update_neutralization.getText().toString());
                {

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.replace("petNeutralization", update_neutralization.getText().toString());
                    databaseReference.updateChildren(childUpdates);

                }


                if (flagImage == 1) {
                    // [START upload_memory]
                    profileImage.setDrawingCacheEnabled(true);
                    profileImage.buildDrawingCache();

                    Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
                    ByteArrayOutputStream uploadStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, uploadStream);
                    byte[] bytes = uploadStream.toByteArray();

                    UploadTask uploadTask = mStorage.child("pet/" + userID + "/" + name + "/profile/profileImage").putBytes(bytes);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });

                    PetEditActivity.this.finish();
                }

                PetEditActivity.this.finish();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==PICK_FROM_ALBUM) {
            Uri selectedImageUri = data.getData();
            profileImage.setImageURI(selectedImageUri);
            flagImage = 1;
        }
    }

}