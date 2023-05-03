package com.example.hw9.ui.main.MainActivity.tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hw9.R;
import com.example.hw9.RecycleViewDecorator;
import com.example.hw9.SharedGeneralPurposeMethods;
import com.example.hw9.ui.main.MainActivity.adapters.EventResultsRecycleViewAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {
    private EventResultsRecycleViewAdapter fav_adapter;

    private RecyclerView fav_recycle_view;

    private CardView fav_empty_cv;

    public FavoriteFragment() {
        // required constructor
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

        fav_recycle_view = view.findViewById(R.id.fav_recycle_view);
        fav_recycle_view.setLayoutManager(new LinearLayoutManager(getContext()));
        fav_recycle_view.addItemDecoration(new RecycleViewDecorator(50));

        fav_empty_cv = view.findViewById(R.id.fav_empty);
    }

    @Override
    public void onResume() {
        super.onResume();

        ProgressBar fav_pb = requireView().findViewById(R.id.fav_progress_bar);
        fav_pb.setVisibility(View.VISIBLE);

        Gson gson = new Gson();
        ArrayList<ArrayList<String>> fav_events = SharedGeneralPurposeMethods.get_sharedPreferences_fav_events(requireContext());
        fav_adapter = new EventResultsRecycleViewAdapter(fav_events, true);

        fav_recycle_view.setAdapter(fav_adapter);

        if (fav_events.isEmpty()) fav_empty_cv.setVisibility(View.VISIBLE);
        else fav_empty_cv.setVisibility(View.GONE);

        fav_adapter.set_heart_icon_onClick_listener((holder, event_search_result, is_fav_tab) -> {
            String event_id = event_search_result.get(6);
            String key = "favorite_id_" + event_id;

            // Get the heart icon state from SharedPreferences
            SharedPreferences shared_preferences = requireContext().getSharedPreferences("favorite_preferences", Context.MODE_PRIVATE);
            boolean fav_state_toggle = shared_preferences.getBoolean(key, false);

            // Get the current list of favorite event ids
            String fav_events_ids_json_obj = shared_preferences.getString("favorite_event_ids", null);
            ArrayList<String> fav_event_ids = fav_events_ids_json_obj != null ? gson.fromJson(fav_events_ids_json_obj, new TypeToken<ArrayList<String>>() {
            }.getType()) : new ArrayList<>();

            if (fav_state_toggle) fav_event_ids.add(event_id);
            else fav_event_ids.remove(event_id);

            // Save the updated list of favorite event ids
            SharedPreferences.Editor editor = shared_preferences.edit();
            editor.putString("favorite_event_ids", gson.toJson(fav_event_ids));
            editor.apply();

            // If this is the favorite tab, remove the item from the list and notify the adapter
            if (is_fav_tab && !fav_state_toggle) {
                int position = holder.getAdapterPosition();
                fav_events.remove(position);
                fav_adapter.notifyItemRemoved(position);

                if (fav_events.isEmpty()) fav_empty_cv.setVisibility(View.VISIBLE);
                else fav_empty_cv.setVisibility(View.GONE);
            }
        });
        fav_pb.setVisibility(View.GONE);
    }
}