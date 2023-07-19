package com.example.collageapp.bean;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;

import com.example.collageapp.R;

import java.util.Objects;
import java.util.UUID;


/**
 * Created by Yue on 2022/10/29.
 */
public class PicManagerBean {

    private final String mId = UUID.randomUUID().toString();

    //图片的uri
    private final Uri mUri;

    //编辑过的bitmap 如果为空则是未编辑过
    private Bitmap mBitmap;
    private Matrix mMatrix;

    //bean的类型  pic是图片 add是添加
    private final String mType;
    //如果特殊bean类型需要的资源id
    private int mTypeResId;

    public PicManagerBean(Uri uri) {
        this(uri, "pic");
    }

    private PicManagerBean(Uri uri, String type) {
        mUri = uri;
        mType = type;
    }

    private PicManagerBean(String type, int typeResId) {
        this(null, type);
        mTypeResId = typeResId;
    }

    private static PicManagerBean sAddBean;

    public static PicManagerBean getAddBean() {
        if (sAddBean == null) {
            sAddBean = new PicManagerBean("add", R.drawable.icon_pic_add);
        }
        return sAddBean;
    }

    public boolean isAdd() {
        return mType.equals("add");
    }

    public Uri getUri() {
        return mUri;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap, Matrix matrix) {
        mBitmap = bitmap;
        mMatrix = matrix;
    }

    public Matrix getMatrix() {
        return mMatrix;
    }

    public int getTypeResId() {
        return mTypeResId;
    }


    public boolean isSame(PicManagerBean bean) {
        return mId.equals(bean.mId);
    }

    public String getId() {
        return mId;
    }

    public PicManagerBean copy() {
        PicManagerBean copy = new PicManagerBean(mUri);
        copy.setBitmap(mBitmap, mMatrix);
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PicManagerBean that = (PicManagerBean) o;

        if (mTypeResId != that.mTypeResId) return false;
        if (!Objects.equals(mId, that.mId)) return false;
        if (!Objects.equals(mUri, that.mUri)) return false;
        return Objects.equals(mType, that.mType);
    }

    @Override
    public int hashCode() {
        int result = mId.hashCode();
        result = 31 * result + (mUri != null ? mUri.hashCode() : 0);
        result = 31 * result + (mType != null ? mType.hashCode() : 0);
        result = 31 * result + mTypeResId;
        return result;
    }
}
