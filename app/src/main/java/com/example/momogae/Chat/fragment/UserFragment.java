package com.example.momogae.Chat.fragment;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.momogae.Chat.model.UserModel;
import com.example.momogae.Login.SharedPreference;
import com.example.momogae.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class UserFragment extends Fragment {


    DatabaseReference mDatabaseReference;
    StorageReference mStorageReference;

    private String userID;
    private ImageView user_photo;
    private EditText user_name;
    private EditText user_msg;

    private UserModel userModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        super.onCreate(savedInstanceState);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        userID = SharedPreference.getAttribute(container.getContext(),"userID");
        user_name = view.findViewById(R.id.user_name);
        user_name.setEnabled(false);
        user_msg = view.findViewById(R.id.user_msg);
        user_photo = view.findViewById(R.id.user_photo);



        Button saveBtn = view.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(saveBtnClickListener);
        //Button changePWBtn = view.findViewById(R.id.changePWBtn);
        //changePWBtn.setOnClickListener(changePWBtnClickListener);

        getUserInfoFromServer();
        return view;
    }

    void getUserInfoFromServer(){
        //String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String uid = SharedPreference.getAttribute(getContext(), "userID");

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(uid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userModel = documentSnapshot.toObject(UserModel.class);
                user_name.setText(userModel.getUsernm());
                user_msg.setText(userModel.getUsermsg());

                FirebaseStorage.getInstance().getReference(userModel.ID+"/profile").child("profileImage").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Glide.with(getActivity()).load(R.drawable.ic_user)
                                .into(user_photo);
                    }
                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getActivity())
                                .load(uri)
                                .into(user_photo);
                    }
                });
            }
        });
    }



    Button.OnClickListener saveBtnClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        public void onClick(final View view) {
            mDatabaseReference.child("users").child(userID).child("userMsg").setValue(user_msg.getText().toString());
            {

                String user = mDatabaseReference.getKey();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.replace("userMsg", user_msg.getText().toString());


                mDatabaseReference.updateChildren(childUpdates);
                Toast.makeText(getActivity(), "상태메시지를 변경하였습니다.", Toast.LENGTH_SHORT).show();

            }


        }
    };


}
