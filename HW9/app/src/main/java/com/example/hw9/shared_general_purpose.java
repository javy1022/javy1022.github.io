package com.example.hw9;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/* Class to share common functions */

public class shared_general_purpose {

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

    public <T extends RecyclerView.Adapter> void generate_linearLayout_recycleView(Context context, RecyclerView recycle_view , T recycle_view_adapter){
        // Init
        RecyclerView.LayoutManager event_search_recycleView_layoutManager = new LinearLayoutManager(context);
        recycle_view.setLayoutManager(event_search_recycleView_layoutManager);
        // Populate recycle view
        recycle_view.setAdapter(recycle_view_adapter);
    }
}
