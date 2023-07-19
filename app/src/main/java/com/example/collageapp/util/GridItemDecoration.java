package com.example.collageapp.util;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Yue on 2022/7/11.
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {

    int mBottomPadding;

    int mSpanCount;

    public GridItemDecoration(int bottomPadding, int spanCount) {
        mBottomPadding = bottomPadding;
        mSpanCount = spanCount;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int count = state.getItemCount();

        if (count < 5) return;


        int position = parent.getChildAdapterPosition(view);

        //计算最后一行需要加padding
        int rem = count % mSpanCount;
        if (rem == 0) rem += mSpanCount;
        int from = count - rem;
        int to = count - 1;
        if (position >= from && position <= to) outRect.bottom = mBottomPadding;
    }
}
