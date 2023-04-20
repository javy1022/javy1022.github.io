package com.example.hw9.ui.main.EventDetailsActivity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hw9.R;
import com.example.hw9.SharedGeneralPurposeMethods;

import java.util.ArrayList;

public class ArtistSpotifyRecycleViewAdapter extends RecyclerView.Adapter<ArtistSpotifyRecycleViewAdapter.ViewHolder> {

    private final ArrayList<ArrayList<String>> artists_spotify_matrix;

    private SharedGeneralPurposeMethods shared;

    public ArtistSpotifyRecycleViewAdapter(ArrayList<ArrayList<String>> artists_spotify_matrix) {
        this.artists_spotify_matrix = artists_spotify_matrix;
    }

    @NonNull
    @Override
    public ArtistSpotifyRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artists_spotify_recycle_view, parent, false);
        return new ArtistSpotifyRecycleViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArtistSpotifyRecycleViewAdapter.ViewHolder holder, int position) {
        ArrayList<String> artist_spotify_info = artists_spotify_matrix.get(position);
        shared = new SharedGeneralPurposeMethods();
        // name
        String name = artist_spotify_info.get(0);
        holder.name.setText(name);

        // artist spotify image
        String img_url = artist_spotify_info.get(1);
        shared.set_recycleViews_imgView(holder,img_url,holder.img);

        // artist followers
        String followers = artist_spotify_info.get(3);
        holder.followers.setText(followers);

    }

    @Override
    public int getItemCount() {
        return artists_spotify_matrix.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageView img;
        private final TextView followers;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.artist_name);
            img = itemView.findViewById(R.id.artist_spotify_img);
            followers = itemView.findViewById(R.id.artist_followers);

            // Enable selected to make marquee effect works on TextViews
            SharedGeneralPurposeMethods.textViews_enable_selected(name,followers);
        }
    }

}
