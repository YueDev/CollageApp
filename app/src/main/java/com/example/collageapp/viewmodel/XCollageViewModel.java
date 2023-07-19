package com.example.collageapp.viewmodel;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.collageapp.application.App;
import com.example.collageapp.bean.BorderBean;
import com.example.collageapp.bean.LoadingResult;
import com.example.collageapp.bean.PicManagerBean;
import com.example.collageapp.bean.RatioBean;
import com.example.collageapp.util.ImageUtil;
import com.example.collageapp.util.ResourcesUtil;
import com.example.collageapp.x_collage.XBitmapSimple;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yue on 2022/10/27.
 */
public class XCollageViewModel extends ViewModel {

    private List<Uri> mUris;

    private int mSize = 256;

    //temp
    private final List<XBitmapSimple> _bitmaps = new ArrayList<>();


    private final List<RatioBean> mRatioBeans = ResourcesUtil.getRatioBeans();


    private final List<BorderBean> mBorderBeans = ResourcesUtil.getBorderBeans();
    private final RatioBean mDefaultRatio = mRatioBeans.get(1);
    private final BorderBean mDefaultBorderBean = mBorderBeans.get(0);

    //拼图的数据 XBitmapSimple 包含着bitmap和uri，String是信息，传加载进度用的
    private final MutableLiveData<LoadingResult<List<XBitmapSimple>, String>> mData = new MutableLiveData<>();

    //比例
    private final MutableLiveData<RatioBean> mRatio = new MutableLiveData<>(mDefaultRatio);

    //边距
    private final MutableLiveData<BorderBean> mBorder = new MutableLiveData<>(mDefaultBorderBean);


    //代替构造方法了，初始化走这里
    public void init(List<Uri> uris, int size) {
        if (mUris != null) return;
        mUris = uris;
        mSize = size;
        _bitmaps.clear();
        loadImage(mUris, 0, mUris.size());
    }

    public List<Uri> getUris() {
        return mUris;
    }


    public LiveData<LoadingResult<List<XBitmapSimple>, String>> getData() {
        return mData;
    }

    public LiveData<RatioBean> getRatio() {
        return mRatio;
    }

    public MutableLiveData<BorderBean> getBorder() {
        return mBorder;
    }


    private void loadImage(List<Uri> uris, int index, int sum) {
        //开始加载
        if (index == 0) {
            LoadingResult<List<XBitmapSimple>, String> init = LoadingResult.Init();
            mData.setValue(init);
        }

        //加载完成
        if (index >= sum) {
            LoadingResult<List<XBitmapSimple>, String> success = LoadingResult.Success(_bitmaps);
            mData.setValue(success);
            return;
        }

        LoadingResult<List<XBitmapSimple>, String> loading = LoadingResult.Loading(index + 1 + "/" + sum);
        mData.setValue(loading);

        Uri uri = uris.get(index);

        ImageUtil.loadBitmapFromGlide(App.sAppContext, uri, mSize, mSize, new ImageUtil.SimpleGlideListener() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                XBitmapSimple xBitmapSimple = new XBitmapSimple(uri, bitmap);
                _bitmaps.add(xBitmapSimple);
                loadImage(uris, index + 1, sum);
            }

            @Override
            public void onError(String errorMessage) {
                LoadingResult<List<XBitmapSimple>, String> error = LoadingResult.Error(errorMessage);
                mData.setValue(error);
            }
        });

    }


    public RatioBean getCurrentRatio() {
        RatioBean value = mRatio.getValue();
        if (value == null) return mDefaultRatio;
        return value;
    }

    public BorderBean getCurrentBorder() {
        BorderBean value = mBorder.getValue();
        if (value == null) return mDefaultBorderBean;
        return value;
    }

    public RatioBean getDefaultRatio() {
        return mRatioBeans.get(0);
    }

    public List<RatioBean> getRatioBeans() {
        return mRatioBeans;
    }

    public List<BorderBean> getBorderBeans() {
        return mBorderBeans;
    }

    public void setRatio(RatioBean ratioBean) {
        mRatio.setValue(ratioBean);
    }

    public void setBorder(BorderBean borderBean) {
        mBorder.setValue(borderBean);
    }

    //边框 点击一次换下一个
    public BorderBean getNextBorder() {
        BorderBean currentBorder = getCurrentBorder();
        int nextIndex = (mBorderBeans.indexOf(currentBorder) + 1) % mBorderBeans.size();
        return mBorderBeans.get(nextIndex);
    }


    //图片管理
    public void changeUris(List<PicManagerBean> picManagerBeans) {
        if (picManagerBeans == null || picManagerBeans.size() == 0) return;
        //判断uri是否更改
        boolean hasChanged = true;
        if (picManagerBeans.size() == mUris.size()) {
            hasChanged = false;
            for (int i = 0; i < picManagerBeans.size(); i++) {
                PicManagerBean bean = picManagerBeans.get(i);
                Uri oldUri = mUris.get(i);
                if (bean.getBitmap() != null || !bean.getUri().equals(oldUri)) {
                    hasChanged = true;
                    break;
                }
            }
        }

        if (!hasChanged) return;
        List<Uri> newUris = new ArrayList<>();
        for (PicManagerBean bean : picManagerBeans) {
            newUris.add(bean.getUri());
        }
        mUris = newUris;
        mSize = ImageUtil.getImageSize(mUris.size());
        _bitmaps.clear();
        loadImageWithPicManagerBeans(picManagerBeans, 0, picManagerBeans.size());
    }



    //根据图片管理的数据来重新加载bitmap
    private void loadImageWithPicManagerBeans(List<PicManagerBean> beans, int index, int sum) {

        //加载完成
        if (index >= sum) {
            LoadingResult<List<XBitmapSimple>, String> success = LoadingResult.Success(_bitmaps);
            mData.setValue(success);
            return;
        }

        //开始加载
        if (index == 0) {
            LoadingResult<List<XBitmapSimple>, String> init = LoadingResult.Init();
            mData.setValue(init);
        }


        LoadingResult<List<XBitmapSimple>, String> loading = LoadingResult.Loading(index + 1 + "/" + sum);
        mData.setValue(loading);

        PicManagerBean bean = beans.get(index);
        Uri uri = bean.getUri();
        if (bean.getBitmap() != null) {
            //加载bitmap
            ImageUtil.loadBitmapFromGlide(App.sAppContext, bean.getBitmap(), mSize, mSize, new ImageUtil.SimpleGlideListener() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    XBitmapSimple xBitmapSimple = new XBitmapSimple(uri, bitmap);
                    xBitmapSimple.setChanged(true, bean.getMatrix());
                    _bitmaps.add(xBitmapSimple);
                    loadImageWithPicManagerBeans(beans, index + 1, sum);
                }

                @Override
                public void onError(String errorMessage) {
                    LoadingResult<List<XBitmapSimple>, String> error = LoadingResult.Error(errorMessage);
                    mData.setValue(error);
                }
            });
        } else {
            //加载uri
            ImageUtil.loadBitmapFromGlide(App.sAppContext, uri, mSize, mSize, new ImageUtil.SimpleGlideListener() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    XBitmapSimple xBitmapSimple = new XBitmapSimple(uri, bitmap);
                    _bitmaps.add(xBitmapSimple);
                    loadImageWithPicManagerBeans(beans, index + 1, sum);
                }

                @Override
                public void onError(String errorMessage) {
                    LoadingResult<List<XBitmapSimple>, String> error = LoadingResult.Error(errorMessage);
                    mData.setValue(error);
                }
            });
        }


    }

}
