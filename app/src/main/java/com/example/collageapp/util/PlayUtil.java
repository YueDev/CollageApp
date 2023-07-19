package com.example.collageapp.util;

import android.content.ContentValues;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by Yue on 2022/11/1.
 * fotoplay的各种util
 */
public class PlayUtil {

    /**
     * copy from EditorBaseActivity
     * 保存到视频到本地，并插入MediaStore以保证相册可以查看到
     * 这是更优化的方法，防止读取的视频获取不到宽高
     * <p>
     * 改成保存图片的了
     */
    public static void insertPictureMediaStore(Context context, String filePath, boolean isPNG, MediaScannerConnection.OnScanCompletedListener callback) {
        if (!new File(filePath).exists())
            return;
        try {
            ContentValues values = initCommonContentValues(filePath);
            values.put(MediaStore.MediaColumns.MIME_TYPE, isPNG ? "image/png" : "image/jpeg");
            context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            MediaScannerConnection.scanFile(
                    context, new String[]{filePath}, null, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static ContentValues initCommonContentValues(String filePath) {
        ContentValues values = new ContentValues();
        File saveFile = new File(filePath);
        long timeMillis = System.currentTimeMillis();
        values.put(MediaStore.MediaColumns.TITLE, saveFile.getName());
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, saveFile.getName());
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, timeMillis / 1000);
        values.put(MediaStore.MediaColumns.DATE_ADDED, timeMillis / 1000);
        values.put(MediaStore.MediaColumns.DATA, saveFile.getAbsolutePath());
        values.put(MediaStore.Images.Media.DESCRIPTION, "XCollage");
        return values;
    }


}
