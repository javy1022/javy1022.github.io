package com.example.hw9;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;


import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hw9.ui.main.MainActivity.adapters.MainSectionsPagerAdapter;
import com.example.hw9.databinding.ActivityMainBinding;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayoutMediator;


public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_ID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Request location permission
        OS_request_loc_permission();

        com.example.hw9.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MainSectionsPagerAdapter mainSectionsPagerAdapter = new MainSectionsPagerAdapter(this, getSupportFragmentManager(), getLifecycle());

        ViewPager2 viewPager = binding.viewPager;
        viewPager.setAdapter(mainSectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabs, viewPager, (tab, position) -> tab.setText(mainSectionsPagerAdapter.getPageTitle(position)));
        tabLayoutMediator.attach();
    }




    private void OS_request_loc_permission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_ID);
        }
    }


}