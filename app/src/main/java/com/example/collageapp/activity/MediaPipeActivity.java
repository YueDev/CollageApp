package com.example.collageapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.example.collageapp.R;
import com.example.collageapp.bean.SegmentResult;
import com.example.collageapp.util.CallableFutureTask;
import com.example.collageapp.util.ImageUtil;
import com.example.collageapp.view.SegmentView;
import com.google.android.material.slider.Slider;
import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.ByteBufferExtractor;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.core.Delegate;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.imagesegmenter.ImageSegmenter;
import com.google.mediapipe.tasks.vision.imagesegmenter.ImageSegmenterResult;

import java.nio.FloatBuffer;
import java.util.List;


public class MediaPipeActivity extends BaseActivity {

    private static final String KEY_MEDIA_PIPE_ACTIVITY_URI = "key_media_pipe_activity_uri";
    private Uri mUri;

    //图片的尺寸
    private int mSize = 1920;

    private View mLoadingView;
    private SegmentView mShowView;

    private Slider mRSlider;
    private Slider mGSlider;
    private Slider mBSlider;

    private View mTypeButton;

    public static void startNewInstance(Context context, Uri uri) {
        Intent intent = new Intent(context, MediaPipeActivity.class);
        intent.putExtra(KEY_MEDIA_PIPE_ACTIVITY_URI, uri);
        context.startActivity(intent);
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_media_pipe;
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
        mUri = intent.getParcelableExtra(KEY_MEDIA_PIPE_ACTIVITY_URI);


        if (mUri == null) {
            errorFinish("uri is null!");
            return;
        }

        initView();
        initViewModel();
        initClick();

        loadData();
    }

    private void loadData() {

        BaseOptions baseOptions = BaseOptions.builder()
                .setDelegate(Delegate.CPU)
                .setModelAssetPath("selfie_multiclass_256x256.tflite")
                .build();
        ImageSegmenter.ImageSegmenterOptions options = ImageSegmenter.ImageSegmenterOptions.builder()
                .setRunningMode(RunningMode.IMAGE)
                .setBaseOptions(baseOptions)
                .setOutputCategoryMask(false)
                .setOutputConfidenceMasks(true)
                .build();

        new CallableFutureTask<>(() -> {
            Bitmap bitmap = ImageUtil.loadBitmapSync(this, mUri, mSize, mSize);
            if (bitmap == null) return null;
            ImageSegmenter segment = ImageSegmenter.createFromOptions(this, options);
            MPImage mpImage = new BitmapImageBuilder(bitmap).build();
            ImageSegmenterResult result = segment.segment(mpImage);
            List<MPImage> mpImages = result.confidenceMasks().orElse(null);
            if (mpImages == null) return null;
            //背景图取反即是人物图
            MPImage image0 = mpImages.get(0);
            Bitmap peopleBitmap = getBitmapWithMPImage(image0, bitmap, true);
            //获取头发的图片
            MPImage image1 = mpImages.get(1);
            Bitmap hearBitmap = getBitmapWithMPImage(image1, bitmap, false);

            return new SegmentResult(bitmap, hearBitmap, peopleBitmap);
        }).execute(result -> runOnUiThread(() -> {
            if (result == null) {
                errorFinish("result is null");
                return;
            }
            mShowView.setData(result);
            mShowView.setRGB(1f, 1f, 1f);
            mLoadingView.setVisibility(View.GONE);
        }));


    }


    private void initView() {
        mLoadingView = findViewById(R.id.view_loading);
        mShowView = findViewById(R.id.image_view_show);
        mRSlider = findViewById(R.id.slider_r);
        mGSlider = findViewById(R.id.slider_g);
        mBSlider = findViewById(R.id.slider_b);

        mRSlider.setValue(1f);
        mGSlider.setValue(1f);
        mBSlider.setValue(1f);

        setSliderShow(false);

        mTypeButton = findViewById(R.id.button_type);

    }


    private void initViewModel() {

    }

    private void initClick() {
        mRSlider.addOnChangeListener((slider, value, fromUser) -> mShowView.setRGB(mRSlider.getValue(), mGSlider.getValue(), mBSlider.getValue()));
        mGSlider.addOnChangeListener((slider, value, fromUser) -> mShowView.setRGB(mRSlider.getValue(), mGSlider.getValue(), mBSlider.getValue()));
        mBSlider.addOnChangeListener((slider, value, fromUser) -> mShowView.setRGB(mRSlider.getValue(), mGSlider.getValue(), mBSlider.getValue()));

        mTypeButton.setOnClickListener(v -> {
            int newType = mShowView.getDrawType() == 1 ? 0 : 1;
            setSliderShow(newType == 1);
            mShowView.setDrawType(newType);
        });

    }

    private void setSliderShow(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        mRSlider.setVisibility(visibility);
        mGSlider.setVisibility(visibility);
        mBSlider.setVisibility(visibility);
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



    //reverse:是否反转alpha值，背景反转后就是人像,其他的不需要反转
    private Bitmap getBitmapWithMPImage(MPImage image, Bitmap src, boolean reverse) {
        try {
            FloatBuffer buffer = ByteBufferExtractor.extract(image).asFloatBuffer();
            int[] pixels = new int[buffer.capacity()];
            for (int i = 0; i < pixels.length; i++) {
                float f = buffer.get();
                float alpha = reverse ? 1.0f - f : f;
                if (alpha < 0.2f) alpha = 0.0f;
                int color = src.getPixel(i % image.getWidth(), i / image.getWidth());
                color = Color.argb(
                        (int)(Color.alpha(color) * alpha),
                        Color.red(color),
                        Color.green(color),
                        Color.blue(color)
                );
                pixels[i] = color;
            }
            return Bitmap.createBitmap(pixels, image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

