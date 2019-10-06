package com.example.chattingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private EditText searchUser;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ImageView contactsIcon, groupsIcon;
    private TextView contactsText, groupsText;

    private RelativeLayout contactsTab, groupsTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mViewPager = findViewById(R.id.startPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        contactsTab = findViewById(R.id.contactsTab);
        groupsTab = findViewById(R.id.groupsTab);

        searchUser = findViewById(R.id.searchContacts);
        searchUser.setFocusable(false);

//        Intent i = new Intent(StartActivity.this, ChatActivity.class);
//        startActivity(i);

        searchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchUser.setFocusableInTouchMode(true);
            }
        });

        contactsIcon = findViewById(R.id.contactsIcon);
        groupsIcon = findViewById(R.id.groupsIcon);
        contactsText = findViewById(R.id.startContactText);
        groupsText = findViewById(R.id.startGroupText);

        contactsIcon.setBackgroundResource(R.drawable.profile_icon_selected);
        contactsText.setTextColor(getResources().getColor(R.color.darkText));
        groupsIcon.setBackgroundResource(R.drawable.group_deselect);
        groupsText.setTextColor(getResources().getColor(R.color.lightText));

        groupsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(1);
            }
        });

        contactsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0) {
                    contactsIcon.setBackgroundResource(R.drawable.profile_icon_selected);
                    contactsText.setTextColor(getResources().getColor(R.color.darkText));
                    groupsIcon.setBackgroundResource(R.drawable.group_deselect);
                    groupsText.setTextColor(getResources().getColor(R.color.lightText));
                } else {
                    contactsIcon.setBackgroundResource(R.drawable.profile_icon_deselect);
                    contactsText.setTextColor(getResources().getColor(R.color.lightText));
                    groupsIcon.setBackgroundResource(R.drawable.group_selected);
                    groupsText.setTextColor(getResources().getColor(R.color.darkText));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }
}
