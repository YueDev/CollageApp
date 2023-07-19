package com.example.collageapp.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collageapp.R;
import com.example.collageapp.bean.ShareBean;
import com.example.collageapp.diff.ShareBeanDiffCallback;


/**
 * Created by Yue on 2022/11/4.
 */
public class ShareAdapter extends ListAdapter<ShareBean, ShareAdapter.ShareHolder> {

    private final ShareClickListener mListener;

    public ShareAdapter(ShareClickListener listener) {
        super(new ShareBeanDiffCallback());
        mListener = listener;
    }

    @NonNull
    @Override
    public ShareHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share, parent, false);
        ShareHolder holder = new ShareHolder(itemView);
        holder.itemView.setOnClickListener(v -> {
            if (mListener == null) return;
            int position = holder.getBindingAdapterPosition();
            if (position < 0 || position >= getItemCount()) return;
            mListener.click(getItem(position));
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShareHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ShareHolder extends RecyclerView.ViewHolder {

        private final ImageView mImageView;
        private final TextView mTextView;

        public ShareHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view_share);
            mTextView = itemView.findViewById(R.id.text_view_share);
        }

        public void bind(ShareBean shareBean) {
            mImageView.setImageResource(shareBean.getIconResId());
            mTextView.setText(shareBean.getNameResId());
        }

    }

    public interface ShareClickListener {
        void click(ShareBean bean);
    }

}
