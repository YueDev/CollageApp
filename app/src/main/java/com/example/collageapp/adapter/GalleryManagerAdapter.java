package com.example.collageapp.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collageapp.R;
import com.example.collageapp.diff.UriDiffCallback;

/**
 * Created by Yue on 2022/10/26.
 */
public class GalleryManagerAdapter extends ListAdapter<Uri, GalleryManagerAdapter.GalleryManagerHolder> {


    public GalleryManagerAdapter() {
        super(new UriDiffCallback());
    }


    private GalleryManagerClickListener mClickListener;

    public void setClickListener(GalleryManagerClickListener clickListener) {
        mClickListener = clickListener;
    }

    @NonNull
    @Override
    public GalleryManagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery_manager, parent, false);
        GalleryManagerHolder holder = new GalleryManagerHolder(view);
        holder.mDeleteView.setOnClickListener( v -> {
            if (mClickListener == null) return;
            int position = holder.getBindingAdapterPosition();
            if (position < 0 || position >=getItemCount()) return;
            mClickListener.delete(position);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryManagerHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class GalleryManagerHolder extends RecyclerView.ViewHolder {

        private final ImageView mImageView;
        public final ImageView mDeleteView;

        public GalleryManagerHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view_pic);
            mDeleteView = itemView.findViewById(R.id.image_view_delete);
        }

        public void bind(Uri uri) {
            Glide.with(mImageView.getContext()).load(uri).into(mImageView);
        }

    }


    public interface GalleryManagerClickListener {
        void delete(int index);
    }

}
