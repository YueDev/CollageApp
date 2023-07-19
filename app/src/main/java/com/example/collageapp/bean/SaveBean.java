package com.example.collageapp.bean;

import android.net.Uri;

/**
 * Created by Yue on 2022/10/29.
 */
public class SaveBean {

    //保存类型 分辨率
    private final SaveType mSaveType;

    //保存状态
    private LoadingState mSaveSate;

    //分享的uri
    private Uri mShareUri;


    public SaveBean(SaveType saveType) {
        mSaveType = saveType;
        mSaveSate = LoadingState.INIT;
    }

    public Uri getShareUri() {
        return mShareUri;
    }

    public void setShareUri(Uri shareUri) {
        mShareUri = shareUri;
    }

    public SaveType getSaveType() {
        return mSaveType;
    }

    public LoadingState getSaveSate() {
        return mSaveSate;
    }

    public void setSaveSate(LoadingState saveSate) {
        mSaveSate = saveSate;
    }


}
