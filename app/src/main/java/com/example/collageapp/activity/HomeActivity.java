package com.example.collageapp.activity;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.collageapp.R;
import com.example.collageapp.util.PermissionUtil;
import com.example.collageapp.view.VerticalScrollView;


public class HomeActivity extends BaseActivity {

    private VerticalScrollView mBgView;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_home;
    }

    @Override
    protected int setContentIdRes() {
        return R.id.content_layout;
    }

    @Override
    protected void create() {

        findViewById(R.id.image_view_add).setOnClickListener(this::toGallery);

        //测试按钮
        findViewById(R.id.image_view_setting).setOnClickListener(v -> SettingActivity.startNewInstance(this));

        mBgView = findViewById(R.id.view_vertical_scroll);
        Glide.with(this)
                .asBitmap()
                .load(R.drawable.icon_home_bg_test)
                .addListener(new RequestListener<>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        mBgView.setData(resource);
                        return true;
                    }
                })
                .preload(2000, 2000);
    }

    private void toGallery(View v) {
        PermissionUtil.requestMediaPermission(this, (allGranted, grantedList, deniedList) -> {
            if (!allGranted) {
                Toast.makeText(this, "miss permission", Toast.LENGTH_SHORT).show();
                return;
            }
            GalleryActivity.startNewInstance(this);
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mBgView.startAnimator();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBgView.pauseAnimator();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBgView.stopAnimator();
    }
}