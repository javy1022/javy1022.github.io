package com.example.hw9.ui.main.EventDetailsActivity.tabs;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.hw9.MySingleton;
import com.example.hw9.R;
import com.example.hw9.SharedGeneralPurposeMethods;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class VenueFragment extends Fragment implements OnMapReadyCallback {
    private static Double venue_lat;
    private static Double venue_lng;
    private SharedGeneralPurposeMethods shared;
    private NestedScrollView nested_scroll_view;
    private ProgressBar venue_pr;

    public VenueFragment() {
        // required constructor
    }

    public static VenueFragment newInstance() {
        return new VenueFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_venue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        venue_pr = view.findViewById(R.id.venue_progress_bar);
        venue_pr.setVisibility(View.VISIBLE);
        nested_scroll_view = view.findViewById(R.id.nested_scroll_view);
        shared = new SharedGeneralPurposeMethods();

        // async getter for venue name from detail fragment
        get_and_utilize_venue_name(view);
    }

    private void get_and_utilize_venue_name(View view) {
        getParentFragmentManager().setFragmentResultListener("venue_name", this, (requestKey, bundle) -> {
            String venue = bundle.getString("venue_name");
            // REST
            get_venue_info(view, venue);
        });
    }

    private void get_venue_info(View view, String venue) {
        String base_url = "https://csci571-hw8-spr23.wl.r.appspot.com/search/venue-detail";
        Uri.Builder builder = Uri.parse(base_url).buildUpon();
        builder.appendQueryParameter("venue_name", venue);
        String backend_url = builder.build().toString();

        JsonObjectRequest json_obj_request = new JsonObjectRequest
                (Request.Method.GET, backend_url, null, resp -> {
                    Gson gson = new Gson();
                    JsonObject gson_resp = gson.fromJson(resp.toString(), JsonObject.class);

                    JsonArray desired_venue = shared.general_json_arr_navigator(gson_resp, "_embedded", "venues");
                    if (desired_venue != null && desired_venue.size() > 0) {
                        JsonObject venue_obj = desired_venue.get(0).getAsJsonObject();

                        String venue_name = shared.general_json_navigator(venue_obj, "name");
                        String address = shared.general_json_navigator(venue_obj, "address", "line1");
                        String city = shared.general_json_navigator(venue_obj, "city", "name");
                        String state = shared.general_json_navigator(venue_obj, "state", "name");
                        String contact = shared.general_json_navigator(venue_obj, "boxOfficeInfo", "phoneNumberDetail");
                        String hours = shared.general_json_navigator(venue_obj, "boxOfficeInfo", "openHoursDetail");
                        String general_rule = shared.general_json_navigator(venue_obj, "generalInfo", "generalRule");
                        String child_rule = shared.general_json_navigator(venue_obj, "generalInfo", "childRule");
                        venue_lat = Double.parseDouble(shared.general_json_navigator(venue_obj, "location", "latitude"));
                        venue_lng = Double.parseDouble(shared.general_json_navigator(venue_obj, "location", "longitude"));
                        load_googleMap();

                        String city_state = custom_city_state_formatter(city, state);
                        set_card_UI(view, venue_name, address, city_state, contact);
                        set_subCard_UI(view, hours, general_rule, child_rule);

                        // progress bar
                        venue_pr.setVisibility(View.GONE);
                        CardView venue_card_tv = view.findViewById(R.id.venue_card);
                        CardView venue_sub_card_tv = view.findViewById(R.id.venue_sub_card);
                        venue_card_tv.setVisibility(View.VISIBLE);
                        venue_sub_card_tv.setVisibility(View.VISIBLE);
                    }
                }, error -> Log.e("Error", "Volley Error Get Venue Details: " + error.getMessage()));
        MySingleton.getInstance(requireContext()).addToRequestQueue(json_obj_request);
    }

    private void set_card_UI(View view, String venue_name, String address, String city_state, String contact) {
        TextView venue_tv = view.findViewById(R.id.venue);
        TextView address_tv = view.findViewById(R.id.address);
        TextView city_tv = view.findViewById(R.id.city);
        TextView contact_tv = view.findViewById(R.id.contact);

        if (!venue_name.isEmpty()) venue_tv.setText(venue_name);
        else venue_tv.setText("N/A");

        if (!address.isEmpty()) address_tv.setText(address);
        else address_tv.setText("N/A");

        if (!city_state.isEmpty()) city_tv.setText(city_state);
        else city_tv.setText("N/A");

        if (!contact.isEmpty()) contact_tv.setText(contact);
        else contact_tv.setText("N/A");

        SharedGeneralPurposeMethods.textViews_enable_selected(venue_tv, address_tv, city_tv, contact_tv);
    }

    private void set_subCard_UI(View view, String hours, String general_rule, String child_rule) {
        TextView hours_tv = view.findViewById(R.id.hours);
        TextView general_rule_tv = view.findViewById(R.id.general_rules);
        TextView child_rule_tv = view.findViewById(R.id.child_rules);

        if (!hours.isEmpty()) {
            hours_tv.setText(hours);
            show_moreLess_toggle(hours_tv);
        } else hours_tv.setText("N/A");

        if (!general_rule.isEmpty()) {
            general_rule_tv.setText(general_rule);
            show_moreLess_toggle(general_rule_tv);
        } else general_rule_tv.setText("N/A");

        if (!child_rule.isEmpty()) {
            child_rule_tv.setText(child_rule);
            show_moreLess_toggle(child_rule_tv);
        } else child_rule_tv.setText("N/A");
    }

    private void show_moreLess_toggle(TextView tv) {
        tv.setOnClickListener(new View.OnClickListener() {
            private boolean show_more = false;

            @Override
            public void onClick(View v) {
                if (show_more) {
                    tv.setMaxLines(3);
                    tv.setEllipsize(TextUtils.TruncateAt.END);
                } else {
                    tv.setMaxLines(Integer.MAX_VALUE);
                    tv.setEllipsize(null);
                }
                show_more = !show_more;
            }
        });
    }

    private String custom_city_state_formatter(String city, String state) {
        StringBuilder full_address_buffer = new StringBuilder();

        if (!city.isEmpty()) {
            full_address_buffer.append(city);
        }

        if (!state.isEmpty()) {
            if (full_address_buffer.length() > 0) {
                full_address_buffer.append(", ");
            }
            full_address_buffer.append(state);
        }
        return full_address_buffer.toString();
    }

    /* Google Map SDK */
    private void load_googleMap() {
        SupportMapFragment map_fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (map_fragment != null) map_fragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap google_map) {
        LatLng location = new LatLng(venue_lat, venue_lng);
        google_map.addMarker(new MarkerOptions()
                .position(location));
        google_map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

        // Disable parent scrolls when dragging Google Map
        google_map.setOnCameraMoveStartedListener(reason -> nested_scroll_view.requestDisallowInterceptTouchEvent(reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE));
    }

}
