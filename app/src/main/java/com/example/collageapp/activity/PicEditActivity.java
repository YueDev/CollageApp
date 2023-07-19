package com.example.collageapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.view.View;

import com.example.collageapp.R;
import com.example.collageapp.util.ImageUtil;
import com.example.collageapp.view.PicEditView;


public class PicEditActivity extends BaseActivity {

    private static final String KEY_URI_PIC_EDIT = "key_uri_pic_edit";
    private static final String KEY_BITMAP_KEY_PIC_EDIT = "key_bitmap_key_pic_edit";

    public static final String KEY_RETURN_MATRIX_VALUE_PIC_EDIT = "key_return_matrix_value_pic_edit";
    public static final String KEY_RETURN_KEY_PIC_EDIT = "key_return_key_pic_edit";

    private View mBackView;
    private View mFlipView;
    private View mMirrorView;
    private View mRotateView;
    private View mOkView;
    private PicEditView mPicEditView;


    private String mKey = "";

    //暂存 结果bitmap
    public static Bitmap sResultBitmap;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_pic_edit;
    }

    @Override
    protected int setContentIdRes() {
        return R.id.layout_content;
    }

    @Override
    protected void create() {
        Intent intent = getIntent();
        if (intent == null) return;

        mBackView = findViewById(R.id.image_view_back);
        mFlipView = findViewById(R.id.image_view_flip);
        mMirrorView = findViewById(R.id.image_view_mirror);
        mRotateView = findViewById(R.id.image_view_rotate);
        mOkView = findViewById(R.id.image_view_ok);
        mPicEditView = findViewById(R.id.view_pic_edit);

        mKey = intent.getStringExtra(KEY_BITMAP_KEY_PIC_EDIT);

        Uri uri = intent.getParcelableExtra(KEY_URI_PIC_EDIT);
        int size = ImageUtil.getImageSize(getApplication(), 1);
        ImageUtil.loadBitmapFromGlide(this, uri, size, size, new ImageUtil.SimpleGlideListener() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                init(bitmap);
            }

            @Override
            public void onError(String errorMessage) {
                finish();
            }
        });
    }

    private void init(Bitmap bitmap) {

        mPicEditView.setBitmap(bitmap);

        mBackView.setOnClickListener(v -> finish());
        mFlipView.setOnClickListener(v -> mPicEditView.flip());
        mMirrorView.setOnClickListener(v -> mPicEditView.mirror());
        mRotateView.setOnClickListener(v -> mPicEditView.rotate(-90f));

        mOkView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(KEY_RETURN_KEY_PIC_EDIT, mKey);
            Matrix matrix = mPicEditView.getBitmapMatrix();
            float[] values = new float[9];
            matrix.getValues(values);
            intent.putExtra(KEY_RETURN_MATRIX_VALUE_PIC_EDIT, values);
            setResult(RESULT_OK, intent);
            sResultBitmap = mPicEditView.getBitmap();
            finish();
        });
    }


    public static Intent getUriIntent(Context context, Uri uri, String key) {
        Intent intent = new Intent(context, PicEditActivity.class);
        intent.putExtra(KEY_URI_PIC_EDIT, uri);
        intent.putExtra(KEY_BITMAP_KEY_PIC_EDIT, key);
        return intent;
    }
}