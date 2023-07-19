package com.example.collageapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collageapp.R;
import com.example.collageapp.bean.PicManagerBean;
import com.example.collageapp.diff.PicMangerBeanDiffCallback;

/**
 * Created by Yue on 2022/10/26.
 */
public class PicManagerAdapter extends ListAdapter<PicManagerBean, PicManagerAdapter.PicManagerHolder> {


    public PicManagerAdapter() {
        super(new PicMangerBeanDiffCallback());
    }

    private PicManagerClickListener mClickListener;

    public void setClickListener(PicManagerClickListener clickListener) {
        mClickListener = clickListener;
    }

    private boolean mShowDelete = true;

    public void setShowDelete(boolean showDelete) {
        if (mShowDelete == showDelete) return;
        mShowDelete = showDelete;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PicManagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery_manager, parent, false);
        PicManagerHolder holder = new PicManagerHolder(view);
        holder.mDeleteView.setOnClickListener(v -> {
            if (mClickListener == null) return;
            int position = holder.getBindingAdapterPosition();
            if (position < 0 || position >= getItemCount()) return;
            mClickListener.delete(getItem(position));
        });

        holder.mImageView.setOnClickListener(v -> {
            if (mClickListener == null) return;
            int position = holder.getBindingAdapterPosition();
            if (position < 0 || position >= getItemCount()) return;
            PicManagerBean bean = getItem(position);
            if (bean.isAdd()) {
                mClickListener.add();
            } else {
                mClickListener.click(bean);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PicManagerHolder holder, int position) {
        holder.bind(getItem(position), mShowDelete);
    }

    static class PicManagerHolder extends RecyclerView.ViewHolder {

        private final ImageView mImageView;
        public final ImageView mDeleteView;

        public PicManagerHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view_pic);
            mDeleteView = itemView.findViewById(R.id.image_view_delete);
        }

        public void bind(PicManagerBean picManagerBean, boolean showDelete) {
            if (picManagerBean.isAdd()) {
                Glide.with(mImageView.getContext()).load(picManagerBean.getTypeResId()).into(mImageView);
            } else if (picManagerBean.getBitmap() != null) {
                Glide.with(mImageView.getContext()).load(picManagerBean.getBitmap()).into(mImageView);
            } else {
                Glide.with(mImageView.getContext()).load(picManagerBean.getUri()).into(mImageView);
            }
            if (showDelete) {
                mDeleteView.setVisibility(picManagerBean.isAdd() ? View.GONE : View.VISIBLE);
            } else {
                mDeleteView.setVisibility(View.GONE);
            }

        }

    }


    public interface PicManagerClickListener {
        void delete(PicManagerBean bean);

        void click(PicManagerBean bean);

        void add();
    }

}
