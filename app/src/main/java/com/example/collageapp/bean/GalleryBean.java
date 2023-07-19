package com.example.collageapp.bean;

import android.net.Uri;

import java.util.Objects;

/**
 * Created by Yue on 2022/10/24.
 */
public class GalleryBean {

    private Uri mUri;
    private final int mNum;

    public GalleryBean(Uri uri, int num) {
        mUri = uri;
        mNum = num;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }

    public Uri getUri() {
        return mUri;
    }

    public int getNum() {
        return mNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GalleryBean that = (GalleryBean) o;

        if (mNum != that.mNum) return false;
        return Objects.equals(mUri, that.mUri);
    }

    @Override
    public int hashCode() {
        int result = mUri != null ? mUri.hashCode() : 0;
        result = 31 * result + mNum;
        return result;
    }

}
