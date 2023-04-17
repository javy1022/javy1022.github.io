package com.example.hw9;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hw9.databinding.ActivityEventDetailsBinding;
import com.example.hw9.ui.main.EventDetailsActivity.adapter.EventDetailsSectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.hw9.databinding.ActivityEventDetailsBinding binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EventDetailsSectionsPagerAdapter sectionsPagerAdapter = new EventDetailsSectionsPagerAdapter(this, getSupportFragmentManager(), getLifecycle());

        ViewPager2 viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabs, viewPager, (tab, position) -> tab.setText(sectionsPagerAdapter.getPageTitle(position)));
        tabLayoutMediator.attach();

        // navigate back to previous activity
        back_btn_onClick();

        Intent intent = getIntent();
        ArrayList<String> eventData = intent.getStringArrayListExtra("event_data");
        if (eventData != null) {
            TextView targetTextView = findViewById(R.id.event_details_title);
            String eventName = eventData.get(3); // assuming the event name is at index 3
            targetTextView.setText(eventName);
        }

    }

    private void back_btn_onClick(){
        ImageButton backButton = findViewById(R.id.green_back_btn);
        backButton.setOnClickListener(v -> onBackPressed());
    }

}