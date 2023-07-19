package com.example.collageapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collageapp.R;
import com.example.collageapp.bean.AlbumBean;

import java.util.List;



/**
 * Created by Yue on 2022/11/2.
 */
public class AlbumBeanAdapter extends RecyclerView.Adapter<AlbumBeanAdapter.AlbumHolder> {

    private List<AlbumBean> mAlbumBeans;

    private AlbumClickListener mAlbumClickListener;

    public void setAlbumClickListener(AlbumClickListener albumClickListener) {
        mAlbumClickListener = albumClickListener;
    }

    @NonNull
    @Override
    public AlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_bean, parent, false);
        AlbumHolder albumHolder = new AlbumHolder(view);
        albumHolder.itemView.setOnClickListener(v -> {
            if (mAlbumClickListener == null) return;
            if (mAlbumBeans == null) return;
            int position = albumHolder.getBindingAdapterPosition();
            if (position < 0 || position >= mAlbumBeans.size()) return;
            mAlbumClickListener.click(mAlbumBeans.get(position));
        });
        return albumHolder;

    }

    @Override
    public void onBindViewHolder(AlbumHolder holder, int position) {
        holder.onBind(mAlbumBeans.get(position));
    }

    @Override
    public int getItemCount() {
        return mAlbumBeans == null ? 0 : mAlbumBeans.size();
    }

    public void setAlbumBeans(List<AlbumBean> albumBeans) {
        if (albumBeans == null || albumBeans.size() == 0) return;
        mAlbumBeans = albumBeans;
        notifyDataSetChanged();
    }


    static class AlbumHolder extends RecyclerView.ViewHolder {

        private final TextView mNameTextView;
        private final TextView mNumTextView;
        private final ImageView mAlbumImageView;

        AlbumHolder(View view) {
            super(view);
            mNameTextView = view.findViewById(R.id.text_view_name);
            mNumTextView = view.findViewById(R.id.text_view_num);
            mAlbumImageView = view.findViewById(R.id.image_view_album);
        }

        void onBind(AlbumBean bean) {
            mNumTextView.setText(bean.getNum() + "");
            mNameTextView.setText(bean.getName());
            if (bean.isGooglePhotos()) {
                Glide.with(mAlbumImageView.getContext()).load(R.drawable.icon_google_photos).into(mAlbumImageView);
                mNumTextView.setVisibility(View.INVISIBLE);
            } else {
                Glide.with(mAlbumImageView.getContext()).load(bean.getUri()).into(mAlbumImageView);
                mNumTextView.setVisibility(View.VISIBLE);
            }
        }

    }

    public interface AlbumClickListener {
        void click(AlbumBean bean);
    }

}