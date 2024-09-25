package com.example.collageapp.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by Yue on 2022/11/3.
 */
public class ProViewModel extends ViewModel {

    private final MutableLiveData<Boolean> mIsRemoveWater = new MutableLiveData<>(true);

    private final MutableLiveData<Boolean> mIsProUser = new MutableLiveData<>(true);

    public MutableLiveData<Boolean> getIsProUser() {
        return mIsProUser;
    }

    public void setProUser(boolean isProUser) {
        mIsProUser.setValue(isProUser);
    }

    public boolean isProUser() {
        Boolean value = mIsProUser.getValue();
        if (value == null) value = false;
        return value;
    }



    public MutableLiveData<Boolean> isRemoveWater() {
        return mIsRemoveWater;
    }

    public void removeWater() {
        mIsRemoveWater.setValue(true);
    }
}
