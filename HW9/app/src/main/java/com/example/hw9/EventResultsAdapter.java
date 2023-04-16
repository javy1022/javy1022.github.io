package com.example.hw9;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

public class EventResultsAdapter extends RecyclerView.Adapter<EventResultsAdapter.ViewHolder> {

    private final String[] event_search_results;

    public EventResultsAdapter(String[] event_search_results) {
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
        String model = event_search_results[position];
        holder.itemText.setText(model);
    }

    @Override
    public int getItemCount() {
         return event_search_results.length;
    }

    //Provide a reference to the type of views that you are using

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemText;


        public ViewHolder(View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.textView4);
        }
    }
}
