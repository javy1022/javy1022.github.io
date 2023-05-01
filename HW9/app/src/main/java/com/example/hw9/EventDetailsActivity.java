package com.example.hw9;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hw9.databinding.ActivityEventDetailsBinding;
import com.example.hw9.ui.main.EventDetailsActivity.adapter.EventDetailsSectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class EventDetailsActivity extends AppCompatActivity {
    private TabLayout tab_layout;
    private String event_id;
    private SharedGeneralPurposeMethods shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.hw9.databinding.ActivityEventDetailsBinding binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get event data from EventResultsAdapter
        Intent intent = getIntent();
        ArrayList<String> event_data = intent.getStringArrayListExtra("event_data");
        event_id = event_data.get(6);

        tab_layout = findViewById(R.id.tabs);
        EventDetailsSectionsPagerAdapter sectionsPagerAdapter = new EventDetailsSectionsPagerAdapter(this, getSupportFragmentManager(), getLifecycle(), tab_layout, event_data);

        ViewPager2 viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabs, viewPager, (tab, position) -> tab.setText(sectionsPagerAdapter.getPageTitle(position)));
        tabLayoutMediator.attach();

        // set activity objects
        set_event_title(event_data);
        set_tabs_content(sectionsPagerAdapter);
        back_btn_onClick();

        shared = new SharedGeneralPurposeMethods();
        init_heart_icon(event_data);
    }

    private void back_btn_onClick() {
        ImageButton back_btn = findViewById(R.id.green_back_btn);
        back_btn.setOnClickListener(v -> onBackPressed());
    }

    private void set_event_title(ArrayList<String> event_data) {
        if (event_data != null) {
            TextView event_details_title = findViewById(R.id.event_details_title);
            String event_name = event_data.get(3);
            event_details_title.setText(event_name);
            event_details_title.setSelected(true);
        }
    }

    private void set_tabs_content(EventDetailsSectionsPagerAdapter sections_pager_adapter) {
        int[] tab_icons = {R.drawable.info_icon, R.drawable.artist_icon, R.drawable.venue_icon};
        tab_layout = findViewById(R.id.tabs);
        sections_pager_adapter.setTabIconsAndTitles(tab_icons);
    }

    private void heart_icon_toggle() {
        SharedPreferences shared_preferences = getSharedPreferences("favorite_preferences", Context.MODE_PRIVATE);
        boolean is_fav = shared_preferences.getBoolean("favorite_id_" + event_id, false);

        ImageView heart_icon = findViewById(R.id.sub_heart_icon);
        shared.update_heart_icon_UI(heart_icon, is_fav);
    }

    private void init_heart_icon(ArrayList<String> event_data) {
        heart_icon_toggle();

        ImageView heart_icon = findViewById(R.id.sub_heart_icon);
        heart_icon.setOnClickListener(view -> {
            SharedPreferences shared_preferences = getSharedPreferences("favorite_preferences", Context.MODE_PRIVATE);
            boolean is_fav = shared_preferences.getBoolean("favorite_id_" + event_id, false);
            shared_preferences.edit().putBoolean("favorite_id_" + event_id, !is_fav).apply();
            heart_icon_toggle();
            SharedGeneralPurposeMethods.updateFavoritesInSharedPreferences(this, !is_fav, event_id, event_data);

            String msg;
            if (!is_fav) {
                msg = event_data.get(3) + " added to favorites";
            } else {
                msg = event_data.get(3) + " removed from favorites";
            }
            shared.snack_bar_msg(view, view.getContext(), msg);
        });
    }
}