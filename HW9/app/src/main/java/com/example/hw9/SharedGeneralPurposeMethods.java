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

    // helper function to  extract desired data array given a sequences of keys
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

    public <T extends RecyclerView.Adapter<?>> void generate_linearLayout_recycleView(Context context, RecyclerView recycle_view, T recycle_view_adapter) {
        // Init
        RecyclerView.LayoutManager event_search_recycleView_layoutManager = new LinearLayoutManager(context);
        recycle_view.setLayoutManager(event_search_recycleView_layoutManager);
        // Populate recycle view
        recycle_view.setAdapter(recycle_view_adapter);
    }


    public <T extends RecyclerView.ViewHolder> void set_recycleViews_imgView(T holder, String img_url, ImageView img){
        MultiTransformation<Bitmap> transformations = new MultiTransformation<>(
                new CenterCrop(),
                new RoundedCorners(45)
        );  //original 325 325
        RequestOptions request_options = new RequestOptions()
                .override(310, 310)
                .transform(transformations);

        Glide.with(holder.itemView.getContext())
                .load(img_url)
                .apply(request_options)
                .into(img);
    }

    public static void textViews_enable_selected(TextView... textViews){
        for (TextView tv : textViews) {
            tv.setSelected(true);
        }
    }

    public void snack_bar_msg(View view, Context context, String msg ){
        Snackbar snackBar = Snackbar.make(view,  msg, Snackbar.LENGTH_SHORT);
        snackBar.getView().setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray_snack_bar)));
        TextView snackBar_text = snackBar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        snackBar_text.setTextColor(ContextCompat.getColor(context, R.color.black));
        snackBar.show();
    }


    public static ArrayList<ArrayList<String>> getFavoriteEvents(Context context) {
        ArrayList<ArrayList<String>> favoriteEvents = new ArrayList<>();

        SharedPreferences sharedPreferences = context.getSharedPreferences("favorite_preferences", Context.MODE_PRIVATE);

        Gson gson = new Gson();

        // Get the list of favorite event IDs
        String favoriteEventIdsJson = sharedPreferences.getString("favorite_event_ids", null);
        ArrayList<String> favoriteEventIds = favoriteEventIdsJson != null ? gson.fromJson(favoriteEventIdsJson, new TypeToken<ArrayList<String>>() {}.getType()) : new ArrayList<>();

        // Loop through the list of favorite event IDs to get the favorite events
        for (String eventId : favoriteEventIds) {
            String eventJson = sharedPreferences.getString("event_data_" + eventId, null);
            if (eventJson != null) {
                ArrayList<String> eventDetails = gson.fromJson(eventJson, new TypeToken<ArrayList<String>>() {}.getType());
                favoriteEvents.add(eventDetails);
            }
        }

        return favoriteEvents;
    }

    public static void updateFavoritesInSharedPreferences(Context context, boolean isFav, String eventId, ArrayList<String> eventData) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("favorite_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        // Get the current list of favorite event IDs
        String favoriteEventIdsJson = sharedPreferences.getString("favorite_event_ids", null);
        ArrayList<String> favoriteEventIds = favoriteEventIdsJson != null ? gson.fromJson(favoriteEventIdsJson, new TypeToken<ArrayList<String>>() {}.getType()) : new ArrayList<>();

        if (isFav) {
            // If the event is now a favorite, add the event ID to the list
            favoriteEventIds.add(eventId);
            // Save the event data
            editor.putString("event_data_" + eventId, gson.toJson(eventData));
        } else {
            // If the event is no longer a favorite, remove the event ID from the list
            favoriteEventIds.remove(eventId);
            // Remove the event data
            editor.remove("event_data_" + eventId);
        }

        // Save the updated list of favorite event IDs
        editor.putString("favorite_event_ids", gson.toJson(favoriteEventIds));
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
