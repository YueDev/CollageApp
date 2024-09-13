package com.example.collageapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collageapp.R;
import com.example.collageapp.bean.GalleryBean;
import com.example.collageapp.diff.GalleryBeanDiffCallback;


/**
 * Created by Yue on 2022/10/24.
 */
public class GalleryAdapter extends ListAdapter<GalleryBean, GalleryAdapter.GalleryHolder> {

    private GalleryClickListener mClickListener;

    public void setClickListener(GalleryClickListener clickListener) {
        mClickListener = clickListener;
    }

    public GalleryAdapter() {
        super(new GalleryBeanDiffCallback());
    }

    @NonNull
    @Override
    public GalleryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        GalleryHolder holder = new GalleryHolder(view);
        holder.itemView.setOnClickListener(v -> {
            if (mClickListener == null) return;
            int position = holder.getBindingAdapterPosition();
            if (position < 0 || position >=getItemCount()) return;
            GalleryBean bean = getItem(position);
            mClickListener.onClick(bean);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryHolder holder, int position) {
        holder.bind(getItem(position));
    }


    public interface GalleryClickListener {
        void onClick(GalleryBean bean);
    }

    public static class GalleryHolder extends RecyclerView.ViewHolder {

        private final ImageView mImageView;
        private final TextView mTextView;

        public GalleryHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view_pic);
            mTextView = itemView.findViewById(R.id.text_view_num);
        }

        public void bind(GalleryBean bean) {

            Glide.with(mImageView)
                    .load(bean.getUri())
                    .into(mImageView);


            mTextView.setText(bean.getNum() + "");
            mTextView.setVisibility(bean.getNum() > 0 ? View.VISIBLE : View.GONE);
        }
    }

}
