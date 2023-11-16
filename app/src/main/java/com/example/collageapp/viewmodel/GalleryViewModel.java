package com.example.collageapp.viewmodel;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.collageapp.bean.AlbumBean;
import com.example.collageapp.bean.GalleryBean;
import com.example.collageapp.repository.GalleryRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Yue on 2022/10/25.
 */
public class GalleryViewModel extends ViewModel {

    private final GalleryRepository mRepository = new GalleryRepository();

    //选中图片
    private final MutableLiveData<List<Uri>> mSelectUris = new MutableLiveData<>(new ArrayList<>());
    private final HashMap<Uri, Integer> mSelectMap = new HashMap<>();

    //专辑
    private MutableLiveData<List<AlbumBean>> mAlbumBeans;

    //图片  boolean表示更新完图片后是否rv调到首部
    private MutableLiveData<Pair<List<GalleryBean>, Boolean>> mGalleryBeans;

    //当前显示专辑名称
    private final MutableLiveData<String> mAlbumName = new MutableLiveData<>(GalleryRepository.NAME_ALL_ALBUM_BEAN);


    public LiveData<List<Uri>> getSelectUris() {
        return mSelectUris;
    }

    public int getSelectNum() {
        List<Uri> uris = mSelectUris.getValue();
        if (uris == null) return 0;
        return uris.size();
    }


    public LiveData<List<AlbumBean>> getAlbumBeans(Context context) {
        if (mAlbumBeans == null) {
            mAlbumBeans = new MutableLiveData<>(new ArrayList<>());
            loadAlbumBeans(context);
        }
        return mAlbumBeans;
    }

    public LiveData<Pair<List<GalleryBean>, Boolean>> getGalleryBeans(ContentResolver contentResolver) {
        if (mGalleryBeans == null) {
            Pair<List<GalleryBean>, Boolean> pair = new Pair<>(
                    new ArrayList<>(), false
            );
            mGalleryBeans = new MutableLiveData<>(pair);
            loadAllGalleryBeans(contentResolver);
        }
        return mGalleryBeans;
    }

    public LiveData<String> getAlbumName() {
        return mAlbumName;
    }

    //读取专辑
    private void loadAlbumBeans(Context context) {
        mRepository.getAlbumList(context).execute(mAlbumBeans::postValue);
    }


    //读取图片
    public void loadGalleryBeans(ContentResolver contentResolver, int albumId) {
        mRepository.getImageList(contentResolver, albumId).execute(uris -> {
            List<GalleryBean> beans = new ArrayList<>();
            for (Uri uri : uris) {
                Integer num = mSelectMap.get(uri);
                if (num == null) num = 0;
                GalleryBean bean = new GalleryBean(uri, num);
                beans.add(bean);
            }

            mGalleryBeans.postValue(new Pair<>(beans, true));
        });
    }

    //读取所有图片
    private void loadAllGalleryBeans(ContentResolver contentResolver) {
        loadGalleryBeans(contentResolver, 0);
    }


    //更换专辑
    public void changeAlbum(ContentResolver contentResolver, AlbumBean bean) {
        if (bean == null) return;
        mAlbumName.setValue(bean.getName());
        loadGalleryBeans(contentResolver, bean.getId());
    }

    //添加一张选择的图片
    public void addUri(GalleryBean bean) {
        if (bean == null) return;
        Uri uri = bean.getUri();
        List<Uri> uris = mSelectUris.getValue();
        if (uris == null) uris = new ArrayList<>();
        uris.add(uri);

        Integer integer = mSelectMap.get(uri);
        if (integer == null) integer = 0;
        integer++;
        mSelectMap.put(uri, integer);
        mSelectUris.setValue(uris);


        Pair<List<GalleryBean>, Boolean> pair = mGalleryBeans.getValue();
        if (pair == null) return;
        List<GalleryBean> beans = pair.first;
        if (beans == null) return;
        int index = beans.indexOf(bean);
        if (index >= 0) {
            List<GalleryBean> newBeans = new ArrayList<>(beans);
            GalleryBean newBean = new GalleryBean(bean.getUri(), bean.getNum() + 1);
            newBeans.remove(index);
            newBeans.add(index, newBean);
            mGalleryBeans.setValue(new Pair<>(newBeans, false));
        }
    }

    //外部添加多张图片 不用判断gallery bean
    public void addOutUris(List<Uri> uris) {
        if (uris == null || uris.size() == 0) return;

        //更新选择的数量
        for (Uri uri : uris) {
            Integer integer = mSelectMap.get(uri);
            if (integer == null) integer = 0;
            integer++;
            mSelectMap.put(uri, integer);
        }

        //更新选择的uri
        List<Uri> selectUris = mSelectUris.getValue();
        if (selectUris == null) selectUris = new ArrayList<>();
        selectUris.addAll(uris);
        mSelectUris.setValue(selectUris);

        //不更新GalleryBean了
    }


    //从所选的list里删除一张图片，返回值是删除后是否为空
    public boolean delete(int index) {
        List<Uri> uris = mSelectUris.getValue();
        if (uris == null || uris.size() == 0) return true;
        if (index < 0 || index >= uris.size()) return false;

        Uri uri = uris.remove(index);
        Integer integer = mSelectMap.get(uri);
        if (integer != null && integer > 0) {
            integer--;
            mSelectMap.put(uri, integer);
            mSelectUris.setValue(uris);
        }

        Pair<List<GalleryBean>, Boolean> pair = mGalleryBeans.getValue();
        if (pair == null) return false;
        List<GalleryBean> beans = pair.first;
        if (beans == null) return false;


        GalleryBean deleteBean = null;
        for (GalleryBean bean : beans) {
            if (bean.getUri().equals(uri) && bean.getNum() > 0) {
                deleteBean = bean;
                break;
            }
        }

        if (deleteBean != null) {
            int deleteIndex = beans.indexOf(deleteBean);
            if (deleteIndex >= 0) {
                List<GalleryBean> newBeans = new ArrayList<>(beans);
                GalleryBean newBean = new GalleryBean(deleteBean.getUri(), deleteBean.getNum() - 1);
                newBeans.remove(deleteIndex);
                newBeans.add(deleteIndex, newBean);
                mGalleryBeans.setValue(new Pair<>(newBeans, false));
            }
        }

        return uris.size() == 0;
    }


    public void deleteAll() {
        mSelectUris.setValue(new ArrayList<>());
        mSelectMap.clear();

        Pair<List<GalleryBean>, Boolean> pair = mGalleryBeans.getValue();
        if (pair == null) return;
        List<GalleryBean> beans = pair.first;
        if (beans == null) return;

        List<GalleryBean> newBeans = new ArrayList<>();
        for (GalleryBean bean : beans) {
            GalleryBean newBean = new GalleryBean(bean.getUri(), 0);
            newBeans.add(newBean);
        }

        mGalleryBeans.setValue(new Pair<>(newBeans, true));

    }


    //重置相册的选择
    public void reset(ContentResolver contentResolver) {
        mSelectUris.setValue(new ArrayList<>());
        mSelectMap.clear();

        List<AlbumBean> albumBeans = mAlbumBeans.getValue();
        if (albumBeans == null || albumBeans.isEmpty()) return;
        changeAlbum(contentResolver, albumBeans.get(0));
    }
}
