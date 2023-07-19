package com.example.collageapp.util;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Yue on 2022/7/11.
 */
public class LinearItemDecoration extends RecyclerView.ItemDecoration {

    int mStartPadding;
    int mEndPadding;


    public LinearItemDecoration(int startPadding, int endPadding) {
        mStartPadding = startPadding;
        mEndPadding = endPadding;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int count = state.getItemCount();

        if (position == 0) outRect.left = mStartPadding;
        if (position == count - 1) outRect.right = mEndPadding;
    }
}
