package com.example.hw9.ui.main.EventDetailsActivity.tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.hw9.MySingleton;
import com.example.hw9.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

public class DetailsFragment  extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String event_id;

    public DetailsFragment () {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoriteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment  fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        ArrayList<String> event_data = args != null ? args.getStringArrayList("event_data") : null;

        get_event_details(view, event_data != null ? event_data.get(6) : null);



    }

    private void get_event_details(View view, String event_id){
        String backend_url = "https://csci571-hw8-spr23.wl.r.appspot.com/search/event-details/"+ event_id;

        JsonObjectRequest json_obj_request = new JsonObjectRequest
                (Request.Method.GET, backend_url, null, resp -> {
                    Gson gson = new Gson();
                    JsonObject gson_resp = gson.fromJson(resp.toString(), JsonObject.class);

                    extract_event_details(view, gson_resp);

                }, error -> {
                    // Handle the error
                    Log.e("Error", "Volley Error Ticketmaster Event Details: " + error.getMessage());
                });
        MySingleton.getInstance(requireContext()).addToRequestQueue(json_obj_request);

    }

    private void extract_event_details(View view, JsonObject gson_resp) {
        String event_title = general_json_navigator(gson_resp, "name");
        String event_id = general_json_navigator(gson_resp, "id");
        String local_date = general_json_navigator(gson_resp, "dates", "start", "localDate");
        String local_time = general_json_navigator(gson_resp, "dates", "start", "localTime");
        String genre = genre_json_navigator(gson_resp);
        String price_range = priceRange_json_navigator(gson_resp);
        String status = status_json_navigator(gson_resp);
        String ticket_url = general_json_navigator(gson_resp, "url");
        String seatmap_url = general_json_navigator(gson_resp, "seatmap", "staticUrl");

        JsonArray attractions = general_json_arr_navigator(gson_resp, "_embedded", "attractions");
        List<String> artist_or_team = new ArrayList<>();
        if (attractions != null) {
            for (JsonElement artist : attractions) {
                artist_or_team.add(general_json_navigator(artist.getAsJsonObject(), "name"));
            }
        }

        JsonArray venues_arr = general_json_arr_navigator(gson_resp, "_embedded", "venues");
        String venue = "";
        if (venues_arr != null && venues_arr.size() > 0) {
            JsonObject desired_venue = venues_arr.get(0).getAsJsonObject();
            venue = general_json_navigator(desired_venue, "name");
        }

        local_time = reformat_localTime(local_time);

        // remove after
        Log.d("yyy", "event_title: " + event_title);
        Log.d("yyy", "local_date: " + local_date);
        Log.d("yyy", "local_time : " + local_time);
        Log.d("yyy", "artist_or_team  : " + artist_or_team);
        Log.d("yyy", "venue  : " + venue);
        Log.d("yyy", "genre  : " + genre);
        Log.d("yyy", "price_range  : " + price_range);
        Log.d("yyy", "status  : " + status);
        Log.d("yyy", "ticket_url  : " + ticket_url);
        Log.d("yyy", "seatmap_url  : " + seatmap_url);


        set_event_details_card_ui(view, artist_or_team);
    }

    private void set_event_details_card_ui(View view, List<String> artist_or_team){
        final TextView artist_team = view.findViewById(R.id.artist_team);
        final TextView artist_team_subtitle = view.findViewById(R.id.artist_team_subtitle);

        if(!artist_or_team.isEmpty()) {
            String formatted_artist_or_team = concat_artists_or_teams(artist_or_team);
            artist_team.setText(formatted_artist_or_team);
            artist_team.setSelected(true);
            artist_team_subtitle.setVisibility(View.VISIBLE);
            artist_team.setVisibility(View.VISIBLE);
        }else{
            artist_team_subtitle.setVisibility(View.GONE);
            artist_team.setVisibility(View.GONE);
        }
    }

    // extract desired data given a sequences of keys
    private String general_json_navigator(JsonObject json_obj, String... keys) {
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

    private String genre_json_navigator(JsonObject json_obj) {
        JsonArray classifications_arr = general_json_arr_navigator(json_obj, "classifications");
        StringJoiner genre_buffer = new StringJoiner(" | ");

        if (classifications_arr != null && classifications_arr.size() > 0) {
            JsonObject classification_obj = classifications_arr.get(0).getAsJsonObject();
            String segment = general_json_navigator(classification_obj, "segment", "name").trim();
            String genre_name = general_json_navigator(classification_obj, "genre", "name").trim();
            String sub_genre = general_json_navigator(classification_obj, "subGenre", "name").trim();
            String type = general_json_navigator(classification_obj, "name").trim();
            String subtype = general_json_navigator(classification_obj, "subType", "name").trim();

            if (!"undefined".equalsIgnoreCase(segment) && segment.length() > 0) {
                genre_buffer.add(segment);
            }
            if (!"undefined".equalsIgnoreCase(genre_name) && genre_name.length() > 0) {
                genre_buffer.add(genre_name);
            }
            if (!"undefined".equalsIgnoreCase(sub_genre) && sub_genre.length() > 0) {
                genre_buffer.add(sub_genre);
            }
            if (!"undefined".equalsIgnoreCase(type) && type.length() > 0) {
                genre_buffer.add(type);
            }
            if (!"undefined".equalsIgnoreCase(subtype) && subtype.length() > 0) {
                genre_buffer.add(subtype);
            }
        }
        return genre_buffer.toString();
    }



    private String priceRange_json_navigator(JsonObject json_obj) {
        JsonArray priceRange_arr = general_json_arr_navigator(json_obj, "priceRanges");
        String price_range = "";
        if (priceRange_arr != null && priceRange_arr.size() > 0) {
            JsonObject priceRange_obj = priceRange_arr.get(0).getAsJsonObject();
            double min = priceRange_obj.has("min") ? priceRange_obj.get("min").getAsDouble() : -1;
            double max = priceRange_obj.has("max") ? priceRange_obj.get("max").getAsDouble() : -1;
            String currency = general_json_navigator(priceRange_obj, "currency");

            if (min == -1) {
                price_range = max + " - " + max + " " + currency;
            } else if (max == -1) {
                price_range = min + " - " + min + " " + currency;
            } else {
                price_range = min + " - " + max + " " +  "(" + currency + ")";
            }
        }

        return price_range;
    }

    private String status_json_navigator(JsonObject json_obj) {
        String status = general_json_navigator(json_obj, "dates", "status", "code");

        if ("onsale".equalsIgnoreCase(status)) {
            status = "On Sale";
        } else if ("offsale".equalsIgnoreCase(status)) {
            status = "Off Sale";
        } else if ("cancelled".equalsIgnoreCase(status) || "canceled".equalsIgnoreCase(status)){
            status = "Canceled";
    } else if ("rescheduled".equalsIgnoreCase(status)) {
        status = "Rescheduled";
    } else if ("postponed".equalsIgnoreCase(status)) {
        status = "Postponed";
    } else {
        status = "";
    }

        return status;
    }

    // helper function to  extract desired data array given a sequences of keys
    private JsonArray general_json_arr_navigator(JsonObject json_obj, String... keys) {
        JsonElement json_element = json_obj;
        for (String key : keys) {
            if (json_element == null) return null;
            json_element = json_element.getAsJsonObject().get(key);
        }
        if (json_element == null) return null;
        return json_element.getAsJsonArray();
    }

    private String concat_artists_or_teams(List<String> artist_or_team) {
        StringJoiner artists_buffer = new StringJoiner(" | ");

        for (String artist : artist_or_team) {
            artist = artist.trim();
            if (!"undefined".equalsIgnoreCase(artist) && artist.length() > 0) {
                artists_buffer.add(artist);
            }
        }
        return artists_buffer.toString();
    }

    private String reformat_localTime(String time) {
        String input_time_format = "HH:mm:ss";
        String desired_time_format = "h:mm a";
        Locale locale = Locale.US;

        try {
            Date parsed_time = new SimpleDateFormat(input_time_format, locale).parse(time);
            if (parsed_time != null) {
                return new SimpleDateFormat(desired_time_format, locale).format(parsed_time);
            }
        } catch (ParseException ignored) {
        }
        return time;
    }


}
