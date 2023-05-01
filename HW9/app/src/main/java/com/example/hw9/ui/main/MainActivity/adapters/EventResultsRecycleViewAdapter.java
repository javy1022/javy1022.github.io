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

public class EventResultsRecycleViewAdapter extends RecyclerView.Adapter<EventResultsRecycleViewAdapter.ViewHolder> {

    private final ArrayList<ArrayList<String>> event_search_results;
    private final boolean is_fav_tab;

    private HeartIconOnClickListener heart_icon_onClick_listener;

    public EventResultsRecycleViewAdapter(ArrayList<ArrayList<String>> event_search_results, boolean is_fav_tab) {
        this.event_search_results = event_search_results;
        this.is_fav_tab = is_fav_tab;
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
        shared.set_recycleViews_imgView(holder, img_url, holder.img);

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
        heart_icon_onClick(holder.itemView.getContext(), holder.heart_icon, event_id, is_fav_tab, holder, event_search_result);


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

        private final ImageView heart_icon;

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

        public void update_heart_icon_state(Context context, String event_id) {
            String key = "favorite_id_" + event_id;
            SharedPreferences shared_preferences = context.getSharedPreferences("favorite_preferences", Context.MODE_PRIVATE);
            boolean fav_state = shared_preferences.getBoolean(key, false);

            if (fav_state) {
                heart_icon.setImageResource(R.drawable.heart_filled);
            } else {
                heart_icon.setImageResource(R.drawable.heart_outline);
            }
        }
    }

    private void heart_icon_onClick(Context context, ImageView heart_icon, String event_id, boolean is_fav_tab, ViewHolder holder, ArrayList<String> event_data) {
        SharedPreferences shared_preferences = context.getSharedPreferences("favorite_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared_preferences.edit();
        String key = "favorite_id_" + event_id;
        final boolean[] fav_toggle = {shared_preferences.getBoolean(key, false)};

        update_heart_icon_UI(heart_icon, fav_toggle[0]);
        Gson gson = new Gson();

        heart_icon.setOnClickListener(view -> {
            ImageView heart_icon_iv = (ImageView) view;
            ArrayList<String> fav_events_ids;

            fav_toggle[0] = !fav_toggle[0];
            update_heart_icon_UI(heart_icon_iv, fav_toggle[0]);
            editor.putBoolean(key, fav_toggle[0]);
            String desired_data = shared_preferences.getString("favorite_event_ids", null);

            if (desired_data != null) {
                fav_events_ids = gson.fromJson(desired_data, new TypeToken<ArrayList<String>>() {
                }.getType());
            } else {
                fav_events_ids = new ArrayList<>();
            }

            if (fav_toggle[0]) {
                fav_events_ids.add(event_id);
                editor.putString("event_data_" + event_id, gson.toJson(event_data));
            } else {
                fav_events_ids.remove(event_id);
                editor.remove("event_data_" + event_id);
            }

            editor.putString("favorite_event_ids", gson.toJson(fav_events_ids));
            editor.apply();

            holder.update_heart_icon_state(context, event_id);

            if (heart_icon_onClick_listener != null) {
                heart_icon_onClick_listener.HeartIconOnClick(holder, event_data, is_fav_tab);
            }
        });
    }

    private void update_heart_icon_UI(ImageView heart_icon, boolean is_fav) {
        if (is_fav) {
            heart_icon.setImageResource(R.drawable.heart_filled);
            heart_icon.setTag("filled");
        } else {
            heart_icon.setImageResource(R.drawable.heart_outline);
            heart_icon.setTag("empty");
        }
    }

    private void event_result_card_onClick(CardView card, ArrayList<String> event_data) {
        card.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), EventDetailsActivity.class);
            intent.putStringArrayListExtra("event_data", event_data);
            view.getContext().startActivity(intent);
        });
    }

    public interface HeartIconOnClickListener {
        void HeartIconOnClick(ViewHolder holder, ArrayList<String> event_data, boolean is_fav_tab);
    }

    public void set_heart_icon_onClick_listener(HeartIconOnClickListener listener) {
        this.heart_icon_onClick_listener = listener;
    }
}
