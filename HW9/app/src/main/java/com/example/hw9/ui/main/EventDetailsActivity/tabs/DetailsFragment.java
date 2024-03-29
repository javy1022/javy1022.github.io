package com.example.hw9.ui.main.EventDetailsActivity.tabs;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.hw9.MySingleton;
import com.example.hw9.R;
import com.example.hw9.SharedGeneralPurposeMethods;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

public class DetailsFragment extends Fragment {
    private ProgressBar event_details_pr;
    private SharedGeneralPurposeMethods shared;

    public DetailsFragment() {
        // required constructor
    }

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ui rendering
        event_details_pr = view.findViewById(R.id.event_details_progress_bar);
        event_details_pr.setVisibility(View.VISIBLE);

        // access data
        Bundle args = getArguments();
        ArrayList<String> event_data = args != null ? args.getStringArrayList("event_data") : null;

        // REST
        shared = new SharedGeneralPurposeMethods();
        get_event_details(view, event_data != null ? event_data.get(6) : null);
    }

    private void get_event_details(View view, String event_id) {
        String backend_url = "https://csci571-hw8-spr23.wl.r.appspot.com/search/event-details/" + event_id;

        JsonObjectRequest json_obj_request = new JsonObjectRequest
                (Request.Method.GET, backend_url, null, resp -> {
                    Gson gson = new Gson();
                    JsonObject gson_resp = gson.fromJson(resp.toString(), JsonObject.class);

                    extract_event_details(view, gson_resp);

                    // UI ready
                    event_details_pr.setVisibility(View.GONE);
                    CardView event_details_card = view.findViewById(R.id.details_card);
                    event_details_card.setVisibility(View.VISIBLE);
                }, error -> {
                    event_details_pr.setVisibility(View.GONE);
                    Log.e("Error", "Volley Error Ticketmaster Event Details: " + error.getMessage());
                });
        MySingleton.getInstance(requireContext()).addToRequestQueue(json_obj_request);
    }

    private void extract_event_details(View view, JsonObject gson_resp) {
        String event_title = shared.general_json_navigator(gson_resp, "name");
        String local_date = shared.general_json_navigator(gson_resp, "dates", "start", "localDate");
        String local_time = shared.general_json_navigator(gson_resp, "dates", "start", "localTime");
        String genre = genre_json_navigator(gson_resp);
        String price_range = priceRange_json_navigator(gson_resp);
        String status = status_json_navigator(gson_resp);
        String ticket_url = shared.general_json_navigator(gson_resp, "url");
        String seatmap_url = shared.general_json_navigator(gson_resp, "seatmap", "staticUrl");

        JsonArray attractions = shared.general_json_arr_navigator(gson_resp, "_embedded", "attractions");
        ArrayList<String> artists = new ArrayList<>();
        ArrayList<String> artist_or_team = new ArrayList<>();
        Bundle artist_name_bundle = new Bundle();
        if (attractions != null) {
            for (JsonElement artist : attractions) {
                JsonObject artist_obj = artist.getAsJsonObject();
                String artist_name = shared.general_json_navigator(artist_obj, "name");
                artist_or_team.add(artist_name);
                JsonArray classifications = shared.general_json_arr_navigator(artist_obj, "classifications");
                if (classifications != null && classifications.size() > 0) {
                    JsonObject classification = classifications.get(0).getAsJsonObject();
                    String artist_category = shared.general_json_navigator(classification, "segment", "name");
                    if ("Music".equalsIgnoreCase(artist_category)) {
                        artists.add(artist_name);
                    }
                }
            }
        }
        // If the json data for artist does not exist
        artist_name_bundle.putStringArrayList("artist_names", artists);
        getParentFragmentManager().setFragmentResult("artist_names", artist_name_bundle);

        JsonArray venues_arr = shared.general_json_arr_navigator(gson_resp, "_embedded", "venues");
        String venue = "";
        if (venues_arr != null && venues_arr.size() > 0) {
            JsonObject desired_venue = venues_arr.get(0).getAsJsonObject();
            venue = shared.general_json_navigator(desired_venue, "name");

            // Pass venue name to venue tab
            Bundle venue_name_bundle = new Bundle();
            venue_name_bundle.putString("venue_name", venue);
            getParentFragmentManager().setFragmentResult("venue_name", venue_name_bundle);
        }

        local_date = reformat_localDate(local_date);
        local_time = reformat_localTime(local_time);

        set_share_icons(ticket_url, event_title);
        set_event_details_card_ui(view, artist_or_team, venue, local_date, local_time, genre, price_range, status, ticket_url, seatmap_url);
    }

    private void set_event_details_card_ui(View view, List<String> artist_or_team, String venue, String date, String time, String genre, String price_range, String status, String ticket_url, String seatmap_url) {
        final TextView artist_team_tv = view.findViewById(R.id.artist_team);
        final TextView venue_tv = view.findViewById(R.id.venue);
        final TextView date_tv = view.findViewById(R.id.date);
        final TextView time_tv = view.findViewById(R.id.time);
        final TextView genres_tv = view.findViewById(R.id.genres);
        final TextView price_range_tv = view.findViewById(R.id.price_range);
        final TextView status_tv = view.findViewById(R.id.ticket_status);
        final TextView ticket_url_tv = view.findViewById(R.id.ticket_url);
        final ImageView seatmap_img = view.findViewById(R.id.seatmap_img);

        if (!artist_or_team.isEmpty()) {
            String formatted_artist_or_team = concat_artists_or_teams(artist_or_team);
            artist_team_tv.setText(formatted_artist_or_team);
            artist_team_tv.setSelected(true);
        } else artist_team_tv.setText("N/A");

        set_textView(venue_tv, venue);
        set_textView(date_tv, date);
        set_textView(time_tv, time);
        set_textView(genres_tv, genre);
        set_textView(price_range_tv, price_range);

        if (!status.isEmpty()) {
            status_tv.setText(status);

            GradientDrawable status_tv_background = (GradientDrawable) status_tv.getBackground();
            switch (status) {
                case "On Sale":
                    status_tv_background.setColor(ContextCompat.getColor(requireContext(), R.color.green));
                    break;
                case "Rescheduled":
                case "Postponed":
                    status_tv_background.setColor(ContextCompat.getColor(requireContext(), R.color.status_postpone));
                    break;
                case "Off Sale":
                    status_tv_background.setColor(ContextCompat.getColor(requireContext(), R.color.red));
                    break;
                case "Canceled":
                    status_tv_background.setColor(ContextCompat.getColor(requireContext(), R.color.black));
                    break;
            }
            status_tv.setSelected(true);
        } else status_tv.setText("N/A");

        if (!ticket_url.isEmpty()) {
            set_textView(ticket_url_tv, ticket_url);
            ticket_url_tv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            ticket_url_tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));
            ticket_url_tv.setOnClickListener(v -> {
                // open website with browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ticket_url));
                v.getContext().startActivity(intent);
            });
        } else ticket_url_tv.setVisibility(View.GONE);

        if (!seatmap_url.isEmpty()) {
            seatmap_img.setVisibility(View.VISIBLE);

            Glide.with(requireContext())
                    .load(seatmap_url)
                    .into(seatmap_img);
        } else seatmap_img.setVisibility(View.GONE);
    }

    /* custom json data navigators */
    private String genre_json_navigator(JsonObject json_obj) {
        JsonArray classifications_arr = shared.general_json_arr_navigator(json_obj, "classifications");
        StringJoiner genre_buffer = new StringJoiner(" | ");

        if (classifications_arr != null && classifications_arr.size() > 0) {
            JsonObject classification_obj = classifications_arr.get(0).getAsJsonObject();
            String segment = shared.general_json_navigator(classification_obj, "segment", "name");
            String genre_name = shared.general_json_navigator(classification_obj, "genre", "name");
            String sub_genre = shared.general_json_navigator(classification_obj, "subGenre", "name");
            String type = shared.general_json_navigator(classification_obj, "name");
            String subtype = shared.general_json_navigator(classification_obj, "subType", "name");

            // formatting
            if (segment.length() > 0) genre_buffer.add(segment);
            if (genre_name.length() > 0) genre_buffer.add(genre_name);
            if (sub_genre.length() > 0) genre_buffer.add(sub_genre);
            if (type.length() > 0) genre_buffer.add(type);
            if (subtype.length() > 0) genre_buffer.add(subtype);
        }
        return genre_buffer.toString();
    }

    private String priceRange_json_navigator(JsonObject json_obj) {
        JsonArray priceRange_arr = shared.general_json_arr_navigator(json_obj, "priceRanges");
        String price_range = "";
        if (priceRange_arr != null && priceRange_arr.size() > 0) {
            JsonObject priceRange_obj = priceRange_arr.get(0).getAsJsonObject();
            double min = priceRange_obj.has("min") ? priceRange_obj.get("min").getAsDouble() : -1;
            double max = priceRange_obj.has("max") ? priceRange_obj.get("max").getAsDouble() : -1;
            String currency = shared.general_json_navigator(priceRange_obj, "currency");

            if (min == -1) {
                price_range = max + " - " + max + " " + "(" + currency + ")";
            } else if (max == -1) {
                price_range = min + " - " + min + " " + "(" + currency + ")";
            } else {
                price_range = min + " - " + max + " " + "(" + currency + ")";
            }
        }
        return price_range;
    }

    private String status_json_navigator(JsonObject json_obj) {
        String status = shared.general_json_navigator(json_obj, "dates", "status", "code");

        switch (status.toLowerCase()) {
            case "onsale":
                status = "On Sale";
                break;
            case "offsale":
                status = "Off Sale";
                break;
            case "cancelled":
            case "canceled":
                status = "Canceled";
                break;
            case "rescheduled":
                status = "Rescheduled";
                break;
            case "postponed":
                status = "Postponed";
                break;
            default:
                status = "";
                break;
        }
        return status;
    }

    /* helper functions */
    private String concat_artists_or_teams(List<String> artist_or_team) {
        StringJoiner artists_buffer = new StringJoiner(" | ");
        for (String artist : artist_or_team) if (artist.length() > 0) artists_buffer.add(artist);
        return artists_buffer.toString();
    }

    private static String reformat_localDate(String date) {
        String input_date_format = "yyyy-M-d";
        String desired_date_format = "MMM d, yyyy";
        Locale locale = Locale.US;

        try {
            LocalDate parsed_date = LocalDate.parse(date, DateTimeFormatter.ofPattern(input_date_format, locale));
            return parsed_date.format(DateTimeFormatter.ofPattern(desired_date_format, locale));
        } catch (Exception ignored) {
        }
        return date;
    }

    private String reformat_localTime(String time) {
        String input_time_format = "HH:mm:ss";
        String desired_time_format = "h:mm a";
        Locale locale = Locale.US;

        try {
            LocalTime parsed_time = LocalTime.parse(time, DateTimeFormatter.ofPattern(input_time_format, locale));
            return parsed_time.format(DateTimeFormatter.ofPattern(desired_time_format, locale));
        } catch (Exception ignored) {
        }
        return time;
    }

    private void set_share_icons(String ticket_url, String event_title) {
        if (!ticket_url.isEmpty()) {
            ImageButton fb_icon = requireActivity().findViewById(R.id.facebook);
            ShapeableImageView twitter_icon = requireActivity().findViewById(R.id.twitter);

            fb_icon.setOnClickListener(v -> {
                String fb_share_url = String.format("https://www.facebook.com/sharer/sharer.php?u=%s&src=sdkpreparse", ticket_url);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(fb_share_url));
                startActivity(intent);
            });

            twitter_icon.setOnClickListener(v -> {
                String twitter_share_url = String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                        Uri.encode("Check out " + event_title + " on TicketMaster!"), Uri.encode(ticket_url));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(twitter_share_url));
                startActivity(intent);
            });
        }
    }

    private void set_textView(TextView tv, String tv_data) {
        if (!tv_data.isEmpty()) {
            tv.setText(tv_data);
            tv.setSelected(true);
        } else tv.setText("N/A");
    }

}
