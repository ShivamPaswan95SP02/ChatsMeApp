package com.example.chatsmeapp.Adapter;

import com.example.chatsmeapp.Fragments.ChatsFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FragmentsAdapter extends FragmentPagerAdapter {

    public FragmentsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new ChatsFragment();

            /**   case 1: return new StatusFragment();
             case 2: return new CallsFragment();
              **/

             default:return new ChatsFragment();

        }

    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if(position==0)
        {
            title="CHATS";
        }
      /**  if(position==1)
        {
            title="STATUS";
        }

        if(position==2)
        {
            title="CALLS";
        }
       **/

        return title;
    }
}
