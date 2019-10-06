package com.example.chattingapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public SectionsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:

                ContactsFragment contactsFragment = new ContactsFragment();

                return contactsFragment;



            case 1:

                GroupsFragment groupsFragment = new GroupsFragment();

                return groupsFragment;



            default:

                return null;

        }

    }

    @Override
    public int getCount() {
        return 2;
    }
}
