package com.example.hw9;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.List;

public class AutoCompleteArrayAdapter extends ArrayAdapter<String> {

    private List<String> items;

    public AutoCompleteArrayAdapter(Context context, int textViewResourceId, List<String> items) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        textView.setTextColor(Color.parseColor("#50C31B"));
        return textView;
    }


}