package com.example.hw9.ui.main.EventDetailsActivity.tabs;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.hw9.MySingleton;
import com.example.hw9.R;
import com.example.hw9.RecycleViewDecorator;
import com.example.hw9.SharedGeneralPurposeMethods;
import com.example.hw9.ui.main.EventDetailsActivity.adapter.ArtistSpotifyRecycleViewAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class ArtistsFragment extends Fragment {
    private SharedGeneralPurposeMethods shared;
    private ArtistSpotifyRecycleViewAdapter artist_spotify_adapter;
    private final ArrayList<ArrayList<Object>> artist_spotify_matrix = new ArrayList<>();
    private RecyclerView artist_spotify_recycleView;
    private ProgressBar artist_cards_pb;
    private CardView artist_empty;

    public ArtistsFragment() {
        // required constructor
    }

    public static ArtistsFragment newInstance() {
        return new ArtistsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shared = new SharedGeneralPurposeMethods();
        artist_spotify_recycleView = view.findViewById(R.id.artists_spotify_recycle_view);
        artist_cards_pb = view.findViewById(R.id.artist_cards_progress_bar);
        artist_empty = view.findViewById(R.id.artist_empty);

        artist_cards_pb.setVisibility(View.VISIBLE);
        // async getter for sequence of artist names passed from detail fragment
        get_and_utilize_artist_names();
        // margin between each item in event result recycleView
        artist_spotify_recycleView.addItemDecoration(new RecycleViewDecorator(30));
    }

    private void get_and_utilize_artist_names() {
        getParentFragmentManager().setFragmentResultListener("artist_names", this, (requestKey, bundle) -> {
            ArrayList<String> artist_names = bundle.getStringArrayList("artist_names");
            if (artist_names.isEmpty()) {
                artist_cards_pb.setVisibility(View.GONE);
                artist_empty.setVisibility(View.VISIBLE);
                return;
            }

            CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
            for (String artist : artist_names)
                future = future.thenCompose(v -> get_artists_spotify_request(artist));

            future.thenRun(() -> {
                Activity activity = getActivity();
                if (activity != null) {
                    // main UI thread
                    activity.runOnUiThread(() -> {
                        artist_spotify_adapter = new ArtistSpotifyRecycleViewAdapter(artist_spotify_matrix);
                        shared.generate_linearLayout_recycleView(getContext(), artist_spotify_recycleView, artist_spotify_adapter);
                        artist_cards_pb.setVisibility(View.GONE);
                    });
                }
            });
        });
    }

    private CompletableFuture<Void> get_artists_spotify_request(String artist) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        String base_url = "https://csci571-hw8-spr23.wl.r.appspot.com/search/artists";
        Uri.Builder builder = Uri.parse(base_url).buildUpon();
        builder.appendQueryParameter("q", artist);
        String backend_url = builder.build().toString();

        JsonObjectRequest json_obj_request = new JsonObjectRequest
                (Request.Method.GET, backend_url, null, resp -> {
                    Gson gson = new Gson();
                    JsonObject gson_resp = gson.fromJson(resp.toString(), JsonObject.class);
                    ArrayList<Object> artist_data = new ArrayList<>();

                    JsonArray desired_artist_spotify_info = shared.general_json_arr_navigator(gson_resp, "artists", "items");
                    if (desired_artist_spotify_info != null && desired_artist_spotify_info.size() > 0) {
                        JsonObject artist_obj = desired_artist_spotify_info.get(0).getAsJsonObject();
                        // name
                        String artist_name = shared.general_json_navigator(artist_obj, "name");
                        artist_data.add(artist_name);

                        // artist image with chosen size
                        JsonObject artist_img_obj = shared.general_json_arr_navigator(artist_obj, "images").get(2).getAsJsonObject();
                        String artist_img = shared.general_json_navigator(artist_img_obj, "url");
                        artist_data.add(artist_img);

                        // popularity
                        String artist_popularity = shared.general_json_navigator(artist_obj, "popularity");
                        artist_data.add(artist_popularity);

                        // formatted number of followers
                        String artist_followers_str = shared.general_json_navigator(artist_obj, "followers", "total");
                        artist_data.add(custom_str_formatter(artist_followers_str));

                        // spotify link
                        String artist_spotify_link = shared.general_json_navigator(artist_obj, "external_urls", "spotify");
                        artist_data.add(artist_spotify_link);

                        // nested async get request for artist spotify albums
                        String artist_id = shared.general_json_navigator(artist_obj, "id");
                        get_artist_spotify_album(artist_id).thenAccept(artist_albums -> {
                            artist_data.add(artist_albums);
                            artist_spotify_matrix.add(artist_data);
                            future.complete(null);
                        });
                    } else future.complete(null);

                }, error -> {
                    future.complete(null);
                    artist_cards_pb.setVisibility(View.GONE);
                    Log.e("Error", "Volley Error Spotify Artist Search: " + error.getMessage());
                });
        MySingleton.getInstance(requireContext()).addToRequestQueue(json_obj_request);
        return future;
    }

    private CompletableFuture<ArrayList<String>> get_artist_spotify_album(String artist_id) {
        CompletableFuture<ArrayList<String>> future = new CompletableFuture<>();
        ArrayList<String> artist_albums = new ArrayList<>();
        String backend_url = "https://csci571-hw8-spr23.wl.r.appspot.com/search/artists-id/" + artist_id;
        JsonObjectRequest json_obj_request = new JsonObjectRequest
                (Request.Method.GET, backend_url, null, resp -> {
                    Gson gson = new Gson();
                    JsonObject gson_resp = gson.fromJson(resp.toString(), JsonObject.class);

                    JsonArray albums_arr = shared.general_json_arr_navigator(gson_resp, "items");
                    for (JsonElement album_element : albums_arr) {
                        JsonObject album_obj = album_element.getAsJsonObject();
                        JsonArray album_img_arr = shared.general_json_arr_navigator(album_obj, "images");

                        if (album_img_arr != null && album_img_arr.size() > 0) {
                            JsonObject desired_img_obj = album_img_arr.get(0).getAsJsonObject();
                            String desired_img_url = shared.general_json_navigator(desired_img_obj, "url");
                            artist_albums.add(desired_img_url);
                        }
                    }
                    future.complete(artist_albums);
                }, error -> {
                    Log.e("Error", "Volley Error Spotify Album Search: " + error.getMessage());
                    artist_cards_pb.setVisibility(View.GONE);
                    future.complete(null);
                });
        MySingleton.getInstance(requireContext()).addToRequestQueue(json_obj_request);
        return future;
    }

    private String custom_str_formatter(String artist_followers_str) {
        int artist_followers_num = Integer.parseInt(artist_followers_str);
        String artist_followers_formatted;
        DecimalFormat decimalFormat;
        if (artist_followers_num >= 1000000) {
            decimalFormat = new DecimalFormat("0.0M");
            artist_followers_formatted = decimalFormat.format(artist_followers_num / 1000000.0);
        } else if (artist_followers_num >= 1000) {
            decimalFormat = new DecimalFormat("0.0K");
            artist_followers_formatted = decimalFormat.format(artist_followers_num / 1000.0);
        } else {
            artist_followers_formatted = Integer.toString(artist_followers_num);
        }
        artist_followers_formatted += " Followers";
        return artist_followers_formatted;
    }

}

