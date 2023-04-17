package com.example.hw9.ui.main.MainActivity;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EventResultsDecorator extends RecyclerView.ItemDecoration {

    private final int items_bottom_margin;

    public EventResultsDecorator(int bottom_margin) {
        this.items_bottom_margin = bottom_margin;
    }
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.bottom = items_bottom_margin;
    }
}
