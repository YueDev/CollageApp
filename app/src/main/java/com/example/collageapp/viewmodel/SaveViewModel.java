package com.example.collageapp.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.collageapp.bean.LoadingState;
import com.example.collageapp.bean.SaveBean;
import com.example.collageapp.bean.SaveType;


/**
 * Created by Yue on 2022/10/29.
 */
public class SaveViewModel extends ViewModel {

    private final SaveBean mSaveBeanLow = new SaveBean(SaveType.LOW);
    private final SaveBean mSaveBeanNormal = new SaveBean(SaveType.NORMAL);
    private final SaveBean mSaveBeanHigh = new SaveBean(SaveType.HIGH);

    private final MutableLiveData<SaveBean> mSaveBean = new MutableLiveData<>(mSaveBeanLow);

    public SaveBean getSaveBeanLow() {
        return mSaveBeanLow;
    }

    public SaveBean getSaveBeanNormal() {
        return mSaveBeanNormal;
    }

    public SaveBean getSaveBeanHigh() {
        return mSaveBeanHigh;
    }

    public LiveData<SaveBean> getSaveBean() {
        return mSaveBean;
    }


    //重置save bean
    public void reset() {
        mSaveBeanLow.setSaveSate(LoadingState.INIT);
        mSaveBeanLow.setShareUri(null);
        mSaveBeanNormal.setSaveSate(LoadingState.INIT);
        mSaveBeanLow.setShareUri(null);
        mSaveBeanHigh.setSaveSate(LoadingState.INIT);
        mSaveBeanLow.setShareUri(null);

        mSaveBean.setValue(mSaveBeanLow);
    }

    public void setLow() {
        mSaveBean.setValue(mSaveBeanLow);
    }

    public void setNormal() {
        mSaveBean.setValue(mSaveBeanNormal);
    }

    public void setHigh() {
        mSaveBean.setValue(mSaveBeanHigh);
    }



    public LoadingState getSaveState() {
        SaveBean value = mSaveBean.getValue();
        if (value == null) return LoadingState.LOADING;
        return value.getSaveSate();
    }

    public void setSaveState(LoadingState state) {
        SaveBean value = mSaveBean.getValue();
        if (value == null) return;
        value.setSaveSate(state);
        mSaveBean.setValue(value);
    }


    public void postSaveState(LoadingState state) {
        SaveBean value = mSaveBean.getValue();
        if (value == null) return;
        value.setSaveSate(state);
        mSaveBean.postValue(value);
    }

    public int getSaveSize() {
        SaveBean value = mSaveBean.getValue();
        if (value == null) return 1080;
        switch (value.getSaveType()) {
            case LOW:
                return 1080;
            case NORMAL:
                return 2160;
            case HIGH:
                return 4096;
        }
        return 1080;
    }

    //设置保存的Uri
    public void setSaveUri(Uri uri) {
        SaveBean value = mSaveBean.getValue();
        if (value == null) return;
        value.setShareUri(uri);
    }

    public Uri getSaveUri() {
        SaveBean value = mSaveBean.getValue();
        if (value == null) return null;
        return value.getShareUri();
    }
}
