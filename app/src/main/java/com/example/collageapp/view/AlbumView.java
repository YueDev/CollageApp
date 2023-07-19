package com.example.collageapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collageapp.R;
import com.example.collageapp.adapter.AlbumBeanAdapter;
import com.example.collageapp.bean.AlbumBean;

import java.util.List;



/**
 * Created by Yue on 2022/11/2.
 */
public class AlbumView extends FrameLayout {


    private View mAlbumListLayout;
    private View mBgView;
    private RecyclerView mRecyclerView;
    private AlbumBeanAdapter mAdapter;

    private ClickListener mClickListener;


    public AlbumView(@NonNull Context context) {
        super(context);
        init();
    }

    public AlbumView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AlbumView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_album, this, true);

        mAlbumListLayout = findViewById(R.id.layout_album_list);
        mBgView = findViewById(R.id.view_album_bg);
        mBgView.setOnClickListener(v -> hidden());

        mRecyclerView = findViewById(R.id.recycler_view_album);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new AlbumBeanAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setAlbumClickListener(bean -> {
            if (mClickListener != null) mClickListener.clickBean(bean);
        });
    }

    public void setData(List<AlbumBean> beans, ClickListener clickListener) {
        mAdapter.setAlbumBeans(beans);
        mClickListener = clickListener;
        mAlbumListLayout.post(() -> {
            mAlbumListLayout.setTranslationY(-mAlbumListLayout.getMeasuredHeight());
            mAlbumListLayout.setVisibility(View.VISIBLE);
        });

    }

    public void show() {
        mRecyclerView.setVisibility(View.VISIBLE);
        ViewCompat.animate(mBgView).alpha(1f).withStartAction(() -> mBgView.setVisibility(View.VISIBLE));
        ViewCompat.animate(mAlbumListLayout).translationY(0);
        if (mClickListener != null) mClickListener.onShow();
    }

    public void hidden() {
        ViewCompat.animate(mBgView).alpha(0f).withEndAction(() -> mBgView.setVisibility(View.GONE));
        ViewCompat.animate(mAlbumListLayout).translationY(-mAlbumListLayout.getMeasuredHeight());
        if (mClickListener != null) mClickListener.onHidden();
    }


    public boolean isNeedHidden() {
        return mBgView.getVisibility() == View.VISIBLE;
    }

    public interface ClickListener {
        void onHidden();

        void onShow();

        void clickBean(AlbumBean bean);
    }

}
