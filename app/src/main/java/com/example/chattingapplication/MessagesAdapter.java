package com.example.chattingapplication;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>{

    public List<Messages> mList = new ArrayList<>();

    public MessagesAdapter(List<Messages> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText,messageTextUser;
        public CircleImageView profileImage;
        public ImageView messageImage;
        public RelativeLayout mainLayout;
        public ProgressBar mProgressbar;

        public MessageViewHolder(View view) {
            super(view);

//            mainLayout = (RelativeLayout) view.findViewById(R.id.image_single_layout);
//            messageText = (TextView) view.findViewById(R.id.message_text_layout);
//            profileImage = (CircleImageView) view.findViewById(R.id.message_image_profile_image);
//            messageImage = (ImageView) view.findViewById(R.id.image_sent_message);
//            messageTextUser = (TextView) view.findViewById(R.id.message_text_user);
//            mProgressbar = (ProgressBar) view.findViewById(R.id.message_image_progress);

        }
    }

}
