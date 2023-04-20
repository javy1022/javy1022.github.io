package com.example.hw9;

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
}
