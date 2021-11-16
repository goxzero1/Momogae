package com.example.momogae.Board;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.momogae.Login.SharedPreference;
import com.example.momogae.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {
    private static final int PICK_FROM_ALBUM = 1;

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";

    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    private EditText mTitleField;
    private EditText mBodyField;
    private ImageView mImageField;
    private FloatingActionButton mSubmitButton;
    private Spinner mSpinner;


    private String userID;
    int flagImage=0;

    public String getUid() {
        return SharedPreference.getAttribute(getApplicationContext(), "userID");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        userID = SharedPreference.getAttribute(getApplicationContext(),"userID");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference(); //파이어베이스 데이터정보를 읽어옴

        mTitleField = findViewById(R.id.fieldTitle); //제목
        mImageField = findViewById(R.id.imgPreview); //이미지
        mBodyField = findViewById(R.id.fieldBody); //글 내용
        mSubmitButton = findViewById(R.id.fabSubmitPost); //제출버튼
        mSpinner = findViewById(R.id.spinner_type); //글 타입


        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource
                (this, R.array.board_type, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //스피너 연결
        mSpinner.setAdapter(arrayAdapter); //스피너를 사용하여 글 타입을 선택

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_FROM_ALBUM && resultCode== RESULT_OK) {
            mImageField.setImageURI(data.getData());
            flagImage = PICK_FROM_ALBUM;
        }
    }


    private void submitPost(int flag) {
        final String title = mTitleField.getText().toString();
        final String body = mBodyField.getText().toString();

        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED); //글제목 필수
            return;
        }

        if (TextUtils.isEmpty(body)) {
            mBodyField.setError(REQUIRED); //글내용 필수
            return;
        }


        setEditingEnabled(false);
        Toast.makeText(this, "게시중...", Toast.LENGTH_SHORT).show();

        final String userID = getUid();
        mDatabase.child("users").child(userID).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String type = BoardType.findWithIndex(mSpinner.getSelectedItemPosition()).name().toLowerCase();
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);

                        if (userModel == null) {
                            Toast.makeText(NewPostActivity.this,
                                    "글 등록이 불가합니다.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            writeNewPost(userID, userModel.userID, title, body, type); //파이어베이스에 등록
                        }

                        setEditingEnabled(true);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        setEditingEnabled(true);
                    }
                });


        if(flag == 1) {

            mImageField.setDrawingCacheEnabled(true);
            mImageField.buildDrawingCache();

            Bitmap bitmap = ((BitmapDrawable) mImageField.getDrawable()).getBitmap();
            ByteArrayOutputStream uploadStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, uploadStream);
            byte[] bytes = uploadStream.toByteArray();

            UploadTask uploadTask = mStorage.child(userID + "/" + mTitleField.getText().toString()).putBytes(bytes);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }
    }

    private void setEditingEnabled(boolean enabled) {
        mTitleField.setEnabled(enabled);
        mBodyField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.show();
        } else {
            mSubmitButton.hide();
        }
    }


    private void writeNewPost(String userID, String username, String title, String body, String type) {

        String key = mDatabase.child("posts").push().getKey(); //게시글 별 키를 다르게 해야 하기 때문에 키를 받아옴
        PostModel postModel = new PostModel(userID, username, title, body, type); //게시글 정보에는 유저 아이디, 이름, 글 제목, 글 내용 그리고 타입이 있다.
        Map<String, Object> postValues = postModel.toMap(); //위 정보는 posts/type/key/ 경로의 하위 정보로 업데이트된다. (postValues)

        Map<String, Object> childUpdates = new HashMap<>(); // 파이어베이스에 데이터 업데이트
        childUpdates.put("/posts/" + type + "/" + key, postValues); //경로 : posts/type/key, +postValues
        childUpdates.put("/user-posts/" + userID + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id== R.id.action_settings) {
            submitPost(flagImage);
        }
        return super.onOptionsItemSelected(item);
    }
}