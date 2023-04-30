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
import com.google.gson.reflect.TypeToken;

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
            heart_icon = itemView.findViewById(R.id.heart_icon);
        }

        public void updateHeartIconState(Context context, String event_id) {
            String key = "favorite_id_" + event_id;
            SharedPreferences shared_preferences = context.getSharedPreferences("favorite_preferences", Context.MODE_PRIVATE);
            boolean fav_state = shared_preferences.getBoolean(key, false);

            if (fav_state) {
                heart_icon.setImageResource(R.drawable.heart_filled); // Use the same resource as in updateHeartIconUI()
            } else {
                heart_icon.setImageResource(R.drawable.heart_outline); // Use the same resource as in updateHeartIconUI()
            }
        }
    }

    private void heart_icon_onClick(Context context, ImageView heart_icon, String event_id, boolean is_fav_tab, ViewHolder holder, ArrayList<String> event_data) {
        SharedPreferences shared_preferences = context.getSharedPreferences("favorite_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared_preferences.edit();
        String key = "favorite_id_" + event_id;

        final boolean[] is_fav = {shared_preferences.getBoolean(key, false)};
        updateHeartIconUI(heart_icon, is_fav[0]);

        Gson gson = new Gson();

        heart_icon.setOnClickListener(view -> {
            ImageView imageView = (ImageView) view;
            is_fav[0] = !is_fav[0];
            updateHeartIconUI(imageView, is_fav[0]);

            editor.putBoolean(key, is_fav[0]);

            String favoriteEventIdsJson = shared_preferences.getString("favorite_event_ids", null);
            ArrayList<String> favoriteEventIds = favoriteEventIdsJson != null ? gson.fromJson(favoriteEventIdsJson, new TypeToken<ArrayList<String>>() {}.getType()) : new ArrayList<>();

            if (is_fav[0]) {
                favoriteEventIds.add(event_id);
                editor.putString("event_data_" + event_id, gson.toJson(event_data));
            } else {
                favoriteEventIds.remove(event_id);
                editor.remove("event_data_" + event_id);
            }

            editor.putString("favorite_event_ids", gson.toJson(favoriteEventIds));
            editor.apply();

            if (onHeartIconClickListener != null) {
                onHeartIconClickListener.onHeartIconClick(holder, event_data, is_fav_tab);
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



    public ArrayList<ArrayList<String>> getEventSearchResults() {
        return event_search_results;
    }








}
