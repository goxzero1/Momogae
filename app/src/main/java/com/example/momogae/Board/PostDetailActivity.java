package com.example.momogae.Board;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momogae.Chat.ChatActivity;
import com.example.momogae.Login.SharedPreference;
import com.example.momogae.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PostDetailActivity";

    public static final String EXTRA_POST_KEY = "post_key";
    public static final String EXTRA_TYPE = "type";

    private DatabaseReference mPostReference;
    private StorageReference mImageReference;
    private DatabaseReference mCommentsReference;
    private ValueEventListener mPostListener;
    private String mPostKey;
    private String mType;
    private CommentAdapter mAdapter;

    private View mAuthor;
    private ImageView mAuthorPhotoView;
    private TextView mAuthorView;
    private TextView mTitleView;
    private ImageView mImageView;
    private TextView mBodyView;

    private ImageView mCommentPhotoView;
    private EditText mCommentField;
    private Button mCommentButton;
    private RecyclerView mCommentsRecycler;

    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        userID = SharedPreference.getAttribute(getApplicationContext(),"userID");
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        mType = getIntent().getStringExtra(EXTRA_TYPE);
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }


        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("posts/" + mType).child(mPostKey);
        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child("post-comments").child(mPostKey);

        mAuthor = (View) findViewById(R.id.Author);
        mAuthorPhotoView = findViewById(R.id.postAuthorPhoto);
        mAuthorView = findViewById(R.id.postAuthor);
        mTitleView = findViewById(R.id.postTitle);
        mImageView = findViewById(R.id.postImage);
        mImageView.setVisibility(View.GONE);
        mBodyView = findViewById(R.id.postBody);

        mCommentPhotoView = findViewById(R.id.commentPhoto);
        mCommentField = findViewById(R.id.fieldCommentText);
        mCommentButton = findViewById(R.id.buttonPostComment);
        mCommentsRecycler = findViewById(R.id.recyclerPostComments);
        mCommentButton.setOnClickListener(this);
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    public String getUid() {
        return SharedPreference.getAttribute(getApplicationContext(), "userID");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_boardchat, menu); // 채팅을 보내기 위해 메뉴 추가
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) { //Select 되었을 때
        if (item.getItemId() == R.id.send_chat) {
            Intent intent = new Intent(this, ChatActivity.class); //chatActivity 연결
            intent.putExtra("toUid", mAuthorView.getText()); //특정 사용자를 나타내는 Uid를 가져옴
            intent.putExtra("roomTitle", mAuthorView.getText());
            startActivity(intent); //ChatActivity 이동 Start
        }
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
        final long ONE_MEGABYTE = 1024 * 1024*1024;

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                PostModel postModel = dataSnapshot.getValue(PostModel.class);

                if(FirebaseStorage.getInstance().getReference().child(postModel.author + "/profile/profileImage") != null){
                    final long ONE_MEGABYTE = 1024 * 1024*1024;
                    FirebaseStorage.getInstance().getReference().child(postModel.author + "/profile/profileImage").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                            mAuthorPhotoView.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    });
                }
                mAuthorView.setText(postModel.author);
                mTitleView.setText(postModel.title);
                mBodyView.setText(postModel.body);

                if (!postModel.author.equals(userID)) {
                    mAuthor.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            return false;
                        }
                    });
                    registerForContextMenu(mAuthor);
                }

                mImageReference = FirebaseStorage.getInstance().getReference().child(postModel.author+"/"+ postModel.title);
                mImageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                        mImageView.setVisibility(mImageView.VISIBLE);
                        mImageView.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mPostReference.addValueEventListener(postListener);
        mPostListener = postListener;
        mAdapter = new CommentAdapter(this, mCommentsReference);
        mCommentsRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mPostListener != null) {
            mPostReference.removeEventListener(mPostListener);
        }

        mAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonPostComment) {
            postComment();
        }
    }

    private void postComment() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        String authorName = userModel.userID;
                        String commentText = mCommentField.getText().toString();
                        CommentModel commentModel = new CommentModel(uid, authorName, commentText);

                        mCommentsReference.push().setValue(commentModel);
                        if(FirebaseStorage.getInstance().getReference().child(userModel.userID + "/profile/profileImage") != null){
                            final long ONE_MEGABYTE = 1024 * 1024*1024;
                            FirebaseStorage.getInstance().getReference().child(userModel.userID + "/profile/profileImage").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                                    mCommentPhotoView.setImageBitmap(bitmap);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                }
                            });
                        }
                        mCommentField.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView authorView;
        public TextView bodyView;
        public ImageView profileView;

        public CommentViewHolder(View itemView) {
            super(itemView);

            authorView = itemView.findViewById(R.id.commentAuthor);
            bodyView = itemView.findViewById(R.id.commentBody);
            profileView = itemView.findViewById(R.id.commentPhoto);
        }
    }

    private static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private StorageReference mStorageReference;
        private ChildEventListener mChildEventListener;

        private List<String> mCommentIds = new ArrayList<>();
        private List<CommentModel> mCommentModels = new ArrayList<>();
        private List<Bitmap> mCommentPhotos = new ArrayList<>();

        public CommentAdapter(final Context context, DatabaseReference ref) {
            final long ONE_MEGABYTE = 1024 * 1024*1024;

            mContext = context;
            mDatabaseReference = ref;
            mStorageReference = FirebaseStorage.getInstance().getReference();

            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                    CommentModel commentModel = dataSnapshot.getValue(CommentModel.class);

                    mCommentIds.add(dataSnapshot.getKey());
                    mCommentModels.add(commentModel);
                    if(mStorageReference.child(commentModel.uid+"/profile/profileImage") != null) {
                        mStorageReference.child(commentModel.uid+"/profile/profileImage").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                                mCommentPhotos.add(bitmap);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {

                            }
                        });
                    }
                    notifyItemInserted(mCommentModels.size() - 1);
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    CommentModel newCommentModel = dataSnapshot.getValue(CommentModel.class);
                    String commentKey = dataSnapshot.getKey();

                    if(mStorageReference.child(newCommentModel.uid+"/profile/profileImage") != null) {
                        mStorageReference.child(newCommentModel.uid+"/profile/profileImage").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap newCommentPhoto = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {

                            }
                        });
                    }

                    final int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Replace with the new data
                        mCommentModels.set(commentIndex, newCommentModel);
                        if(mStorageReference.child(newCommentModel.uid+"/profile/profileImage") != null) {
                            mStorageReference.child(newCommentModel.uid+"/profile/profileImage").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap newCommentPhoto = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                                    mCommentPhotos.set(commentIndex, newCommentPhoto);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                }
                            });
                        }

                        notifyItemChanged(commentIndex); //변경사항 발생시에 업데이트
                    } else {

                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    String commentKey = dataSnapshot.getKey();

                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {

                        mCommentIds.remove(commentIndex);
                        mCommentModels.remove(commentIndex);
                        mCommentPhotos.remove(commentIndex);
                        notifyItemRemoved(commentIndex);

                    } else {

                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                    CommentModel movedCommentModel = dataSnapshot.getValue(CommentModel.class);
                    String commentKey = dataSnapshot.getKey();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            ref.addChildEventListener(childEventListener);
            mChildEventListener = childEventListener;
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final CommentViewHolder holder, int position) {
            CommentModel commentModel = mCommentModels.get(position);
            holder.authorView.setText(commentModel.author);
            holder.bodyView.setText(commentModel.text);

            if(FirebaseStorage.getInstance().getReference().child(commentModel.uid + "/profile/profileImage") != null){
                final long ONE_MEGABYTE = 1024 * 1024*1024;
                FirebaseStorage.getInstance().getReference().child(commentModel.uid + "/profile/profileImage").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                        holder.profileView.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mCommentModels.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }
}