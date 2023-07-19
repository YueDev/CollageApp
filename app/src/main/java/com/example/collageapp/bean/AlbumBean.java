package com.example.collageapp.bean;

import android.net.Uri;

/**
 * Created by Yue on 2022/10/24.
 */
public class AlbumBean {

    private static final String NAME_GOOGLE_PHOTOS = "Google Photos";

    private final int mId;
    private final String mName;

    private final Uri mUri;

    private int mNum;


    public boolean isGooglePhotos() {
        return mId == 0 && NAME_GOOGLE_PHOTOS.equals(mName);
    }

    public static AlbumBean GooglePhotoBean() {
        return new AlbumBean(0, NAME_GOOGLE_PHOTOS, null);
    }


    public AlbumBean(int id, String name, Uri uri) {
        mId = id;
        mName = name;
        mUri = uri;
        mNum = 1;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Uri getUri() {
        return mUri;
    }

    public int getNum() {
        return mNum;
    }

    public void add() {
        mNum++;
    }

    public void setNum(int num) {
        mNum = num;
    }
}
