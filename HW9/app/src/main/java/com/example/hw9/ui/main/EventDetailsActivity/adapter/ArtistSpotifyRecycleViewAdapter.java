package com.example.hw9.ui.main.EventDetailsActivity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hw9.R;

import java.util.ArrayList;

public class ArtistSpotifyRecycleViewAdapter extends RecyclerView.Adapter<ArtistSpotifyRecycleViewAdapter.ViewHolder> {

    private final ArrayList<ArrayList<String>> artists_spotify_matrix;

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
        // name
        String name = artist_spotify_info.get(0);
        holder.name.setText(name);

    }

    @Override
    public int getItemCount() {
        return artists_spotify_matrix.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;


        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.artist_name);


        }
    }

}
