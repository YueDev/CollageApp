package com.example.collageapp.diff;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.collageapp.bean.GalleryBean;


/**
 * Created by Yue on 2022/10/24.
 */
public class GalleryBeanDiffCallback extends DiffUtil.ItemCallback<GalleryBean> {

    @Override
    public boolean areItemsTheSame(@NonNull GalleryBean oldItem, @NonNull GalleryBean newItem) {
        return oldItem.getUri().equals(newItem.getUri());
    }

    @Override
    public boolean areContentsTheSame(@NonNull GalleryBean oldItem, @NonNull GalleryBean newItem) {
        return oldItem.equals(newItem);
    }
}
