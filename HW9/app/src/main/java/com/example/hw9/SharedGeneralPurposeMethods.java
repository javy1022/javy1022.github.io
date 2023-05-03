package com.example.hw9;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/* Class to share common functions */

public class SharedGeneralPurposeMethods {

    // helper function to extract desired data array given a sequences of keys
    public JsonArray general_json_arr_navigator(JsonObject json_obj, String... keys) {
        JsonElement json_element = json_obj;
        for (String key : keys) {
            if (json_element == null) return null;
            json_element = json_element.getAsJsonObject().get(key);
        }
        if (json_element == null) return null;
        return json_element.getAsJsonArray();
    }

    // extract desired data given a sequences of keys
    public String general_json_navigator(JsonObject json_obj, String... keys) {
        JsonElement json_element = json_obj;
        for (String key : keys) {
            if (json_element == null) return "";
            json_element = json_element.getAsJsonObject().get(key);
        }
        if (json_element == null) return "";
        String desired_data = json_element.getAsString().trim();
        if ("undefined".equalsIgnoreCase(desired_data)) {
            return "";
        }
        return desired_data;
    }

    // generic recycle view layout constructor
    public <T extends RecyclerView.Adapter<?>> void generate_linearLayout_recycleView(Context context, RecyclerView recycle_view, T recycle_view_adapter) {
        // Init
        RecyclerView.LayoutManager event_search_recycleView_layoutManager = new LinearLayoutManager(context);
        recycle_view.setLayoutManager(event_search_recycleView_layoutManager);
        // Populate recycle view
        recycle_view.setAdapter(recycle_view_adapter);
    }

    // generic helper function to set image with Glide
    public <T extends RecyclerView.ViewHolder> void set_recycle_view_iv(T holder, String img_url, ImageView img) {
        MultiTransformation<Bitmap> transformations = new MultiTransformation<>(
                new CenterCrop(),
                new RoundedCorners(45)
        );

        RequestOptions request_options = new RequestOptions()
                .override(310, 310)
                .transform(transformations);

        Glide.with(holder.itemView.getContext())
                .load(img_url)
                .apply(request_options)
                .into(img);
    }

    public static void textViews_enable_selected(TextView... text_views) {
        for (TextView tv : text_views) tv.setSelected(true);
    }

    public void snack_bar_msg(View view, Context context, String msg) {
        Snackbar snackBar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        snackBar.getView().setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray_snack_bar)));
        TextView snackBar_text = snackBar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        snackBar_text.setTextColor(ContextCompat.getColor(context, R.color.black));
        snackBar.show();
    }

    // Reference source: https://www.geeksforgeeks.org/how-to-save-arraylist-to-sharedpreferences-in-android/
    public static ArrayList<ArrayList<String>> get_sharedPreferences_fav_events(Context context) {
        ArrayList<ArrayList<String>> fav_events = new ArrayList<>();
        SharedPreferences shared_preferences = context.getSharedPreferences("favorite_preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        // Get the list of favorite events ids
        String fav_events_ids_json = shared_preferences.getString("favorite_event_ids", null);
        ArrayList<String> fav_event_ids = fav_events_ids_json != null ? gson.fromJson(fav_events_ids_json, new TypeToken<ArrayList<String>>() {
        }.getType()) : new ArrayList<>();

        // Get the favorite events
        for (String event_id : fav_event_ids) {
            String event_json = shared_preferences.getString("event_data_" + event_id, null);
            if (event_json != null) {
                ArrayList<String> event_data = gson.fromJson(event_json, new TypeToken<ArrayList<String>>() {
                }.getType());
                fav_events.add(event_data);
            }
        }
        return fav_events;
    }

    public static void update_sharedPreferences_fav_events(Context context, boolean is_fav, String event_id, ArrayList<String> event_data) {
        SharedPreferences shared_preferences = context.getSharedPreferences("favorite_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared_preferences.edit();
        Gson gson = new Gson();

        // Get the current list of favorite events ids
        String fav_events_ids_json = shared_preferences.getString("favorite_event_ids", null);
        ArrayList<String> fav_events_ids = fav_events_ids_json != null ? gson.fromJson(fav_events_ids_json, new TypeToken<ArrayList<String>>() {
        }.getType()) : new ArrayList<>();

        if (is_fav) {
            fav_events_ids.add(event_id);
            editor.putString("event_data_" + event_id, gson.toJson(event_data));
        } else {
            fav_events_ids.remove(event_id);
            editor.remove("event_data_" + event_id);
        }

        // Save the updated list of favorite events ids
        editor.putString("favorite_event_ids", gson.toJson(fav_events_ids));
        editor.apply();
    }

    public void update_heart_icon_UI(ImageView heart_icon, boolean is_fav) {
        if (is_fav) {
            heart_icon.setImageResource(R.drawable.heart_filled);
            heart_icon.setTag("filled");
        } else {
            heart_icon.setImageResource(R.drawable.heart_outline);
            heart_icon.setTag("empty");
        }
    }

}
