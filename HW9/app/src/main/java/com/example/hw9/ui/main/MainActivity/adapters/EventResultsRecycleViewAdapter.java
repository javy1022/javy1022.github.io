package com.example.hw9.ui.main.MainActivity.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hw9.EventDetailsActivity;
import com.example.hw9.R;
import com.example.hw9.SharedGeneralPurposeMethods;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventResultsRecycleViewAdapter extends RecyclerView.Adapter<EventResultsRecycleViewAdapter.ViewHolder> {

    private final ArrayList<ArrayList<String>> event_search_results;
    private final boolean isFavoriteTab;

    public EventResultsRecycleViewAdapter(ArrayList<ArrayList<String>> event_search_results, boolean isFavoriteTab) {
        this.event_search_results = event_search_results;
        this.isFavoriteTab = isFavoriteTab;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_search_recycle_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ArrayList<String> event_search_result = event_search_results.get(position);
        SharedGeneralPurposeMethods shared = new SharedGeneralPurposeMethods();

        // date
        String date = event_search_result.get(0);
        holder.date.setText(date);

        // time
        String time = event_search_result.get(1);
        holder.time.setText(time);
        // image
        String img_url = event_search_result.get(2);
        shared.set_recycleViews_imgView(holder,img_url,holder.img);

        // event name
        String name = event_search_result.get(3);
        holder.name.setText(name);

        // category
        String category = event_search_result.get(4);
        holder.category.setText(category);

        // venue
        String venue = event_search_result.get(5);
        holder.venue.setText(venue);

        // Navigate to EventDetails Activity
        CardView event_result_card = holder.itemView.findViewById(R.id.event_result_card);
        event_result_card_onClick(event_result_card, event_search_result);

        // favorite icon
        String event_id = event_search_result.get(6);
        heart_icon_onClick(holder.itemView.getContext(), holder.heart_icon, event_id, isFavoriteTab, holder, event_search_result);




        // Enable selected to make marquee effect works on TextViews
        SharedGeneralPurposeMethods.textViews_enable_selected(holder.name, holder.venue, holder.category);

    }

    @Override
    public int getItemCount() {
         return event_search_results.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView date;

        private final TextView time;
        private final ImageView img;
        private final TextView name;

        private final TextView category;
        private final TextView venue;

        private final ImageView  heart_icon;

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.event_date);
            time = itemView.findViewById(R.id.event_time);
            img = itemView.findViewById(R.id.event_img);
            name = itemView.findViewById(R.id.event_name);
            category = itemView.findViewById(R.id.event_category);
            venue = itemView.findViewById(R.id.event_venue);
            // Fill/Empty heart icon
            heart_icon = itemView.findViewById(R.id.heart_icon);


        }
    }



    private void heart_icon_onClick(Context context, ImageView heart_icon, String event_id, boolean isFavoriteTab , ViewHolder holder, ArrayList<String> eventData) {
        SharedPreferences shared_preferences = context.getSharedPreferences("favorite_preferences", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared_preferences.edit();
        // Create a key for the favorite state based on the unique event ID
        String key = "favorite_id_" + event_id;

        // Set the initial state of the heart icon based on the saved favorite state
        AtomicBoolean is_fav = new AtomicBoolean(shared_preferences.getBoolean(key, false));
        updateHeartIconUI(heart_icon, is_fav.get());

        // Update the favorite state and save it when the heart icon is clicked
        heart_icon.setOnClickListener(view -> {
            ImageView imageView = (ImageView) view;
            boolean fav_state_toggle = !is_fav.get();
            updateHeartIconUI(imageView, fav_state_toggle);

            editor.putBoolean(key, fav_state_toggle);

            // Save event data as a serialized JSON string
            if (fav_state_toggle) {
                Gson gson = new Gson();
                String eventJson = gson.toJson(eventData);
                editor.putString("event_data_" + event_id, eventJson);
            } else {
                editor.remove("event_data_" + event_id);
            }

            editor.apply();

            // Update the isFavorite variable for the next click
            is_fav.set(fav_state_toggle);

            // Call the listener
            if (onHeartIconClickListener != null) {
                onHeartIconClickListener.onHeartIconClick(holder, eventData, isFavoriteTab);
            }


        });
    }



    private void updateHeartIconUI(ImageView heart_icon, boolean is_fav) {
        if (is_fav) {
            heart_icon.setImageResource(R.drawable.heart_filled);
            heart_icon.setTag("filled");
        } else {
            heart_icon.setImageResource(R.drawable.heart_outline);
            heart_icon.setTag("empty");
        }
    }

    private static void event_result_card_onClick(CardView card, ArrayList<String> event_data){
        card.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), EventDetailsActivity.class);
            // data
            intent.putStringArrayListExtra("event_data", event_data);
            view.getContext().startActivity(intent);
        });
    }


    public interface OnHeartIconClickListener {
        void onHeartIconClick(ViewHolder holder, ArrayList<String> eventData, boolean isFavoriteTab);
    }

    private OnHeartIconClickListener onHeartIconClickListener;

    public void setOnHeartIconClickListener(OnHeartIconClickListener listener) {
        this.onHeartIconClickListener = listener;
    }

}
