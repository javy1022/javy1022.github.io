package com.example.hw9;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class EventResultsAdapter extends RecyclerView.Adapter<EventResultsAdapter.ViewHolder> {

    private final ArrayList<ArrayList<String>> event_search_results;

    public EventResultsAdapter(ArrayList<ArrayList<String>> event_search_results) {
        this.event_search_results = event_search_results;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_recycle_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ArrayList<String> event_search_result = event_search_results.get(position);

        // date
        String date = event_search_result.get(0);
        holder.date.setText(date);
        // image
        String img_url = event_search_result.get(2);
        set_img(holder,img_url);
    }

    @Override
    public int getItemCount() {
         return event_search_results.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView date;
        private final ImageView img;


        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.event_date);
            img = itemView.findViewById(R.id.event_img);
        }
    }

    private void set_img(ViewHolder holder, String img_url){
        MultiTransformation<Bitmap> transformations = new MultiTransformation<>(
                new CenterCrop(),
                new RoundedCorners(15)
        );
        RequestOptions request_options = new RequestOptions()
                .override(325, 325)
                .transform(transformations);

        Glide.with(holder.itemView.getContext())
                .load(img_url)
                .apply(request_options)
                .into(holder.img);
    }
}
