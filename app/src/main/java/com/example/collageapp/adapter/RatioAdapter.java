package com.example.collageapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collageapp.R;
import com.example.collageapp.bean.RatioBean;

import java.util.List;



/**
 * Created by Yue on 2022/10/28.
 */
public class RatioAdapter extends RecyclerView.Adapter<RatioAdapter.RatioHolder> {

    private List<RatioBean> mRatioBeans;
    private int mSelectIndex = 1;

    private RatioClickListener mClickListener;

    public void setClickListener(RatioClickListener clickListener) {
        mClickListener = clickListener;
    }

    @NonNull
    @Override
    public RatioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ratio_bean, parent, false);
        RatioHolder ratioHolder = new RatioHolder(view);
        ratioHolder.itemView.setOnClickListener(v -> {
            if (mClickListener == null) return;
            if (mRatioBeans == null || mRatioBeans.size() == 0) return;
            int position = ratioHolder.getBindingAdapterPosition();
            if (position < 0 || position >= mRatioBeans.size()) return;
            mClickListener.clickRatio(mRatioBeans.get(position));
            //直接view model调用setSelectIndex修改了
//                setSelectIndex(position);

        });
        return ratioHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RatioHolder holder, int position) {
        if (mRatioBeans == null) return;
        holder.bind(mRatioBeans.get(position), position == mSelectIndex);
    }

    @Override
    public int getItemCount() {
        return mRatioBeans == null ? 0 : mRatioBeans.size();
    }


    public void setRatioBeans(List<RatioBean> ratioBeans) {
        mRatioBeans = ratioBeans;
        notifyDataSetChanged();
    }

    public void setSelectIndex(int selectIndex) {
        int old = mSelectIndex;
        mSelectIndex = selectIndex;
        if (old != mSelectIndex) {
            notifyItemChanged(old);
            notifyItemChanged(mSelectIndex);
        }
    }

    public void setSelectIndex(RatioBean bean) {
        if (mRatioBeans == null || bean == null) return;
        int i = mRatioBeans.indexOf(bean);
        setSelectIndex(i);
    }



    static class RatioHolder extends RecyclerView.ViewHolder {

        private final ImageView mImageView;
        private final View mSelectView;

        public RatioHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view_ratio_bean);
            mSelectView = itemView.findViewById(R.id.view_select);
        }

        public void bind(RatioBean ratioBean, boolean isSelect) {
            mImageView.setImageResource(ratioBean.getIconResId());
            mSelectView.setVisibility(isSelect ? View.VISIBLE : View.GONE);
        }
    }


    public interface RatioClickListener {
        void clickRatio(RatioBean ratioBean);
    }

}
