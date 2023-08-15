package com.example.collageapp.repository;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.collageapp.bean.AlbumBean;
import com.example.collageapp.util.CallableFutureTask;
import com.example.collageapp.util.ShareUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;


/**
 * Created by Yue on 2022/10/24.
 */
public class GalleryRepository {

    public static final String NAME_ALL_ALBUM_BEAN = "All";

    //获取第一个uri 给allbean
    private Uri mFirstUri;

    private static final String SELECTION = MediaStore.Images.Media.WIDTH + " > 192 AND " +
            MediaStore.Images.Media.HEIGHT + " > 192";


    public CallableFutureTask<List<AlbumBean>> getAlbumList(Context context) {

        ContentResolver contentResolver = context.getContentResolver();

        Callable<List<AlbumBean>> callable = () -> {

            LinkedHashMap<Integer, AlbumBean> albumMap = new LinkedHashMap<>();

            String orderBy = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " ASC, " +
                    MediaStore.Images.Media.DATE_MODIFIED + " DESC";

            String[] projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            };

            int totalNum = 0;
            Uri allUri = null;

            try (Cursor cursor = contentResolver.query(EXTERNAL_CONTENT_URI, projection, SELECTION, null, orderBy)) {
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int bucketIdIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                        if (bucketIdIndex < 0) continue;
                        int bucketId = cursor.getInt(bucketIdIndex);

                        int bucketNameIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                        if (bucketNameIndex < 0) continue;
                        String bucketName = cursor.getString(bucketNameIndex);

                        int idIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        if (idIndex < 0) continue;
                        int id = cursor.getInt(idIndex);

                        AlbumBean albumBean = albumMap.get(bucketId);
                        if (albumBean == null) {
                            Uri uri = Uri.withAppendedPath(EXTERNAL_CONTENT_URI, id + "");
                            if (allUri == null) allUri = uri;
                            albumMap.put(bucketId, new AlbumBean(bucketId, bucketName, uri));
                        } else {
                            albumBean.add();
                        }
                        totalNum++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayList<AlbumBean> beanList = new ArrayList<>(albumMap.values());

            Uri uri = allUri;
            if (mFirstUri != null) uri = mFirstUri;

            AlbumBean allBean = new AlbumBean(0, NAME_ALL_ALBUM_BEAN, uri);
            allBean.setNum(totalNum);
            beanList.add(0, allBean);
//            AlbumBean googlePhotosBean = ShareUtil.getGooglePhotosAlbumBean(context);
//            if (googlePhotosBean != null) {
//                beanList.add(1, googlePhotosBean);
//            }
            return beanList;
        };

        return new CallableFutureTask<>(callable);
    }


    public CallableFutureTask<List<Uri>> getImageList(ContentResolver contentResolver, int alubmId) {

        Callable<List<Uri>> callable = () -> {

            List<Uri> imageUris = new ArrayList<>();

            String orderBy = MediaStore.Images.Media.DATE_MODIFIED + " DESC";

            String[] projection = new String[]{
                    MediaStore.Images.Media._ID
            };

            String selection = SELECTION;
            String[] selectionArgs = null;

            if (alubmId != 0) {
                selection = selection + " AND " + MediaStore.Images.Media.BUCKET_ID + " = ?";
                selectionArgs = new String[]{alubmId + ""};
            }

            try (Cursor cursor = contentResolver.query(EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, orderBy)) {
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int idIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        if (idIndex < 0) continue;
                        int id = cursor.getInt(idIndex);
                        Uri uri = Uri.withAppendedPath(EXTERNAL_CONTENT_URI, id + "");
                        imageUris.add(uri);
                        if (alubmId == 0 && mFirstUri == null) {
                            //记录相册第一张uri 给all album bean
                            mFirstUri = uri;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return imageUris;
        };

        return new CallableFutureTask<>(callable);

    }

}
