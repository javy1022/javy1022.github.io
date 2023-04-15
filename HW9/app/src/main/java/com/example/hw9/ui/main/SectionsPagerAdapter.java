package com.example.hw9.ui.main;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.hw9.R;import androidx.annotation.NonNull;

public class SectionsPagerAdapter extends FragmentStateAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        mContext = context;
    }

    @Override
    @NonNull
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = SearchFragment.newInstance("123", "456");
                break;
            case 1:
                fragment = FavoriteFragment.newInstance("123", "456");
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
