package com.example.hw9.ui.main.MainActivity.tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hw9.R;
import com.example.hw9.RecycleViewDecorator;
import com.example.hw9.ui.main.MainActivity.adapters.EventResultsRecycleViewAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {
    private EventResultsRecycleViewAdapter fav_adapter;

    private RecyclerView fav_recycle_view;


    public FavoriteFragment() {
        // Required empty public constructor
    }
    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Initialize the RecyclerView
        fav_recycle_view = view.findViewById(R.id.fav_recycle_view);

        // Set a layout manager for the RecyclerView
        fav_recycle_view.setLayoutManager(new LinearLayoutManager(getContext()));
        fav_recycle_view.addItemDecoration(new RecycleViewDecorator(50));


    }

    private ArrayList<ArrayList<String>> getFavoriteEvents() {
        ArrayList<ArrayList<String>> favoriteEvents = new ArrayList<>();

        SharedPreferences shared_preferences = requireContext().getSharedPreferences("favorite_preferences", Context.MODE_PRIVATE);

        Gson gson = new Gson();

        // Get the list of favorite event IDs
        String favoriteEventIdsJson = shared_preferences.getString("favorite_event_ids", null);
        ArrayList<String> favoriteEventIds = favoriteEventIdsJson != null ? gson.fromJson(favoriteEventIdsJson, new TypeToken<ArrayList<String>>() {}.getType()) : new ArrayList<>();

        // Loop through the list of favorite event IDs to get the favorite events
        for (String eventId : favoriteEventIds) {
            String eventJson = shared_preferences.getString("event_data_" + eventId, null);
            if (eventJson != null) {
                ArrayList<String> eventDetails = gson.fromJson(eventJson, new TypeToken<ArrayList<String>>() {}.getType());
                favoriteEvents.add(eventDetails);
            }
        }

        return favoriteEvents;
    }


    @Override
    public void onResume() {
        super.onResume();

        // Create a Gson object
        Gson gson = new Gson();

        // Create a new ArrayList containing favorite events from the SharedPreferences
        ArrayList<ArrayList<String>> favoriteEvents = getFavoriteEvents();

        // Create a new instance of EventResultsRecycleViewAdapter with the list of favorite events
        fav_adapter = new EventResultsRecycleViewAdapter(favoriteEvents, true);

        // Set the adapter to the RecyclerView in the favorite tab
        fav_recycle_view.setAdapter(fav_adapter);

        fav_adapter.set_heart_icon_onClick_listener((holder, event_search_result, isFavoriteTab) -> {
            // Your current heart_icon_onClick code here

            // Get the event_id
            String event_id = event_search_result.get(6);

            // Create a key for the favorite state based on the unique event ID
            String key = "favorite_id_" + event_id;

            // Get the favorite state from SharedPreferences
            SharedPreferences shared_preferences = requireContext().getSharedPreferences("favorite_preferences", Context.MODE_PRIVATE);
            boolean fav_state_toggle = shared_preferences.getBoolean(key, false);

            // Get the current list of favorite event IDs
            String favoriteEventIdsJson = shared_preferences.getString("favorite_event_ids", null);
            ArrayList<String> favoriteEventIds = favoriteEventIdsJson != null ? gson.fromJson(favoriteEventIdsJson, new TypeToken<ArrayList<String>>() {}.getType()) : new ArrayList<>();

            if (fav_state_toggle) {
                // If the event is now a favorite, add the event ID to the list
                favoriteEventIds.add(event_id);
            } else {
                // If the event is no longer a favorite, remove the event ID from the list
                favoriteEventIds.remove(event_id);
            }

            // Save the updated list of favorite event IDs
            SharedPreferences.Editor editor = shared_preferences.edit();
            editor.putString("favorite_event_ids", gson.toJson(favoriteEventIds));
            editor.apply();

            // If this is the favorite tab, remove the item from the list and notify the adapter
            if (isFavoriteTab && !fav_state_toggle) {
                int position = holder.getAdapterPosition();
                favoriteEvents.remove(position);
                fav_adapter.notifyItemRemoved(position);
            }
        });


    }




}