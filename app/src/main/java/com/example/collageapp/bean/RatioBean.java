package com.example.collageapp.bean;


import com.example.collageapp.R;

/**
 * Created by Yue on 2022/10/27.
 */
public class RatioBean {
    private int mW;
    private int mH;
    private final int mIconResId;

    public RatioBean(int w, int h, int iconResId) {
        mW = w;
        mH = h;
        mIconResId = iconResId;
    }

    public int getW() {
        return mW;
    }

    public int getH() {
        return mH;
    }

    public void setW(int w) {
        mW = w;
    }

    public void setH(int h) {
        mH = h;
    }

    public int getIconResId() {
        return mIconResId;
    }

    public float getRatio() {
        return (float) mW / mH;
    }

    public boolean isCustom() {
        return mIconResId == R.drawable.icon_ratio_custom;
    }
}
