package com.example.hw9.ui.main.EventDetailsActivity.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

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
import com.example.hw9.ui.main.EventDetailsActivity.tabs.VenueFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

public class EventDetailsSectionsPagerAdapter extends FragmentStateAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_3, R.string.tab_text_4, R.string.tab_text_5};
    private final Context mContext;
    private final TabLayout mTabLayout;

    private final ArrayList<String> mEventData;

    public EventDetailsSectionsPagerAdapter(Context context, FragmentManager fragmentManager, Lifecycle lifecycle, TabLayout tabLayout, ArrayList<String> eventData) {
        super(fragmentManager, lifecycle);
        mContext = context;
        mTabLayout = tabLayout;
        mEventData = eventData;
    }

    @Override
    @NonNull
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = DetailsFragment.newInstance("123", "456");
                loadEventData(fragment);
                break;
            case 1:
                fragment = ArtistsFragment.newInstance("123", "456");
                break;
            case 2:
                fragment = VenueFragment.newInstance("123", "456");
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
        return 3;
    }

    private void loadEventData(Fragment fragment){
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("event_data", mEventData);
        fragment.setArguments(bundle);
    }


    public void setTabIconsAndTitles(int[] tab_icons) {
        // Set the icons for each tab
        Objects.requireNonNull(mTabLayout.getTabAt(0)).setIcon(tab_icons[0]);
        Objects.requireNonNull(mTabLayout.getTabAt(1)).setIcon(tab_icons[1]);
        Objects.requireNonNull(mTabLayout.getTabAt(2)).setIcon(tab_icons[2]);

        // Set the titles for each tab
        Objects.requireNonNull(mTabLayout.getTabAt(0)).setText(getPageTitle(0));
        Objects.requireNonNull(mTabLayout.getTabAt(1)).setText(getPageTitle(1));
        Objects.requireNonNull(mTabLayout.getTabAt(2)).setText(getPageTitle(2));

        // Create a color state list for tab image icons selected/unselected color
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_selected},
                new int[]{-android.R.attr.state_selected}
        };
        int[] colors = new int[]{
                Color.parseColor( "#50C31B"),
                Color.WHITE
        };
        ColorStateList colorStateList = new ColorStateList(states, colors);
        mTabLayout.setTabIconTint(colorStateList);
    }
}
