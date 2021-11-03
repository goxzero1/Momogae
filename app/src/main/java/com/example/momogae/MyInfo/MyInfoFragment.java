package com.example.momogae.MyInfo;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.momogae.Login.SharedPreference;
import com.example.momogae.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class MyInfoFragment extends Fragment {

    DatabaseReference mDatabaseReference;
    StorageReference mStorageReference;
    String userID;
    TextView userIDView;
    TextView userNameView;
    TextView userPhoneView;
    TextView userEmailView;
    EditText userPasswordView;
    EditText userPasswordConfirmView;
    Button modify;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        userID = SharedPreference.getAttribute(getContext(),"userID");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_info, container, false);

        userIDView = view.findViewById(R.id.userID);
        userNameView = view.findViewById(R.id.userName);
        userPhoneView = view.findViewById(R.id.userPhone);
        userEmailView = view.findViewById(R.id.userEmail);
        userPasswordView = view.findViewById(R.id.edt_password);
        userPasswordConfirmView = view.findViewById(R.id.edt_password_confirm);

        modify = view.findViewById(R.id.submit);
        modify.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                    mDatabaseReference.child("users").child(userID).child("Password").setValue(userPasswordConfirmView.getText().toString());
                    {

                        String user = mDatabaseReference.getKey();
                        Map<String, Object> childUpdates = new HashMap<>();


                        if(!userPasswordConfirmView.getText().toString().equals(userPasswordView.getText().toString())){
                            Toast.makeText(getActivity(),"비밀번호가 일치하지 않습니다.",Toast.LENGTH_LONG).show();
                            return;
                        } else  childUpdates.replace("password", userPasswordConfirmView.getText().toString());

                        mDatabaseReference.updateChildren(childUpdates);
                        Toast.makeText(getActivity(), "비밀번호를 변경하였습니다.", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        mDatabaseReference.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userIDView.setText(dataSnapshot.child("ID").getValue().toString());
                userNameView.setText(dataSnapshot.child("Name").getValue().toString());
                userPhoneView.setText(dataSnapshot.child("Phone").getValue().toString());
                userEmailView.setText(dataSnapshot.child("Email").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
}