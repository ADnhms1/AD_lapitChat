package com.example.ad_lapitchat;

import android.view.View;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch(position)
        {
            case 0 :
                ADrequestFragment requestFragment = new ADrequestFragment();
                return requestFragment;
            case 1 :
                ADchatFragment chatFragment = new ADchatFragment();
                return chatFragment;
            case 2 :
                ADfriendsFragment friendsFragment = new ADfriendsFragment();
                return friendsFragment;
            default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position)
    {
        switch(position){
            case 0 :
                return "REQUESTS";
            case 1 :
                return "CHATS";
            case 2 :
                return "FRIENDS";
            default :
                return null;
        }
    }
}
