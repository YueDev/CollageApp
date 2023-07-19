package com.example.collageapp.util;

import android.Manifest;
import android.os.Build;

import androidx.fragment.app.FragmentActivity;

import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.util.ArrayList;

/**
 * Created by Yue on 2022/11/3.
 */
public class PermissionUtil {

    private static String getMediaPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_IMAGES : (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? Manifest.permission.READ_EXTERNAL_STORAGE : Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static void requestMediaPermission(FragmentActivity activity, RequestCallback callback) {
        PermissionX.init(activity)
                .permissions(getMediaPermission())
                .request(callback);
    }

    public static void requestWritePermission(FragmentActivity activity, RequestCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            callback.onResult(true, new ArrayList<>(), new ArrayList<>());
            return;
        }
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        PermissionX.init(activity)
                .permissions(permission)
                .request(callback);

    }

}
