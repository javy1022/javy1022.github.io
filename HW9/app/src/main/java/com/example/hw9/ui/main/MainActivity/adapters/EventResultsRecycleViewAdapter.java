package com.example.hw9.ui.main.MainActivity.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.hw9.EventDetailsActivity;
import com.example.hw9.R;

import java.util.ArrayList;

public class EventResultsRecycleViewAdapter extends RecyclerView.Adapter<EventResultsRecycleViewAdapter.ViewHolder> {

    private final ArrayList<ArrayList<String>> event_search_results;

    public EventResultsRecycleViewAdapter(ArrayList<ArrayList<String>> event_search_results) {
        this.event_search_results = event_search_results;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_search_recycle_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ArrayList<String> event_search_result = event_search_results.get(position);

        // date
        String date = event_search_result.get(0);
        holder.date.setText(date);

        // time
        String time = event_search_result.get(1);
        holder.time.setText(time);
        // image
        String img_url = event_search_result.get(2);
        set_img(holder,img_url);

        // event name
        String name = event_search_result.get(3);
        holder.name.setText(name);

        // category
        String category = event_search_result.get(4);
        holder.category.setText(category);

        // venue
        String venue = event_search_result.get(5);
        holder.venue.setText(venue);

        // Navigate to EventDetails Activity
        CardView event_result_card = holder.itemView.findViewById(R.id.event_result_card);
        event_result_card_onClick(event_result_card, event_search_result);

    }

    @Override
    public int getItemCount() {
         return event_search_results.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView date;

        private final TextView time;
        private final ImageView img;
        private final TextView name;

        private final TextView category;
        private final TextView venue;

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.event_date);
            time = itemView.findViewById(R.id.event_time);
            img = itemView.findViewById(R.id.event_img);
            name = itemView.findViewById(R.id.event_name);
            category = itemView.findViewById(R.id.event_category);
            venue = itemView.findViewById(R.id.event_venue);

            // Fill/Empty heart icon
            ImageView heart_icon = itemView.findViewById(R.id.heart_icon);
            heart_icon_onClick(heart_icon);

            // Enable selected to make marquee effect works on TextViews
            textViews_enable_selected(name, venue, category);
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


    private static void textViews_enable_selected(TextView event_name, TextView venue, TextView category){
        event_name.setSelected(true);
        venue.setSelected(true);
        category.setSelected(true);
    }

    private static void heart_icon_onClick(ImageView heart_icon){
        heart_icon.setOnClickListener(view -> {
            ImageView imageView = (ImageView) view;
            Object tag = imageView.getTag();
            if (tag == null || tag.equals("empty")) {
                imageView.setImageResource(R.drawable.heart_filled);
                imageView.setTag("filled");
            } else {
                imageView.setImageResource(R.drawable.heart_outline);
                imageView.setTag("empty");
            }
        });

    }

    private static void event_result_card_onClick(CardView card, ArrayList<String> event_data){
        card.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), EventDetailsActivity.class);
            // data
            intent.putStringArrayListExtra("event_data", event_data);

            view.getContext().startActivity(intent);
        });

    }


}
