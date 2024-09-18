package com.example.collageapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collageapp.R;
import com.example.collageapp.adapter.SimpleImageAdapter;
import com.example.collageapp.util.ImageUtil;
import com.example.collageapp.view.NinePatchView;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;


public class SingleImageActivity extends BaseActivity {

    private static final String KEY_SINGLE_IMAGE_ACTIVITY_URI = "key_single_image_activity_uri";
    private static final String ACTION_START_NEW_INSTANCE = "single.image.activity.new.instance";
    private Uri mUri;

    //图片的尺寸
    private int mSize = 1920;

    private View mLoadingView;

    private RecyclerView mRecyclerView;
    private SimpleImageAdapter mAdapter;
    private List<Integer> mResList;
    private NinePatchView mNinePatchView;

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
        return R.id.layout_root;
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

        //点9的资源list
        mResList = new ArrayList<>();
        mResList.add(R.drawable.test_84);
        mResList.add(R.drawable.test_84_small);
        mResList.add(R.drawable.test_85);
        mResList.add(R.drawable.test_85_small);

        initView();
        initViewModel();
        initClick();

        loadData();
    }


    private void initView() {
        mNinePatchView = findViewById(R.id.view_nine_patch);
        mLoadingView = findViewById(R.id.view_loading);
        mRecyclerView = findViewById(R.id.recycler_view_image);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));


    }


    private void initViewModel() {

    }

    private void initClick() {

    }

    private void loadDone(Bitmap bitmap) {
        mNinePatchView.setBitmap(bitmap);

        mAdapter = new SimpleImageAdapter((position, imageResId) -> {
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), imageResId, null);
            if (drawable != null) {
                mNinePatchView.setNinePatch((NinePatchDrawable) drawable);
            }
            return Unit.INSTANCE;
        });
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.submitList(mResList);
    }

    private void loadData() {
        ImageUtil.loadBitmapFromGlide(this, mUri, 1024, 1024, new ImageUtil.SimpleGlideListener() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                loadDone(bitmap);
                mLoadingView.setVisibility(View.GONE);
            }

            @Override
            public void onError(String errorMessage) {
                errorFinish(errorMessage);
            }
        });
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




