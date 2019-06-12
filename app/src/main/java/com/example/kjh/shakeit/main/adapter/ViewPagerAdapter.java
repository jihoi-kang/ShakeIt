package com.example.kjh.shakeit.main.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.kjh.shakeit.main.chat.TabChatRoomListFragment;
import com.example.kjh.shakeit.main.friend.TabFriendListFragment;
import com.example.kjh.shakeit.main.more.TabMoreFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    /** 탭 변경시 필요한 Fragment 선언 */
    private final Fragment[] frags = new Fragment[]{
            TabFriendListFragment.getInstance(),
            TabChatRoomListFragment.getInstance(),
            TabMoreFragment.getInstance()
    };

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return frags[position];
    }

    @Override
    public int getCount() {
        return frags.length;
    }

}
