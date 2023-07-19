package com.example.collageapp.bean;

import java.util.Objects;

/**
 * Created by Yue on 2022/11/4.
 */
public class ShareBean {
    private final ShareType mShareType;
    private final int mNameResId;
    private final int mIconResId;

    public ShareBean(ShareType shareType, int nameResId, int iconResId) {
        mShareType = shareType;
        mNameResId = nameResId;
        mIconResId = iconResId;
    }

    public ShareType getShareType() {
        return mShareType;
    }

    public int getNameResId() {
        return mNameResId;
    }

    public int getIconResId() {
        return mIconResId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShareBean shareBean = (ShareBean) o;
        return mIconResId == shareBean.mIconResId && mShareType == shareBean.mShareType && Objects.equals(mNameResId, shareBean.mNameResId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mShareType, mNameResId, mIconResId);
    }
}
