package com.example.hw9.ui.main.EventDetailsActivity.tabs;

import android.app.Activity;
import android.net.Uri;
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
import com.example.hw9.RecycleViewDecorator;
import com.example.hw9.SharedGeneralPurposeMethods;
import com.example.hw9.ui.main.EventDetailsActivity.adapter.ArtistSpotifyRecycleViewAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VenueFragment  extends Fragment implements OnMapReadyCallback {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private SharedGeneralPurposeMethods shared;

    public VenueFragment () {
        // Required empty public constructor
    }
    public static VenueFragment newInstance(String param1, String param2) {
        VenueFragment  fragment = new VenueFragment();
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
        return inflater.inflate(R.layout.fragment_venue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shared = new SharedGeneralPurposeMethods();

        // async getter for venue name from detail fragment
        get_and_utilize_venue_name(view);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void  get_and_utilize_venue_name(View view){
        getParentFragmentManager().setFragmentResultListener("venue_name", this, (requestKey, bundle) -> {
            String venue = bundle.getString("venue_name");
            // REST
            get_venue_info(view,venue);
        });
    }

    private void get_venue_info(View view, String venue){
        String base_url = "https://csci571-hw8-spr23.wl.r.appspot.com/search/venue-detail";
        Uri.Builder builder = Uri.parse(base_url).buildUpon();
        builder.appendQueryParameter("venue_name", venue);
        String backend_url = builder.build().toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, backend_url, null, resp -> {
                    Gson gson = new Gson();
                    JsonObject gson_resp = gson.fromJson(resp.toString(), JsonObject.class);

                    JsonArray desired_venue = shared.general_json_arr_navigator(gson_resp,"_embedded","venues");
                    if (desired_venue!= null && desired_venue.size() > 0) {
                        JsonObject venue_obj = desired_venue.get(0).getAsJsonObject();

                        String venue_name = shared.general_json_navigator(venue_obj,"name");
                        String address = shared.general_json_navigator(venue_obj,"address", "line1");
                        String city = shared.general_json_navigator(venue_obj,"city", "name");
                        String state = shared.general_json_navigator(venue_obj,"state", "name");
                        String contact = shared.general_json_navigator(venue_obj,"boxOfficeInfo", "phoneNumberDetail");
                        String hours = shared.general_json_navigator(venue_obj,"boxOfficeInfo", "openHoursDetail");
                        String general_rule = shared.general_json_navigator(venue_obj,"generalInfo", "generalRule");
                        String child_rule = shared.general_json_navigator(venue_obj,"generalInfo", "childRule");

                        String city_state = custom_city_state_formatter(city,state);
                        set_card_UI(view,venue_name, address, city_state, contact);

                        Log.d("v request", "test resp: " + venue_name );
                        Log.d("v request", "test resp: " + address );
                        Log.d("v request", "test resp: " + city );
                        Log.d("v request", "test resp: " + state );
                        Log.d("v request", "test resp: " + contact );
                        Log.d("v request", "test resp: " + hours );
                        Log.d("v request", "test resp: " + general_rule);
                        Log.d("v request", "test resp: " + child_rule);
                        Log.d("v request", "test resp: " + city_state );

                    }

                }, error -> {
                    Log.e("Error", "Volley Error Get Venue Details: " + error.getMessage());
                });
        MySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void set_card_UI(View view,String venue_name, String address, String city_state, String contact){
        TextView venue_tv =  view.findViewById(R.id.venue);
        TextView address_tv =  view.findViewById(R.id.address);
        TextView city_tv =  view.findViewById(R.id.city);
        TextView contact_tv =  view.findViewById(R.id.contact);

        if(!venue_name.isEmpty()) venue_tv.setText(venue_name);
        else venue_tv.setText("N/A");

        if(!address.isEmpty()) address_tv.setText(address);
        else address_tv.setText("N/A");

        if(!city_state.isEmpty()) city_tv.setText(city_state);
        else city_tv.setText("N/A");

        if(!contact.isEmpty()) contact_tv.setText(contact);
        else contact_tv.setText("N/A");

        shared.textViews_enable_selected(venue_tv, address_tv, city_tv, contact_tv);
    }

    private String custom_city_state_formatter(String city, String state){
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng location = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }
}
