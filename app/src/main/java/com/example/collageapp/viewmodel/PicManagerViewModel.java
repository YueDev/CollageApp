package com.example.collageapp.viewmodel;

import android.graphics.Matrix;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.collageapp.activity.PicEditActivity;
import com.example.collageapp.bean.PicManagerBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by Yue on 2022/10/29.
 */
public class PicManagerViewModel extends ViewModel {

    private List<Uri> mUris;

    private final MutableLiveData<List<PicManagerBean>> picManagerBeans = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<PicManagerBean>> getPicManagerBeans() {
        return picManagerBeans;
    }


    public int getNum() {
        List<PicManagerBean> beans = picManagerBeans.getValue();
        if (beans == null) return 0;
        return beans.size();
    }

    //代替构造方法了，初始化走这里
    public void init(List<Uri> uris) {
        if (mUris != null) return;
        mUris = uris;
        List<PicManagerBean> beans = new ArrayList<>();
        for (Uri uri : mUris) {
            beans.add(new PicManagerBean(uri));
        }
        picManagerBeans.setValue(beans);
    }


    public void deleteBean(PicManagerBean bean) {
        List<PicManagerBean> beans = picManagerBeans.getValue();
        if (beans == null) return;
        PicManagerBean deleteBean = null;
        for (PicManagerBean item : beans) {
            if (item.isSame(bean)) {
                deleteBean = item;
                break;
            }
        }

        if (deleteBean == null) return;
        beans.remove(deleteBean);
        picManagerBeans.setValue(beans);
    }

    public void deleteAll() {
        picManagerBeans.setValue(new ArrayList<>());
    }

    public void addUris(Collection<Uri> uris) {
        if (uris == null || uris.size() == 0) return;
        List<PicManagerBean> beans = picManagerBeans.getValue();
        if (beans == null) return;
        List<PicManagerBean> adds = new ArrayList<>();
        for (Uri uri : uris) {
            adds.add(new PicManagerBean(uri));
        }

        beans.addAll(adds);
        picManagerBeans.setValue(beans);
    }

    public void changeBitmap(String key, Matrix matrix) {
        List<PicManagerBean> beans = picManagerBeans.getValue();
        if (beans == null || beans.size() == 0) return;
        PicManagerBean selectBean = null;
        for (PicManagerBean bean : beans) {
            if (bean.getId().equals(key)) {
                selectBean = bean;
                break;
            }
        }

        if (selectBean == null) return;
        selectBean.setBitmap(PicEditActivity.sResultBitmap, matrix);
        PicEditActivity.sResultBitmap = null;
        int index = beans.indexOf(selectBean);
        beans.remove(index);
        beans.add(index, selectBean.copy());
        picManagerBeans.setValue(beans);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        PicEditActivity.sResultBitmap = null;
    }
}
