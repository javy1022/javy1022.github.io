package com.example.hw9.ui.main.EventDetailsActivity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hw9.R;

public class ArtistSpotifyRecycleViewAdapter extends RecyclerView.Adapter<ArtistSpotifyRecycleViewAdapter.ViewHolder> {


    public ArtistSpotifyRecycleViewAdapter() {

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
        String[] testValues = {"Test artist 1", "Test artist 2", "Test artist 3"};
        holder.name.setText(testValues[position]);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;


        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.artist_name);


        }
    }

}
