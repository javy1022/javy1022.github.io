package com.example.hw9;

import android.os.Bundle;


import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hw9.ui.main.SectionsPagerAdapter;
import com.example.hw9.databinding.ActivityMainBinding;

import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayoutMediator;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        com.example.hw9.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), getLifecycle());

        ViewPager2 viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabs, viewPager, (tab, position) -> tab.setText(sectionsPagerAdapter.getPageTitle(position)));
        tabLayoutMediator.attach();
    }
}