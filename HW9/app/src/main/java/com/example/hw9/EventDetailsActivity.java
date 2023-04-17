package com.example.hw9;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hw9.databinding.ActivityEventDetailsBinding;
import com.example.hw9.ui.main.EventDetailsActivity.adapter.EventDetailsSectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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
    }

    private void back_btn_onClick(){
        ImageButton backButton = findViewById(R.id.green_back_btn);
        backButton.setOnClickListener(v -> onBackPressed());
    }

}