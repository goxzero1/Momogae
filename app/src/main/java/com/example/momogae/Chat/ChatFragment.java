package com.example.momogae.Chat;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.momogae.Login.SharedPreference;
import com.example.momogae.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;

public class ChatFragment extends Fragment {
    private static final int PICK_FROM_ALBUM = 1;
    private static String rootPath = Util.getRootPath()+"/DirectTalk9/";

    private Button sendBtn;
    private EditText msg_input;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter mAdapter;
    private SimpleDateFormat dateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateFormatHour = new SimpleDateFormat("aa hh:mm");
    private String roomID;
    private String myUid;
    private String toUid;
    private Map<String, UserModel> userList = new HashMap<>();

    private ListenerRegistration listenerRegistration;
    private FirebaseFirestore firestore=null;
    private StorageReference storageReference;
    private LinearLayoutManager linearLayoutManager;

    private ProgressDialog progressDialog = null;
    private Integer userCount = 0;

    public ChatFragment() {
    }

    public static final ChatFragment getInstance(String toUid, String roomID) {
        ChatFragment f = new ChatFragment();
        Bundle bdl = new Bundle();
        bdl.putString("toUid", toUid);
        bdl.putString("roomID", roomID);
        f.setArguments(bdl);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        msg_input = view.findViewById(R.id.msg_input);
        sendBtn = view.findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(sendBtnClickListener);

        view.findViewById(R.id.imageBtn).setOnClickListener(imageBtnClickListener);

        view.findViewById(R.id.msg_input).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    Util.hideKeyboard(getActivity());
                }
            }
        });



        if (getArguments() != null) {
            roomID = getArguments().getString("roomID");
            toUid = getArguments().getString("toUid");
        }

        firestore = FirebaseFirestore.getInstance();
        storageReference  = FirebaseStorage.getInstance().getReference();

        dateFormatDay.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        dateFormatHour.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        myUid = SharedPreference.getAttribute(getContext(),"userID");

        /*

         two user: roomid or uid talking

         multi user: roomid

         */

        if (!"".equals(toUid) && toUid!=null) {                     // find existing room for two user
            findChatRoom(toUid);
        } else
        if (!"".equals(roomID) && roomID!=null) { // existing room (multi user)
            setChatRoom(roomID);
        };

        if (roomID==null) {                                                     // new room for two user
            getUserInfoFromServer(myUid);
            getUserInfoFromServer(toUid);
            userCount = 2;
        };

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (mAdapter!=null & bottom < oldBottom) {
                    final int lastAdapterItem = mAdapter.getItemCount() - 1;
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            int recyclerViewPositionOffset = -1000000;
                            View bottomView = linearLayoutManager.findViewByPosition(lastAdapterItem);
                            if (bottomView != null) {
                                recyclerViewPositionOffset = 0 - bottomView.getHeight();
                            }
                            linearLayoutManager.scrollToPositionWithOffset(lastAdapterItem, recyclerViewPositionOffset);
                        }
                    });
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    // get a user info
    void getUserInfoFromServer(String id){
        firestore.collection("users").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserModel userModel = documentSnapshot.toObject(UserModel.class);
                userList.put(userModel.getUid(), userModel);
                if (roomID != null & userCount == userList.size()) {
                    mAdapter = new RecyclerViewAdapter();
                    recyclerView.setAdapter(mAdapter);
                }
            }
        });
    }

    // Returns the room ID after locating the chatting room with the user ID.
    void findChatRoom(final String toUid){
        firestore.collection("rooms").whereGreaterThanOrEqualTo("users."+myUid, 0).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {return;}

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Long> users = (Map<String, Long>) document.get("users");
                            if (users.size()==2 & users.get(toUid)!=null){
                                setChatRoom(document.getId());
                                break;
                            }
                        }
                    }
                });

    }

    // get user list in a chatting room
    void setChatRoom(String rid) {
        roomID = rid;
        firestore.collection("rooms").document(roomID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.isSuccessful()) {return;}
                DocumentSnapshot document = task.getResult();
                Map<String, Long> users = (Map<String, Long>) document.get("users");

                for( String key : users.keySet() ){
                    getUserInfoFromServer(key);
                }
                userCount = users.size();
                //users.put(myUid, (long) 0);
                //document.getReference().update("users", users);
            }
        });
    }

    void setUnread2Read() {
        if (roomID==null) return;

        firestore.collection("rooms").document(roomID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.isSuccessful()) {return;}
                DocumentSnapshot document = task.getResult();
                Map<String, Long> users = (Map<String, Long>) document.get("users");

                users.put(myUid, (long) 0);
                document.getReference().update("users", users);
            }

        });

    }

    public void CreateChattingRoom(final DocumentReference room) {
        Map<String, Integer> users = new HashMap<>();
        String title = "";
        for( String key : userList.keySet() ){
            users.put(key, 0);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("title", null);
        data.put("users", users);

        room.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mAdapter = new RecyclerViewAdapter();
                    recyclerView.setAdapter(mAdapter);
                }
            }
        });
    }

    public Map<String, UserModel> getUserList() {
        return userList;
    }

    Button.OnClickListener sendBtnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            String msg = msg_input.getText().toString();
            sendMessage(msg, "0", null);
            msg_input.setText("");
        }

    };

    private void sendMessage(final String msg, String msgtype, final ChatModel.FileInfo fileinfo) {
        sendBtn.setEnabled(false);

        if (roomID==null) {             // create chatting room for two user
            roomID = firestore.collection("rooms").document().getId();
            CreateChattingRoom( firestore.collection("rooms").document(roomID) );
        }

        final Map<String,Object> messages = new HashMap<>();
        messages.put("uid", myUid);
        messages.put("msg", msg);
        messages.put("msgtype", msgtype);
        messages.put("timestamp", FieldValue.serverTimestamp());
        if (fileinfo!=null){
            messages.put("filename", fileinfo.filename);
            messages.put("filesize", fileinfo.filesize);
        }

        final DocumentReference docRef = firestore.collection("rooms").document(roomID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.isSuccessful()) {return;}

                WriteBatch batch = firestore.batch();

                // save last message
                batch.set(docRef, messages, SetOptions.merge());

                // save message
                List<String> readUsers = new ArrayList();
                readUsers.add(myUid);
                messages.put("readUsers", readUsers);//new String[]{myUid} );
                batch.set(docRef.collection("messages").document(), messages);

                // inc unread message count

                DocumentSnapshot document = task.getResult();
                Map<String, Long> users = (Map<String, Long>) document.get("users");

                for( String key : users.keySet() ){
                    if (!myUid.equals(key)) users.put(key, users.get(key)+1);
                }
                document.getReference().update("users", users);

                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //sendGCM();
                            sendBtn.setEnabled(true);
                        }
                    }
                });
            }

        });
    };

    // choose image
    Button.OnClickListener imageBtnClickListener = new View.OnClickListener() {
        public void onClick(final View view) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, PICK_FROM_ALBUM);
        }
    };


    // uploading image / file
    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode!= RESULT_OK) { return;}
        Uri fileUri = data.getData();
        final String filename = Util.getUniqueValue();

        showProgressDialog("이미지를 보내는 중 입니다.");
        final ChatModel.FileInfo fileinfo  = getFileDetailFromUri(getContext(), fileUri);

        storageReference.child("files/"+filename).putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                sendMessage(filename, Integer.toString(requestCode), fileinfo);
                hideProgressDialog();
            }

        });

        if (requestCode != PICK_FROM_ALBUM) { return;}

        // small image
        Glide.with(getContext())
                .asBitmap()
                .load(fileUri)
                .apply(new RequestOptions().override(300, 300))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
                        storageReference.child("filesmall/"+filename).putBytes(data);
                    }

                });

    }

    // get file name and size from Uri
    public static ChatModel.FileInfo getFileDetailFromUri(final Context context, final Uri uri) {
        if (uri == null) { return null; }

        ChatModel.FileInfo fileDetail = new ChatModel.FileInfo();
        // File Scheme.
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            File file = new File(uri.getPath());
            fileDetail.filename = file.getName();
            fileDetail.filesize = Util.size2String(file.length());
        }

        // Content Scheme.
        else if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            Cursor returnCursor =
                    context.getContentResolver().query(uri, null, null, null, null);
            if (returnCursor != null && returnCursor.moveToFirst()) {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                fileDetail.filename = returnCursor.getString(nameIndex);
                fileDetail.filesize = Util.size2String(returnCursor.getLong(sizeIndex));
                returnCursor.close();

            }
        }

        return fileDetail;

    }

    public void showProgressDialog(String title ) {
        if (progressDialog==null) {
            progressDialog = new ProgressDialog(getContext());
        }

        progressDialog.setIndeterminate(true);
        progressDialog.setTitle(title);
        progressDialog.setMessage("잠시만 기다려 주시개");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    // =======================================================================================



    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        final private RequestOptions requestOptions = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(90));

        List<MessageModel> messageModelList;
        String beforeDay = null;


        RecyclerViewAdapter() {
            File dir = new File(rootPath);
            if (!dir.exists()) {
                if (!Util.isPermissionGranted(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    return;
                }
                dir.mkdirs();
            }

            messageModelList = new ArrayList<MessageModel>();
            setUnread2Read();
            startListening();
        }

        public void startListening() {
            beforeDay = null;
            messageModelList.clear();

            CollectionReference roomRef = firestore.collection("rooms").document(roomID).collection("messages");
            // my chatting room information
            listenerRegistration = roomRef.orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {return;}
                    MessageModel messageModel;
                    for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                        switch (change.getType()) {
                            case ADDED:
                                messageModel = change.getDocument().toObject(MessageModel.class);
                                //if (messageModel.msg !=null & messageModel.timestamp == null) {continue;} // FieldValue.serverTimestamp is so late
                                if (messageModel.getReadUsers().indexOf(myUid) == -1) {
                                    messageModel.getReadUsers().add(myUid);
                                    change.getDocument().getReference().update("readUsers", messageModel.getReadUsers());
                                }
                                messageModelList.add(messageModel);
                                notifyItemInserted(change.getNewIndex());
                                break;

                            case MODIFIED:
                                messageModel = change.getDocument().toObject(MessageModel.class);
                                messageModelList.set(change.getOldIndex(), messageModel);
                                notifyItemChanged(change.getOldIndex());
                                break;

                            case REMOVED:
                                messageModelList.remove(change.getOldIndex());
                                notifyItemRemoved(change.getOldIndex());
                                break;
                        }
                    }
                    recyclerView.scrollToPosition(messageModelList.size() - 1);
                }
            });

        }


        public void stopListening() {
            if (listenerRegistration != null) {
                listenerRegistration.remove();
                listenerRegistration = null;
            }

            messageModelList.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            MessageModel messageModel = messageModelList.get(position);
            if (myUid.equals(messageModel.getUid()) ) {
                switch(messageModel.getMsgtype()){
                    case "1": return R.layout.item_chatimage_right;
                    default:  return R.layout.item_chatmsg_right;
                }

            } else {
                switch(messageModel.getMsgtype()){
                    case "1": return R.layout.item_chatimage_left;
                    default:  return R.layout.item_chatmsg_left;

                }

            }

        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = null;
            view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
            final MessageModel messageModel = messageModelList.get(position);

            setReadCounter(messageModel, messageViewHolder.read_counter);

            if ("0".equals(messageModel.getMsgtype())) {                                      // text messageModel
                messageViewHolder.msg_item.setText(messageModel.getMsg());
            } else
            {                                                               // image transfer
                messageViewHolder.realname = messageModel.getMsg();
                Glide.with(getContext())
                        .load(storageReference.child("filesmall/"+ messageModel.getMsg()))
                        .apply(new RequestOptions().override(800, 800))
                        .into(messageViewHolder.img_item);
            }

            if (! myUid.equals(messageModel.getUid())) {
                UserModel userModel = userList.get(messageModel.getUid());
                messageViewHolder.msg_name.setText(userModel.getUsernm());

                if (FirebaseStorage.getInstance().getReference(userModel.ID+"/profile").child("profileImage")==null) {
                    Glide.with(getContext()).load(R.drawable.ic_user)
                            .apply(requestOptions)
                            .into(messageViewHolder.user_photo);
                } else{
                    Glide.with(getContext())
                            .load(FirebaseStorage.getInstance().getReference(userModel.ID+"/profile/profileImage"))
                            .apply(requestOptions)
                            .into(messageViewHolder.user_photo);
                }
            }

            messageViewHolder.divider.setVisibility(View.INVISIBLE);
            messageViewHolder.divider.getLayoutParams().height = 0;
            messageViewHolder.timestamp.setText("");
            if (messageModel.getTimestamp()==null) {return;}

            String day = dateFormatDay.format( messageModel.getTimestamp());
            String timestamp = dateFormatHour.format( messageModel.getTimestamp());
            messageViewHolder.timestamp.setText(timestamp);

            if (position==0) {
                messageViewHolder.divider_date.setText(day);
                messageViewHolder.divider.setVisibility(View.VISIBLE);
                messageViewHolder.divider.getLayoutParams().height = 60;
            } else {
                MessageModel beforeMsg = messageModelList.get(position - 1);
                String beforeDay = dateFormatDay.format( beforeMsg.getTimestamp() );

                if (!day.equals(beforeDay) && beforeDay != null) {
                    messageViewHolder.divider_date.setText(day);
                    messageViewHolder.divider.setVisibility(View.VISIBLE);
                    messageViewHolder.divider.getLayoutParams().height = 60;

                }

            }

        }

        void setReadCounter (MessageModel messageModel, final TextView textView) {
            int cnt = userCount - messageModel.getReadUsers().size();
            if (cnt > 0) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(String.valueOf(cnt));
            } else {
                textView.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return messageModelList.size();
        }

    }

    private class MessageViewHolder extends RecyclerView.ViewHolder {
        public ImageView user_photo;
        public TextView msg_item;
        public ImageView img_item;          // only item_chatimage_
        public TextView msg_name;
        public TextView timestamp;
        public TextView read_counter;
        public LinearLayout divider;
        public TextView divider_date;
        public String filename;
        public String realname;

        public MessageViewHolder(View view) {
            super(view);
            user_photo = view.findViewById(R.id.user_photo);
            msg_item = view.findViewById(R.id.msg_item);
            img_item = view.findViewById(R.id.img_item);
            timestamp = view.findViewById(R.id.timestamp);
            msg_name = view.findViewById(R.id.msg_name);
            read_counter = view.findViewById(R.id.read_counter);
            divider = view.findViewById(R.id.divider);
            divider_date = view.findViewById(R.id.divider_date);

        }
    }



    public void backPressed() {

    }

}