package com.example.collageapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collageapp.R;
import com.example.collageapp.adapter.RatioAdapter;
import com.example.collageapp.bean.RatioBean;
import com.example.collageapp.util.LinearItemDecoration;
import com.example.collageapp.util.SizeUtil;

import java.util.List;


/**
 * Created by Yue on 2022/10/27.
 */
public class CollageButtonView extends FrameLayout {

    private View mCollageButton;
    private ImageView mRatioButton;
    private ImageView mBorderButton;

    private RecyclerView mRatioRecyclerView;
    private RatioAdapter mRatioAdapter;

    private View mRatioOkButton;

    private View mButtonLayout;
    private View mRatioLayout;

    private ClickListener mClickListener;

    public void setClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public CollageButtonView(@NonNull Context context) {
        super(context);
        init();
    }

    public CollageButtonView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CollageButtonView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setFocusable(true);
        setClickable(true);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_collage_button, this, true);

        mButtonLayout = findViewById(R.id.layout_button);
        mRatioLayout = findViewById(R.id.layout_ratio);

        mCollageButton = findViewById(R.id.image_view_collage);
        mRatioButton = findViewById(R.id.image_view_ratio);

        mBorderButton = findViewById(R.id.image_view_border);


        mRatioRecyclerView = findViewById(R.id.recycler_view_ratio);
        mRatioRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        int padding = SizeUtil.dp2px(6);
        mRatioRecyclerView.addItemDecoration(new LinearItemDecoration(padding, padding));


        mRatioAdapter = new RatioAdapter();
        mRatioRecyclerView.setAdapter(mRatioAdapter);
        mRatioAdapter.setClickListener(ratioBean -> {
            if (mClickListener != null) mClickListener.clickRatio(ratioBean);
        });

        mRatioOkButton = findViewById(R.id.image_view_ratio_ok);

        post(this::initClick);

    }

    private void initClick() {

        mRatioLayout.setTranslationY(getMeasuredHeight());
        mRatioLayout.setVisibility(View.VISIBLE);

        mCollageButton.setOnClickListener(v -> {
            if (mClickListener != null) mClickListener.clickCollageButton();
        });


        mBorderButton.setOnClickListener(v -> {
            if (mClickListener != null) mClickListener.clickBorderButton();
        });

        mRatioButton.setOnClickListener(v -> showRatio());

        mRatioOkButton.setOnClickListener(v -> hiddenRatio());
    }

    public void setRatioBeans(List<RatioBean> ratioBeans) {
        if (mRatioAdapter == null) return;
        mRatioAdapter.setRatioBeans(ratioBeans);
    }


    private void showRatio() {
        int h = getMeasuredHeight();
        if (h == 0) return;
        if (mButtonLayout.getTranslationY() != 0) return;

        ViewCompat.animate(mButtonLayout).translationY(h);
        ViewCompat.animate(mRatioLayout).translationY(0);
    }


    public void hiddenRatio() {
        int h = getMeasuredHeight();
        if (h == 0) return;
        if (mRatioLayout.getTranslationY() != 0) return;

        ViewCompat.animate(mRatioLayout).translationY(h);
        ViewCompat.animate(mButtonLayout).translationY(0);
    }

    public boolean tryHiddenRatio() {
        int h = getMeasuredHeight();
        if (h == 0) return false;
        boolean needHidden = mRatioLayout.getTranslationY() == 0;
        if (needHidden) hiddenRatio();
        return needHidden;
    }

    public void setRatioResId(RatioBean bean) {
        mRatioButton.setImageResource(bean.getIconResId());
        if (mRatioAdapter == null) return;
        mRatioAdapter.setSelectIndex(bean);
    }

    public void setBorderResId(int iconResId) {
        mBorderButton.setImageResource(iconResId);
    }


    public interface ClickListener {
        void clickCollageButton();

        void clickBorderButton();

        void clickRatio(RatioBean ratioBean);
    }


}
