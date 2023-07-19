package com.example.collageapp.bean;

/**
 * Created by Yue on 2022/10/27.
 */
public class BorderBean {
    private final float mInnerPadding;
    private final float mOuterPadding;
    private final int mIconResId;


    public BorderBean(float innerPadding, float outerPadding, int iconResId) {
        mInnerPadding = innerPadding;
        mOuterPadding = outerPadding;
        mIconResId = iconResId;
    }

    public float getInnerPadding() {
        return mInnerPadding;
    }

    public float getOuterPadding() {
        return mOuterPadding;
    }

    public int getIconResId() {
        return mIconResId;
    }
}
