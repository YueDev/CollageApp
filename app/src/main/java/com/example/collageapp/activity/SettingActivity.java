package com.example.collageapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.TextView;

import com.example.collageapp.R;
import com.example.collageapp.util.ShareUtil;


public class SettingActivity extends BaseActivity {

    public static void startNewInstance(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }


    @Override
    protected int setLayoutResId() {
        return R.layout.activity_setting;
    }

    @Override
    protected int setContentIdRes() {
        return R.id.layout_content;
    }

    @Override
    protected void create() {
        findViewById(R.id.image_view_close).setOnClickListener(v -> finish());
        findViewById(R.id.text_view_feedback).setOnClickListener(v -> FeedbackActivity.startNewInstance(this));
        findViewById(R.id.text_view_share).setOnClickListener(v -> ShareUtil.shareApp(this));
        TextView versionTextView = findViewById(R.id.textView6);
        versionTextView.setText(getVersion());
    }



    public String getVersion() {
        String version = "unknown";
        PackageManager manager = getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            version = info.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

}