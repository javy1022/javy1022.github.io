package com.example.hw9.ui.main.EventDetailsActivity.adapter;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hw9.R;
import com.example.hw9.SharedGeneralPurposeMethods;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class ArtistSpotifyRecycleViewAdapter extends RecyclerView.Adapter<ArtistSpotifyRecycleViewAdapter.ViewHolder> {
    private final ArrayList<ArrayList<Object>> artists_spotify_matrix;
    private SharedGeneralPurposeMethods shared;

    public ArtistSpotifyRecycleViewAdapter(ArrayList<ArrayList<Object>> artists_spotify_matrix) {
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
        ArrayList<Object> artist_spotify_info = artists_spotify_matrix.get(position);
        shared = new SharedGeneralPurposeMethods();
        // name
        String name = (String) artist_spotify_info.get(0);
        holder.name.setText(name);

        // artist spotify image
        String img_url = (String) artist_spotify_info.get(1);
        shared.set_recycle_view_iv(holder, img_url, holder.img);

        // artist followers
        String followers = (String) artist_spotify_info.get(3);
        holder.followers.setText(followers);

        // artist spotify link
        String spotify_link_url = (String) artist_spotify_info.get(4);
        holder.spotify_link.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        holder.spotify_link.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(spotify_link_url));
            v.getContext().startActivity(intent);
        });

        // spotify popularity percentage text and indicator
        String popularity_percentage_str = (String) artist_spotify_info.get(2);
        holder.popularity_percentage_str.setText(popularity_percentage_str);
        holder.popularity_pr.setProgressCompat(Integer.parseInt(popularity_percentage_str), true);

        // artist spotify album image 1
        Object artist_album_obj = artist_spotify_info.get(5);
        set_album_img(artist_album_obj, 0, holder, holder.album_img_1);

        // artist spotify album image 2
        set_album_img(artist_album_obj, 1, holder, holder.album_img_2);

        // artist spotify album image 3
        set_album_img(artist_album_obj, 2, holder, holder.album_img_3);

        // Enable selected to make marquee effect works on TextViews
        SharedGeneralPurposeMethods.textViews_enable_selected(holder.name, holder.followers, holder.spotify_link);
    }

    @Override
    public int getItemCount() {
        return artists_spotify_matrix.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageView img;
        private final TextView followers;
        private final TextView spotify_link;
        private final TextView popularity_percentage_str;
        private final CircularProgressIndicator popularity_pr;
        private final ImageView album_img_1;
        private final ImageView album_img_2;
        private final ImageView album_img_3;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.artist_name);
            img = itemView.findViewById(R.id.artist_spotify_img);
            followers = itemView.findViewById(R.id.artist_followers);
            spotify_link = itemView.findViewById(R.id.artist_spotify_link);
            popularity_percentage_str = itemView.findViewById(R.id.popularity_percentage_str);
            popularity_pr = itemView.findViewById(R.id.popularity_pb);
            album_img_1 = itemView.findViewById(R.id.album_img_1);
            album_img_2 = itemView.findViewById(R.id.album_img_2);
            album_img_3 = itemView.findViewById(R.id.album_img_3);
        }
    }

    private void set_album_img(Object artist_album_obj, int index, ViewHolder holder, ImageView artist_album_iv) {
        // generic wildcard list
        List<?> list = (List<?>) artist_album_obj;
        if (list.size() > index) {
            String img_url = (String) list.get(index);
            artist_album_iv.setVisibility(View.VISIBLE);
            shared.set_recycle_view_iv(holder, img_url, artist_album_iv);
        } else artist_album_iv.setVisibility(View.GONE);
    }

}
