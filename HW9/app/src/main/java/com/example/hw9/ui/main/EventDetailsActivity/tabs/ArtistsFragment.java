package com.example.hw9.ui.main.EventDetailsActivity.tabs;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.hw9.MySingleton;
import com.example.hw9.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class ArtistsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public ArtistsFragment () {
        // Required empty public constructor
    }
    public static ArtistsFragment newInstance(String param1, String param2) {
        ArtistsFragment  fragment = new ArtistsFragment();
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
        return inflater.inflate(R.layout.fragment_artists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // async getter for sequence of artist names passed from detail fragment
        get_and_utilize_artist_names();

    }

    private void get_and_utilize_artist_names(){
        getParentFragmentManager().setFragmentResultListener("artist_names", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                ArrayList<String> artist_names = bundle.getStringArrayList("artist_names");
                get_artists_spotify_request(artist_names);
            }
        });
    }

    private void get_artists_spotify_request(ArrayList<String> artist_names){
        for (String artist : artist_names) {
            String base_url = "https://csci571-hw8-spr23.wl.r.appspot.com/search/artists";
            Uri.Builder builder = Uri.parse(base_url).buildUpon();
            builder.appendQueryParameter("q", artist);
            String backend_url = builder.build().toString();

            JsonObjectRequest json_obj_request = new JsonObjectRequest
                    (Request.Method.GET, backend_url, null, resp -> {
                        Gson gson = new Gson();
                        JsonObject gson_resp = gson.fromJson(resp.toString(), JsonObject.class);
                        Log.d("dd man", "results :" + gson_resp);



                    }, error -> {
                        // Handle the error
                        Log.e("Error", "Volley Error Spotify Artist Search: " + error.getMessage());
                    });
            MySingleton.getInstance(requireContext()).addToRequestQueue(json_obj_request);
        }
    }

}

