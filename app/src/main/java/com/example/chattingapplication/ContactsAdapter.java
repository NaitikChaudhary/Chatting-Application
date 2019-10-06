package com.example.chattingapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    public List<Users> mList = new ArrayList<>();

    public ContactsAdapter(List<Users> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_user_info ,parent, false);

        return new ContactsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position) {


        Users users = mList.get(position);

        FirebaseDatabase.getInstance().getReference().child("Users").child(users.getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    holder.mUserName.setText(name);
                    if(dataSnapshot.hasChild("imageComp")) {
                        String image = dataSnapshot.child("imageComp").getValue().toString();
                        Glide.with(holder.mUserName.getContext())
                                .load(image)
                                .into(holder.mUserImage);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView mUserImage;
        public TextView mUserName, onlineStatus;
        public ImageView onlineIcon;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            mUserImage = itemView.findViewById(R.id.profileImage);
            mUserName = itemView.findViewById(R.id.userSingleName);
            onlineIcon = itemView.findViewById(R.id.onlineIcon);
            onlineStatus = itemView.findViewById(R.id.onlineText);
        }
    }

}
