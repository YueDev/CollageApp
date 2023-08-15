package com.example.collageapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.example.collageapp.R;


/**
 * Created by Yue on 2022/10/27.
 */
public class LoadingLayout extends FrameLayout {


    public LoadingLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public LoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setClickable(true);
        setFocusable(true);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, this, true);
    }

    public void setProgressText(String progressText) {

    }

    public void setProgress(int progress) {
        setProgress(progress, 100);
    }

    public void setProgress(int progress, int max) {

    }



    public void show() {
        ViewCompat.animate(this).alpha(1f).withStartAction(() -> setVisibility(View.VISIBLE));
    }

    public void hidden() {
        ViewCompat.animate(this).alpha(0f).withEndAction(() -> setVisibility(View.GONE));
    }

}
