package com.example.chattingapplication;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private RecyclerView mAllUsersView;
    private ContactsAdapter mAdapter;

    private List<Users> mList = new ArrayList<>();


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);

        LinearLayoutManager mLinearLayout = new LinearLayoutManager(getActivity().getApplicationContext());

        mAllUsersView = v.findViewById(R.id.allContactsList);

        mAdapter = new ContactsAdapter(mList);

        mAllUsersView.setHasFixedSize(true);
        mAllUsersView.setLayoutManager(mLinearLayout);
        mAllUsersView.setAdapter(mAdapter);

        mList.clear();
        loadContacts();

        return v;
    }

    private void loadContacts() {
        final DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("Users");


        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot usersSnap : dataSnapshot.getChildren()) {

//                    String name = usersSnap.child("name").getValue().toString();
//                    String UID = usersSnap.child("UID").getValue().toString();
//                    String image = usersSnap.child("image").getValue().toString();
//                    String imageComp = usersSnap.child("imageComp").getValue().toString();
//
//                    Users u = new Users(UID, name, image, imageComp);
//                    mList.add(u);
                    Users u = usersSnap.getValue(Users.class);
                    mList.add(u);
                    mAdapter.notifyDataSetChanged();
//                    for(DataSnapshot detailSnap : usersSnap.getChildren()) {
//
//                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
