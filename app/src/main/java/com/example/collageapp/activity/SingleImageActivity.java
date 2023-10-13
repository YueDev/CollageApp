package com.example.collageapp.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.example.collageapp.R;
import com.example.collageapp.view.ShowView;


public class SingleImageActivity extends BaseActivity {

    private static final String KEY_SINGLE_IMAGE_ACTIVITY_URI = "key_single_image_activity_uri";
    private static final String ACTION_START_NEW_INSTANCE = "single.image.activity.new.instance";
    private Uri mUri;

    //图片的尺寸
    private int mSize = 1920;

    private View mLoadingView;

    public static void startNewInstance(Context context, Uri uri) {
        Intent intent = new Intent(context, SingleImageActivity.class).setAction(ACTION_START_NEW_INSTANCE);
        intent.putExtra(KEY_SINGLE_IMAGE_ACTIVITY_URI, uri);
        context.startActivity(intent);
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_single_image;
    }

    @Override
    protected int setContentIdRes() {
        return 0;
    }


    @Override
    protected void create() {

        Intent intent = getIntent();
        if (intent == null) {
            errorFinish("getIntent() is null");
            return;
        }

        //相册进来
        mUri = intent.getParcelableExtra(KEY_SINGLE_IMAGE_ACTIVITY_URI);


        if (mUri == null) {
            errorFinish("uri is null!");
            return;
        }

        initView();
        initViewModel();
        initClick();

        loadData();
    }



    private void initView() {
        mLoadingView = findViewById(R.id.view_loading);
    }


    private void initViewModel() {

    }

    private void initClick() {

    }


    private void loadData() {
        mLoadingView.setVisibility(View.GONE);
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    private void errorFinish(String message) {
        runOnUiThread(() -> {
            //toast的message为null 一些android版本会崩溃
            String s = message == null ? "unknown error" : message;
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}




