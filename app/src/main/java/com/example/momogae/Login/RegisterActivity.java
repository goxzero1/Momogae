package com.example.momogae.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.momogae.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseFirestore firestore;
    private EditText editID;
    private EditText editPassword;
    private EditText editPasswordConfirmation;
    private EditText editPhone;
    private EditText editName;
    private EditText editEmail;


    private ValueEventListener checkRegister = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
            while (child.hasNext()) {
                if (editID.getText().toString().equals(child.next().getKey())) {
                    Toast.makeText(getApplicationContext(), "이미 존재하는 아이디입니다", Toast.LENGTH_LONG).show();
                    databaseReference.removeEventListener(this);
                    return;
                }
            }
            makeNewUser();
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);


        databaseReference  = FirebaseDatabase.getInstance().getReference("users");
        firestore = FirebaseFirestore.getInstance();

        editID = (EditText)findViewById(R.id.edt_id);
        editPassword = (EditText)findViewById(R.id.edt_password);
        editPasswordConfirmation = (EditText)findViewById(R.id.edt_password_confirm);
        editPhone = (EditText)findViewById(R.id.edt_phone);
        editName = (EditText)findViewById(R.id.edt_name);
        editEmail = (EditText)findViewById(R.id.edt_email);

        Button Submit = (Button)findViewById(R.id.submit);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editID.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "아이디는 필수개", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(editPassword.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "비밀번호는 필수개", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(editPasswordConfirmation.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "비밀번호 확인을 해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(editPhone.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "전화번호는 필수개", Toast.LENGTH_SHORT).show();
                    return;
                }



                if(!editPasswordConfirmation.getText().toString().equals(editPassword.getText().toString())){
                    Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다.",Toast.LENGTH_LONG).show();
                    return;
                }


                databaseReference.addListenerForSingleValueEvent(checkRegister);

                Intent intent  = new Intent(RegisterActivity.this, LoginActivity.class); //완료시에 로그인 액티비티로 돌아감
                startActivity(intent);
                finish();
            }
        });
    }
    void makeNewUser()
    {

        Map<String, Object> user = new HashMap<>(); //여기부터
        user.put("ID",editID.getText().toString());
        if(editName.getText().toString() != null) {
            user.put("name", editName.getText().toString());
        }
        else{
            user.put("name", "");
        }
        user.put("usermsg", "hello world!");
        user.put("token", "");
        user.put("userphoto", "");
        firestore.collection("users").document(editID.getText().toString()).set(user); //여기까지 firestore 저장내용

        Date date = new Date(System.currentTimeMillis());
        databaseReference.child(editID.getText().toString()).child("ID").setValue(editID.getText().toString());
        databaseReference.child(editID.getText().toString()).child("Password").setValue(editPassword.getText().toString());
        databaseReference.child(editID.getText().toString()).child("Phone").setValue(editPhone.getText().toString());
        databaseReference.child(editID.getText().toString()).child("userMsg").setValue("hello world!");
        databaseReference.child(editID.getText().toString()).child("RegistrationDate").setValue(date.toString());

        if(editName.getText().toString()!=null){

            databaseReference.child(editID.getText().toString()).child("Name").setValue(editName.getText().toString());
        }
        else{

            databaseReference.child(editID.getText().toString()).child("Name").setValue("No Data");
        }

        if(editEmail.getText().toString()!= null){

            databaseReference.child(editID.getText().toString()).child("Email").setValue(editEmail.getText().toString());        }
        else {

            databaseReference.child(editID.getText().toString()).child("Email").setValue("No Data");
        }

        Toast.makeText(getApplicationContext(), "모모개 가입을 환영하개", Toast.LENGTH_LONG).show();
    }
}
