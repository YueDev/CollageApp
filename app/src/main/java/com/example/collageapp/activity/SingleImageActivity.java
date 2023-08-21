package com.example.collageapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.collageapp.R;
import com.example.collageapp.util.ImageUtil;
import com.example.collageapp.view.ShowView;
import com.google.android.gms.common.moduleinstall.InstallStatusListener;
import com.google.android.gms.common.moduleinstall.ModuleAvailabilityResponse;
import com.google.android.gms.common.moduleinstall.ModuleInstall;
import com.google.android.gms.common.moduleinstall.ModuleInstallClient;
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest;
import com.google.android.gms.common.moduleinstall.ModuleInstallResponse;
import com.google.android.gms.common.moduleinstall.ModuleInstallStatusUpdate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.interfaces.Detector;

import java.util.List;
import java.util.stream.Collectors;


public class SingleImageActivity extends BaseActivity {

    private static final String KEY_SINGLE_IMAGE_ACTIVITY_URI = "key_single_image_activity_uri";
    private static final String KEY_SINGLE_IMAGE_ACTIVITY_FAST_MODE = "key_single_image_activity_fast_mode";
    private static final String ACTION_START_NEW_INSTANCE = "single.image.activity.new.instance";
    private Uri mUri;

    private boolean mFastMode = false;

    //图片的尺寸
    private int mSize = 1920;

    private View mLoadingView;
    private ShowView mShowView;
    private FaceDetector mDetector;
    private TextView mDebugTextView;

    public static void startNewInstance(Context context, Uri uri, boolean fastMode) {
        Intent intent = new Intent(context, SingleImageActivity.class).setAction(ACTION_START_NEW_INSTANCE);
        intent.putExtra(KEY_SINGLE_IMAGE_ACTIVITY_URI, uri);
        intent.putExtra(KEY_SINGLE_IMAGE_ACTIVITY_FAST_MODE, fastMode);
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
        mFastMode = intent.getBooleanExtra(KEY_SINGLE_IMAGE_ACTIVITY_FAST_MODE, false);

        if (mUri == null) {
            errorFinish("uri is null!");
            return;
        }

        initView();
        initViewModel();
        initClick();

        //低端机用快速模式   0.1的最小尺寸
        //高端机用精准模式， 0.05的最小尺寸

        Log.d("YUEDEVTAG", "mfast: " + mFastMode);

        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(mFastMode ? FaceDetectorOptions.PERFORMANCE_MODE_FAST : FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setMinFaceSize(mFastMode ? 0.1f : 0.05f)
                .build();
        mDetector = FaceDetection.getClient(options);

        ModuleInstallClient moduleInstallClient = ModuleInstall.getClient(this);

        moduleInstallClient.areModulesAvailable(mDetector)
                .addOnSuccessListener(response -> {
                    if (response.areModulesAvailable()) {
//                        Toast.makeText(this, "模型可用", Toast.LENGTH_SHORT).show();
                        loadData();
//                        mLoadingView.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this, "模型不可用， 请求安装", Toast.LENGTH_SHORT).show();
                        ModuleInstallRequest request =
                                ModuleInstallRequest.newBuilder()
                                        .addApi(mDetector)
                                        .setListener(update -> {
                                            if (update.getInstallState() == ModuleInstallStatusUpdate.InstallState.STATE_COMPLETED) {
                                                Toast.makeText(this, "模型安装成功", Toast.LENGTH_SHORT).show();
                                                loadData();
                                            } else {
                                                Toast.makeText(this, "模型安装失败, code: " + update.getInstallState(), Toast.LENGTH_SHORT).show();
                                                mLoadingView.setVisibility(View.GONE);
                                            }
                                        })
                                        .build();
                        moduleInstallClient.installModules(request);
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "模型检测失败", Toast.LENGTH_SHORT).show();
                    mLoadingView.setVisibility(View.GONE);
                });

//        loadData();
    }


    private void initView() {
        mLoadingView = findViewById(R.id.view_loading);
        mShowView = findViewById(R.id.image_view_show);
        mDebugTextView = findViewById(R.id.text_view_time);
    }


    private void initViewModel() {

    }

    private void initClick() {

    }


    private void loadData() {

        ImageUtil.loadBitmapFromGlide(this, mUri, mSize, mSize, new ImageUtil.SimpleGlideListener() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                InputImage image = InputImage.fromBitmap(bitmap, 0);
                long t = System.currentTimeMillis();
                mDetector.process(image)
                        .addOnSuccessListener(faces -> runOnUiThread(() -> {
                            long time = System.currentTimeMillis() - t;
                            String s = "time: " + time + "ms\nfast mode: " + mFastMode + "\nface count: " + faces.size();
                            mDebugTextView.setText(s);
                            List<Rect> bounds = faces.stream()
                                    .map(Face::getBoundingBox)
                                    .collect(Collectors.toList());
                            mShowView.setData(bitmap, bounds);
                            mLoadingView.setVisibility(View.GONE);
                        })).addOnFailureListener(e -> {
                            //检测失败
                            runOnUiThread(() -> {
                                Toast.makeText(SingleImageActivity.this, "no detect face", Toast.LENGTH_SHORT).show();
                                mLoadingView.setVisibility(View.GONE);
                            });
                        });
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




