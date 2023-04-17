package com.example.hw9.ui.main.EventDetailsActivity.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.hw9.R;
import com.example.hw9.ui.main.EventDetailsActivity.tabs.ArtistsFragment;
import com.example.hw9.ui.main.EventDetailsActivity.tabs.DetailsFragment;

public class EventDetailsSectionsPagerAdapter extends FragmentStateAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_3, R.string.tab_text_4};
    private final Context mContext;

    public EventDetailsSectionsPagerAdapter(Context context, FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        mContext = context;
    }

    @Override
    @NonNull
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = DetailsFragment.newInstance("123", "456");
                break;
            case 1:
                fragment = ArtistsFragment.newInstance("123", "456");
                break;
            default:
                throw new IllegalStateException("Exception: Invalid Position createFragment(int position)" + position);
        }
        return fragment;
    }

    @Nullable
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
