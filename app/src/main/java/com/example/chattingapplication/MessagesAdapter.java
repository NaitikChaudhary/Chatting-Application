package com.example.chattingapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>{

    public List<Messages> mList;
    String image;

    public MessagesAdapter(List<Messages> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_message_layout ,parent, false);

        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        final Messages c = mList.get(position);
        Messages k = null;
        Messages m = null;
        final String from_user_before;
        String from_user_after;
        if (position > 0) {
            k = mList.get(position - 1);
        }
        if(position + 1 < mList.size()) {
            m = mList.get(position + 1);
        }

        if(k == null) {
            from_user_before = null;
        } else {
            from_user_before = k.getFrom();
        }

        if(m == null) {
            from_user_after = null;
        } else {
            from_user_after = m.getFrom();
        }

        final String from_user = c.getFrom();
        String message = c.getMessage();

        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("imageComp")) {
                    image = dataSnapshot.child("imageComp").getValue().toString();
                    Picasso.with(holder.profileImage.getContext()).load(image).placeholder(R.drawable.profile_image).into(holder.profileImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String displayUser = mUserDatabase.getKey();


        holder.messageText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final int position = holder.getAdapterPosition();

                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("messages");

                CharSequence options[] = new CharSequence[]{"Delete this message for me", "Cancel"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.messageText.getContext());

                builder.setTitle("Select Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 0) {
                            reference.child(currentUser).child(c.getTo()).child(c.getId()).removeValue();
                            mList.remove(position);
                            notifyDataSetChanged();
                        } else if(which == 1) {
                            dialog.dismiss();
                        }

                    }
                });

                builder.show();

                return false;

            }

        });

        holder.messageTextUser.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final int position = holder.getAdapterPosition();

                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("messages");

                CharSequence options[] = new CharSequence[]{"Delete this message for me", "Delete for everyone", "Cancel"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.messageText.getContext());

                builder.setTitle("Select Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 0) {

                            reference.child(currentUser).child(c.getTo()).child(c.getId()).removeValue();
                            mList.remove(position);
                            notifyDataSetChanged();

                        } else if(which == 1) {

                            reference.child(c.getTo()).child(currentUser).child(c.getId()).removeValue();
                            reference.child(currentUser).child(c.getTo()).child(c.getId()).removeValue();
                            mList.remove(position);
                            notifyDataSetChanged();

                        } else if (which == 2) {
                            dialog.dismiss();
                        }

                    }
                });

                builder.show();

                return false;
            }
        });

        holder.messageText.setVisibility(View.GONE);
        holder.profileImage.setVisibility(View.GONE);
        holder.messageTextUser.setVisibility(View.GONE);

        if(from_user_before == null) {
            holder.messageContainer.setPadding(0,40,0,0);
        } else if (!from_user_before.equals(displayUser)) {
            holder.messageContainer.setPadding(0,30,0,0);
        }


        if (displayUser.equals(currentUser)) {

            holder.messageTextUser.setText(message);
            holder.messageTextUser.setVisibility(View.VISIBLE);
            if(from_user_after != null ) {
                if(!from_user_after.equals(currentUser)){
                    holder.messageTextUser.setBackgroundResource(R.drawable.self_chat_box_end);
                }
            } else {
                holder.messageTextUser.setBackgroundResource(R.drawable.self_chat_box_end);
            }

        } else {
            holder.messageText.setText(message);
            holder.messageText.setVisibility(View.VISIBLE);
            if(from_user_after != null ) {
                if(!from_user_after.equals(displayUser)){
                    holder.messageText.setBackgroundResource(R.drawable.contact_chat_box_end);
                    holder.profileImage.setVisibility(View.VISIBLE);
                }
            } else {
                holder.messageText.setBackgroundResource(R.drawable.contact_chat_box_end);
                holder.profileImage.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText,messageTextUser;
        public CircleImageView profileImage;
        private RelativeLayout messageContainer;

        public MessageViewHolder(View view) {
            super(view);

            messageContainer = view.findViewById(R.id.messageContainer);
            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_image_profile_image);
            messageTextUser = (TextView) view.findViewById(R.id.message_text_user);

        }
    }

}
