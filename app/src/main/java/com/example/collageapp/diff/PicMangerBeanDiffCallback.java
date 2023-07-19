package com.example.collageapp.diff;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import com.example.collageapp.bean.PicManagerBean;

/**
 * Created by Yue on 2022/10/29.
 */
public class PicMangerBeanDiffCallback extends DiffUtil.ItemCallback<PicManagerBean> {

    @Override
    public boolean areItemsTheSame(@NonNull PicManagerBean oldItem, @NonNull PicManagerBean newItem) {
        return oldItem.isSame(newItem);
    }

    @Override
    public boolean areContentsTheSame(@NonNull PicManagerBean oldItem, @NonNull PicManagerBean newItem) {
        return oldItem.equals(newItem);
    }

}
