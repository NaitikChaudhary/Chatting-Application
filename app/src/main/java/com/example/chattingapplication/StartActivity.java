package com.example.chattingapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class StartActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mToolbar;

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

        mToolbar = findViewById(R.id.start_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.app_custom_toolbar, null);

        actionBar.setCustomView(action_bar_view);

        mViewPager = findViewById(R.id.startPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        contactsTab = findViewById(R.id.contactsTab);
        groupsTab = findViewById(R.id.groupsTab);

        searchUser = findViewById(R.id.searchContacts);
        searchUser.setFocusable(false);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_button) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if(currentUser != null) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
            }
            FirebaseAuth.getInstance().signOut();

            Intent i = new Intent(StartActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);

        } else if(item.getItemId() == R.id.main_settings_button) {
            Intent i = new Intent(StartActivity.this, UserInfoActivity.class);
            startActivity(i);
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = currentUser.getUid();

        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid).child("online").setValue("true");
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

}
