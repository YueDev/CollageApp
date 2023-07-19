package com.example.collageapp.util;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * Created by Yue on 2022/10/25.
 */
public class ImageUtil {

    public static int sMaxNum = 120;

    public static int sScreenSize = 720;



    public static int getImageSize(Application app, int imageNum) {

        int size = (int) Math.min(Math.sqrt(2048.0 * 2048.0 / imageNum), 1440.0);

        int screenSize = Math.min(SizeUtil.getScreenWidth(app), SizeUtil.getScreenHeight(app));
        if (screenSize > 0) {
            sScreenSize = screenSize;
            if (screenSize < 900) size /= 1.5;
        }

        return size;
    }

    //上边方法走完后使用比较好
    public static int getImageSize(int imageNum) {
        int size = (int) Math.min(Math.sqrt(2048.0 * 2048.0 / imageNum), 1440.0);
        if (sScreenSize > 0 && sScreenSize < 900) {
            size /= 1.5;
        }
        return size;
    }


    public static Bitmap loadBitmapSync(Context context, Uri uri, int width, int height) {
        Bitmap bitmap = null;
        try {
            FutureTarget<Bitmap> future = Glide.with(context)
                    .asBitmap()
                    .load(uri)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .centerInside()
                    .submit(width, height);
            bitmap = future.get();
            future.cancel(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    //加载bitmap
    //要跳过内存缓存：有些操作会调用bitmap.recycle()，如果开启内存缓存，会出现bitmap recycled error
    @UiThread
    public static void loadBitmapFromGlide(Context context, Uri uri, int width, int height, SimpleGlideListener listener) {

        Glide.with(context).asBitmap()
                .load(uri)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerInside()
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Exception exception = e;
                        if (exception == null) exception = new Exception("Glide onLoadFailed! unknown error!");
                        exception.printStackTrace();
                        listener.onError(exception.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        if (resource == null) {
                            listener.onError("error: bitmap is null!");
                            return true;
                        }
                        listener.onSuccess(resource);
                        return true;
                    }
                })
                .preload(width, height);
    }

    //利用glide控制bitmap的size
    @UiThread
    public static void loadBitmapFromGlide(Context context, Bitmap bitmap, int width, int height, SimpleGlideListener listener) {

        Glide.with(context).asBitmap()
                .load(bitmap)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerInside()
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Exception exception = e;
                        if (exception == null) exception = new Exception("Glide onLoadFailed! unknown error!");
                        exception.printStackTrace();
                        listener.onError(exception.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        if (resource == null) {
                            listener.onError("error: bitmap is null!");
                            return true;
                        }
                        listener.onSuccess(resource);
                        return true;
                    }
                })
                .preload(width, height);
    }


    public interface SimpleGlideListener {
        void onSuccess(Bitmap bitmap);

        void onError(String errorMessage);
    }


}
