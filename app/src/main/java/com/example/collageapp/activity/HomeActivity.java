package com.example.collageapp.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.collageapp.R;
import com.example.collageapp.util.PermissionUtil;


public class HomeActivity extends BaseActivity {


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

        ImageView bgImageView = findViewById(R.id.image_view_bg);

        Glide.with(this)
                .load(R.drawable.icon_home_bg_test)
                .into(bgImageView);
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
}