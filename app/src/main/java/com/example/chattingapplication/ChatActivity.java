package com.example.chattingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mToolbar;
    private RecyclerView allMessagesView;
    private CircleImageView contactImage;
    private TextView contactName, lastSeenTextView;

    private EditText messageInput;
    private ImageView sendBtn, attachBtn;

    private DatabaseReference mRootRef;

    private final List<Messages> messagesList = new ArrayList<>();
    private MessagesAdapter mAdapter;
    private LinearLayoutManager mLinearLayout;

    private String messageUserId, mCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mToolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        messageInput = findViewById(R.id.messageText);
        sendBtn = findViewById(R.id.sendMessageBtn);
        attachBtn = findViewById(R.id.attachment);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        messageUserId = getIntent().getStringExtra("UID");
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        allMessagesView = findViewById(R.id.allChatsList);
        mLinearLayout = new LinearLayoutManager(this);

        mAdapter = new MessagesAdapter(messagesList);
        allMessagesView.setHasFixedSize(true);
        allMessagesView.setLayoutManager(mLinearLayout);
        allMessagesView.setAdapter(mAdapter);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);

        contactImage = findViewById(R.id.contactImage);
        contactName = findViewById(R.id.contactName);
        lastSeenTextView = findViewById(R.id.contact_last_seen);

        setDetails();
        messagesList.clear();
        loadMessages();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!messageInput.getText().toString().isEmpty()) {
                    sendMessage(messageInput.getText().toString());
                }
            }
        });

    }

    private void loadMessages() {
        final DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(messageUserId);

        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = (Messages) dataSnapshot.getValue(Messages.class);

                if(dataSnapshot.getChildrenCount() > messagesList.size()) {
                    messagesList.add(message);
                    mAdapter.notifyDataSetChanged();
                }

                allMessagesView.smoothScrollToPosition(messagesList.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void sendMessage(String message) {

        String current_user_ref = "messages/" + mCurrentUserId + "/" + messageUserId;
        String chat_user_ref = "messages/" + messageUserId + "/" + mCurrentUserId;

        DatabaseReference user_message_push = mRootRef.child("messages")
                .child(mCurrentUserId).child(messageUserId).push();

        String push_id = user_message_push.getKey();

        Map messageMap = new HashMap();
        messageMap.put("message", message);
        messageMap.put("seen", false);
        messageMap.put( "type", "text");
        messageMap.put("time", ServerValue.TIMESTAMP);
        messageMap.put("from", mCurrentUserId);
        messageMap.put("id", push_id);
        messageMap.put("to", messageUserId);

        Map messageUserMap = new HashMap();
        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

        messageInput.setText("");

        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if(databaseError != null) {

                    Log.d("CHAT_LOG", databaseError.getMessage().toString());

                }

            }
        });

    }

    private void setDetails() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(messageUserId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("name")){
                    String name = dataSnapshot.child("name").getValue().toString();
                    contactName.setText(name);
                }
                if(dataSnapshot.hasChild("online")) {
                    if(!dataSnapshot.child("online").getValue().toString().equals("true")) {

                        String timeEpoch = dataSnapshot.child("online").getValue().toString();
                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                        long lastSeen = Long.parseLong(timeEpoch);
                        String time = getTimeAgo.getTimeAgo(lastSeen, ChatActivity.this);
                        lastSeenTextView.setText(time);

                    }else {
                        lastSeenTextView.setText("online");
                    }
                }
                if(dataSnapshot.hasChild("imageComp")){
                    String image = dataSnapshot.child("imageComp").getValue().toString();
                    Glide.with(contactImage.getContext()).load(image).into(contactImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String  currentUid = currentUser.getUid();

        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid).child("online").setValue("true");


    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if(currentUser != null) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("online").setValue(ServerValue.TIMESTAMP);
        }
    }


}
