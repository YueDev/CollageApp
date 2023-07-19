package com.example.collageapp.diff;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

/**
 * Created by Yue on 2022/10/24.
 */
public class UriDiffCallback extends DiffUtil.ItemCallback<Uri> {

    @Override
    public boolean areItemsTheSame(@NonNull Uri oldItem, @NonNull Uri newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    public boolean areContentsTheSame(@NonNull Uri oldItem, @NonNull Uri newItem) {
        return oldItem.equals(newItem);
    }
}
