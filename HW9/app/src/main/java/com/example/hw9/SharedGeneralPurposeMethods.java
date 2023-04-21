package com.example.hw9;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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


}
