package com.example.hw9;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<String> {

    private List<String> items;

    public CustomArrayAdapter(Context context, int textViewResourceId, List<String> items) {
        super(context, textViewResourceId, items);
        this.items = items;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                filterResults.values = items;
                filterResults.count = items.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }
}