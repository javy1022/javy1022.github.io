package com.example.hw9;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hw9.databinding.ActivityEventDetailsBinding;
import com.example.hw9.ui.main.EventDetailsActivity.adapter.EventDetailsSectionsPagerAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class EventDetailsActivity extends AppCompatActivity {
    private TabLayout tab_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.hw9.databinding.ActivityEventDetailsBinding binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get event data from EventResultsAdapter
        Intent intent = getIntent();
        ArrayList<String> event_data = intent.getStringArrayListExtra("event_data");

        tab_layout = findViewById(R.id.tabs);
        EventDetailsSectionsPagerAdapter sectionsPagerAdapter = new EventDetailsSectionsPagerAdapter(this, getSupportFragmentManager(), getLifecycle(), tab_layout,  event_data);

        ViewPager2 viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabs, viewPager, (tab, position) -> tab.setText(sectionsPagerAdapter.getPageTitle(position)));
        tabLayoutMediator.attach();

        // set activity objects
        set_event_title(event_data);
        set_tabs_content(sectionsPagerAdapter);
        back_btn_onClick();

    }

    private void back_btn_onClick(){
        ImageButton backButton = findViewById(R.id.green_back_btn);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void set_event_title(ArrayList<String> event_data){
        if (event_data != null) {
            TextView event_details_title = findViewById(R.id.event_details_title);
            String event_name = event_data.get(3);
            event_details_title.setText(event_name);
            event_details_title.setSelected(true);
        }
    }

    private void set_tabs_content(EventDetailsSectionsPagerAdapter sectionsPagerAdapter ){
        int[] tab_icons = {R.drawable.info_icon, R.drawable.artist_icon,R.drawable.venue_icon};
        tab_layout = findViewById(R.id.tabs);
        sectionsPagerAdapter.setTabIconsAndTitles(tab_icons);
    }

}