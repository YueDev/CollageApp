package com.example.collageapp.diff;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.collageapp.bean.ShareBean;


/**
 * Created by Yue on 2022/10/24.
 */
public class ShareBeanDiffCallback extends DiffUtil.ItemCallback<ShareBean> {

    @Override
    public boolean areItemsTheSame(@NonNull ShareBean oldItem, @NonNull ShareBean newItem) {
        return oldItem.getShareType().equals(newItem.getShareType());
    }

    @Override
    public boolean areContentsTheSame(@NonNull ShareBean oldItem, @NonNull ShareBean newItem) {
        return oldItem.equals(newItem);
    }
}
