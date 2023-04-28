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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VenueFragment  extends Fragment {
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

    }

    private void  get_and_utilize_venue_name(View view){
        getParentFragmentManager().setFragmentResultListener("venue_name", this, (requestKey, bundle) -> {
            String venue = bundle.getString("venue_name");

            // REST
            get_venue_info(venue);
        });
    }

    private void get_venue_info(String venue){
        String base_url = "https://csci571-hw8-spr23.wl.r.appspot.com/search/venue-detail";
        Uri.Builder builder = Uri.parse(base_url).buildUpon();
        builder.appendQueryParameter("venue_name", venue);
        String backend_url = builder.build().toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, backend_url, null, resp -> {
                    Gson gson = new Gson();
                    JsonObject gson_resp = gson.fromJson(resp.toString(), JsonObject.class);

                    Log.d("v request", "test resp: " + gson_resp);

                }, error -> {
                    Log.e("Error", "Volley Error Get Venue Details: " + error.getMessage());
                });
        MySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest);
    }

}
