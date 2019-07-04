package com.example.a17_friends;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsAccessorAdapter   extends FragmentPagerAdapter
{
    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i)
        {
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return  chatsFragment;

            case 1:
                GroupsFragment groupsFragment = new GroupsFragment();
                return  groupsFragment;

            case 2:
                ContactsFragment contactsFragment = new ContactsFragment();
                return  contactsFragment;

            case 3:
               RequestsFragment requestsFragment = new RequestsFragment();
                return  requestsFragment;

                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return "聊天";

            case 1:
                return "群組";

            case 2:
                return "好友";

            case 3:
                return "好友邀請";

            default:
                return null;
        }
    }
}
