package com.example.collageapp.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.WorkerThread;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.collageapp.R;
import com.example.collageapp.bean.BorderBean;
import com.example.collageapp.bean.LoadingState;
import com.example.collageapp.bean.RatioBean;
import com.example.collageapp.fragment.PicManagerDialogFragment;
import com.example.collageapp.fragment.ProDialogFragment;
import com.example.collageapp.fragment.RatioDialogFragment;
import com.example.collageapp.fragment.SaveDialogFragment;
import com.example.collageapp.util.ImageUtil;
import com.example.collageapp.util.PermissionUtil;
import com.example.collageapp.util.SizeUtil;
import com.example.collageapp.util.WindowSizeClass;
import com.example.collageapp.view.CollageButtonView;
import com.example.collageapp.view.LoadingLayout;
import com.example.collageapp.viewmodel.ProViewModel;
import com.example.collageapp.viewmodel.SaveViewModel;
import com.example.collageapp.viewmodel.XCollageViewModel;
import com.example.collageapp.x_collage.XBitmapSimple;
import com.example.collageapp.x_collage.XCollageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;


public class XCollageActivity extends BaseActivity {

    private static final String KEY_X_COLLAGE_ACTIVITY_IMAGES = "key_x_collage_activity_images";

    public static final String KEY_RETURN_URIS = "key_return_uris_x_collage_activity";

    private XCollageViewModel mViewModel;
    private SaveViewModel mSaveViewModel;

    private ProViewModel mProViewModel;

    //图片列表
    private List<Uri> mUriList;

    //图片的尺寸d
    private int mSize = 256;

    //loading view
    private LoadingLayout mLoadingView;

    //拼图view
    private XCollageView mCollageView;
    //图片管理按钮
    private View mPicManagerView;

    //保存按钮
    private View mSaveButton;

    //关闭按钮
    private View mCloseButton;

    //关闭的对话框
    private View mCloseDialogBg;
    private View mCloseDialogButton;

    //底部的各种按钮view
    private CollageButtonView mCollageButtonView;

    private boolean mIsAboveQ = false;

    //宽度size

    public static void startNewInstance(Context context, Collection<Uri> imageUris) {
        Intent intent = new Intent(context, XCollageActivity.class);
        ArrayList<Uri> uris = new ArrayList<>(imageUris);
        intent.putParcelableArrayListExtra(KEY_X_COLLAGE_ACTIVITY_IMAGES, uris);
        context.startActivity(intent);
    }

    public static Intent getReturnUrisIntent(Context context, Collection<Uri> imageUris) {
        Intent intent = new Intent(context, XCollageActivity.class);
        ArrayList<Uri> uris = new ArrayList<>(imageUris);
        intent.putParcelableArrayListExtra(KEY_X_COLLAGE_ACTIVITY_IMAGES, uris);
        return intent;
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_x_collage;
    }

    @Override
    protected int setContentIdRes() {
        return R.id.content_layout;
    }

    @Override
    protected int setContentIdRes2() {
        return R.id.content_layout2;
    }

    @Override
    protected void create() {

        mIsAboveQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;

        Intent intent = getIntent();
        if (intent == null) {
            errorFinish("getIntent() is null");
            return;
        }

        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            //单图分享进来
            Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (imageUri == null) {
                errorFinish("share uri is null");
                return;
            }
            mUriList = new ArrayList<>();
            mUriList.add(imageUri);
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            //多图分享进来
            ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            if (uris == null || uris.size() == 0) {
                errorFinish("share uris is null or empty");
                return;
            }
            mUriList = new ArrayList<>();
            if (uris.size() > 120) {
                mUriList = uris.subList(0, 120);
            } else {
                mUriList = new ArrayList<>(uris);
            }
        } else {
            //相册进来
            mUriList = intent.getParcelableArrayListExtra(KEY_X_COLLAGE_ACTIVITY_IMAGES);
        }

        if (mUriList == null || mUriList.size() == 0) {
            errorFinish("uriList is null or empty");
            return;
        }
        //计算图片尺寸
        mSize = ImageUtil.getImageSize(getApplication(), mUriList.size());
        initView();
        initViewModel();
        initClick();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                XCollageActivity.this.handleOnBackPressed();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

//        loadImage(mUriList, 0, mUriList.size());
    }


    private void initView() {
        mLoadingView = findViewById(R.id.view_loading);

        mCollageView = findViewById(R.id.collage_view);

        mSaveButton = findViewById(R.id.text_view_save);
        mCloseButton = findViewById(R.id.image_view_close);
        mPicManagerView = findViewById(R.id.text_view_pic_manager);

        mCloseDialogBg = findViewById(R.id.view_close_bg);
        mCloseDialogButton = findViewById(R.id.text_view_close);

        mCollageButtonView = findViewById(R.id.view_collage_button);
    }
    private void initViewModel() {
        mProViewModel = new ViewModelProvider(this).get(ProViewModel.class);
        mProViewModel.setProUser(true);

        mProViewModel.isRemoveWater().observe(this, isRemove -> {
            if (isRemove == null) return;
            mCollageView.setShowWaterMark(!isRemove);
        });

        mSaveViewModel = new ViewModelProvider(this).get(SaveViewModel.class);

        mViewModel = new ViewModelProvider(this).get(XCollageViewModel.class);
        mViewModel.init(mUriList, mSize);

        //观察拼图数据
        mViewModel.getData().observe(this, result -> {
            if (result == null) return;
            switch (result.getState()) {
                case INIT:
                    mLoadingView.show();
                    break;
                case LOADING:
                    mLoadingView.setProgressText(result.getMessage());
                    break;
                case SUCCESS:
                    setBitmaps(result.getContent());
                    mLoadingView.hidden();
                    break;
                case ERROR:
                    mLoadingView.hidden();
                    errorFinish(result.getMessage());
                    break;
            }
        });

        //观察比例
        mViewModel.getRatio().observe(this, ratio -> {
            if (ratio == null) return;
            mCollageButtonView.setRatioResId(ratio);
        });

        //观察边框
        mViewModel.getBorder().observe(this, border -> {
            if (border == null) return;
            mCollageButtonView.setBorderResId(border.getIconResId());

        });
    }

    private void initClick() {

        mCollageView.setClickListener(this::clickWaterMark);

        mPicManagerView.setOnClickListener(this::clickPicManager);

        mSaveButton.setOnClickListener(this::clickSave);
        mCloseButton.setOnClickListener(v -> showCloseDialog());

        mCloseDialogButton.setOnClickListener(this::clickExit);
        mCloseDialogBg.setOnClickListener(v -> hiddenCloseDialog());

        mCollageButtonView.setClickListener(new CollageButtonView.ClickListener() {
            @Override
            public void clickCollageButton() {
                XCollageActivity.this.clickCollageButton();
            }

            @Override
            public void clickBorderButton() {
                clickBorder();
            }

            @Override
            public void clickRatio(RatioBean ratioBean) {
                XCollageActivity.this.clickRatio(ratioBean);
            }
        });

        mCollageButtonView.setRatioBeans(mViewModel.getRatioBeans());
    }


    private void setBitmaps(List<XBitmapSimple> bitmaps) {

        float ratio = mViewModel.getCurrentRatio().getRatio();
        float outerPadding = mViewModel.getCurrentBorder().getOuterPadding();
        float innerPadding = mViewModel.getCurrentBorder().getInnerPadding();

        mCollageView.setData(bitmaps, outerPadding, innerPadding, ratio);
    }


    //点击拼图按钮
    private void clickCollageButton() {
        mCollageView.collageAnimated();
    }


    //点击比例 返回值用于ratio adapter更改选择框
    private void clickRatio(RatioBean ratioBean) {
        //如果是默认比例 返回false 交给ratio fragment去处理
        if (ratioBean.isCustom()) {
            RatioBean currentRatio = mViewModel.getCurrentRatio();
            int w = currentRatio.getW();
            int h = currentRatio.getH();
            RatioDialogFragment.newInstance(w, h).show(getSupportFragmentManager(), RatioDialogFragment.TAG);
            return;
        }
        //其他情况直接改变比例
        boolean result = mCollageView.setRatioAnimated(ratioBean.getRatio());
        if (result) {
            mViewModel.setRatio(ratioBean);
        }
    }

    //更改自定义比例
    public void changeCustomRatio(int w, int h) {
        if (mCollageView.setRatioAnimated((float) w / h)) {
            RatioBean defaultRatio = mViewModel.getDefaultRatio();
            defaultRatio.setW(w);
            defaultRatio.setH(h);
            mViewModel.setRatio(defaultRatio);
        }
    }

    //点击边框
    private void clickBorder() {
        BorderBean border = mViewModel.getNextBorder();
        if (mCollageView.setCollagePaddingAnimated(border.getOuterPadding(), border.getInnerPadding())) {
            //更改有效 通知ui更新
            mViewModel.setBorder(border);
        }
    }

    //点击图片管理
    private void clickPicManager(View view) {
        PicManagerDialogFragment
                .newInstance(mViewModel.getUris())
                .show(getSupportFragmentManager(), PicManagerDialogFragment.TAG);
    }

    //点击保存
    private void clickSave(View v) {
        mSaveViewModel.reset();
        SaveDialogFragment.newInstance().show(getSupportFragmentManager(), SaveDialogFragment.TAG);
    }


    //点击水印
    private void clickWaterMark() {
        ProDialogFragment.newInstance(true).show(getSupportFragmentManager(), ProDialogFragment.TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCollageView.stopSave();
        mSaveViewModel.reset();
    }

    private void clickExit(View view) {
        finishAndReturnUris(mViewModel.getUris());
    }

    private void finishAndReturnUris(List<Uri> uris) {
        if (uris == null) uris = new ArrayList<>();
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(KEY_RETURN_URIS, new ArrayList<>(uris));
        setResult(RESULT_OK, intent);
        finish();
    }

    //重新来一张
    public void makeAnother() {
        finishAndReturnUris(null);
    }


    private void handleOnBackPressed() {
        if (mLoadingView.getVisibility() == View.VISIBLE) return;
        if (mCollageButtonView.tryHiddenRatio()) return;
        if (mCloseDialogButton.getVisibility() == View.VISIBLE) {
            hiddenCloseDialog();
        } else {
            showCloseDialog();
        }
    }

    private void showCloseDialog() {
        ViewCompat.animate(mCloseDialogBg).alpha(1f).withStartAction(() -> mCloseDialogBg.setVisibility(View.VISIBLE));
        ViewCompat.animate(mCloseDialogButton).alpha(1f).withStartAction(() -> mCloseDialogButton.setVisibility(View.VISIBLE));
    }


    private boolean _canHidden = true;

    private void hiddenCloseDialog() {
        if (!_canHidden) return;
        ViewCompat.animate(mCloseDialogBg).alpha(0f).withStartAction(() -> _canHidden = false).withEndAction(() -> {
                    mCloseDialogBg.setVisibility(View.GONE);
                    _canHidden = true;
                }
        );
        ViewCompat.animate(mCloseDialogButton).alpha(0f).withEndAction(() -> mCloseDialogButton.setVisibility(View.GONE));
    }

    private void errorFinish(String message) {
        runOnUiThread(() -> {
            //toast的message为null 一些android版本会崩溃
            String s = message == null ? "unknown error" : message;
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            finish();
        });
    }


    //====================================保存=================================================

    public void save() {
        if (mIsAboveQ) {
            startSave();
        } else {
            //Q以下分享进来 没有写权限的话没法保存
            PermissionUtil.requestWritePermission(this, (allGranted, grantedList, deniedList) -> {
                if (!allGranted) {
                    Toast.makeText(this, "miss permission", Toast.LENGTH_SHORT).show();
                    return;
                }
                startSave();
            });
        }

    }

    //开始保存
    public void startSave() {
        mSaveViewModel.setSaveState(LoadingState.LOADING);
        new Thread(() -> {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                int yearNow = calendar.get(Calendar.YEAR);
                int monthNow = calendar.get(Calendar.MONTH) + 1;
                int dayNow = calendar.get(Calendar.DAY_OF_MONTH);
                int hourNow = calendar.get(Calendar.HOUR_OF_DAY);
                int minuteNow = calendar.get(Calendar.MINUTE);
                int secondNow = calendar.get(Calendar.SECOND);
                int millisecond = calendar.get(Calendar.MILLISECOND);

                final String fileName = "XCollage_" + yearNow
                        + monthNow + dayNow
                        + hourNow + minuteNow
                        + secondNow + millisecond;

                int size = mSaveViewModel.getSaveSize();
                Bitmap bitmap = mCollageView.getHiResBitmap(size);
                if (mIsAboveQ) {
                    saveBitmapToDisk29(bitmap, fileName, false);
                } else {
                    saveBitmapToDisk(bitmap, fileName, false);
                }
                mSaveViewModel.postSaveState(LoadingState.SUCCESS);
                runOnUiThread(() -> Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
                mSaveViewModel.postSaveState(LoadingState.ERROR);
                runOnUiThread(() -> Toast.makeText(this, "save error!", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


    //android q以下的保存
    @WorkerThread
    private void saveBitmapToDisk(Bitmap bitmap, String name, boolean isPNG) throws Exception {

        String fullName = isPNG ? name + ".png" : name + ".jpg";

        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator +
                "Pictures" +
                File.separator +
                "XCollage";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir.getAbsolutePath() + File.separator + fullName);
        FileOutputStream fos = new FileOutputStream(file);
        boolean success = bitmap.compress(isPNG ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG,
                90,
                fos
        );

        if (!success) throw new Exception("bitmap compress failed");
        fos.close();

        String filePath = file.getAbsolutePath();

        //保存完成 通知媒体库更新
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
        MediaScannerConnection.scanFile(this, new String[]{filePath}, null, (path, uri) -> {
            mSaveViewModel.setSaveUri(uri);
        });

    }


    //ui线程  android q和以上版本 保存
    @WorkerThread
    private void saveBitmapToDisk29(Bitmap bitmap, String name, boolean isPNG) throws Exception {

        ContentResolver contentResolver = getContentResolver();
        Uri externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        //先进行图片查重
        String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
        String selection = MediaStore.Images.Media.DISPLAY_NAME + " = ?";
        String[] selectionArgs = {name};
        Cursor cursorName = contentResolver.query(externalUri, projection, selection, selectionArgs, null);

        if (cursorName != null) {
            if (cursorName.getCount() > 1) throw new Exception("has a same picture");
            cursorName.close();
        }

        //DISPLAY_NAME是前缀名，后缀名根据MIME_TYPE让系统指定
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, name);
        values.put(MediaStore.Images.Media.TITLE, name);
        values.put(MediaStore.MediaColumns.MIME_TYPE, isPNG ? "image/png" : "image/jpeg");
        values.put(MediaStore.Images.Media.DESCRIPTION, "XCollage");
        values.put(MediaStore.Video.Media.RELATIVE_PATH, "Pictures/XCollage");


        long time = System.currentTimeMillis();
        values.put(MediaStore.Images.Media.DATE_ADDED, time / 1000);
        values.put(MediaStore.Images.Media.DATE_MODIFIED, time / 1000);
        //API29以上，设置IS_PENDING状态为1，这样存储结束前，其他应用就不会处理这张图片
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) values.put(MediaStore.Images.Media.IS_PENDING, 1);

        //获取插入图片的uri
        Uri insertUri = contentResolver.insert(externalUri, values);


        if (insertUri == null) throw new Exception("insertUri is null");

        OutputStream stream = contentResolver.openOutputStream(insertUri);


        boolean success = bitmap.compress(
                isPNG ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG,
                90,
                stream
        );

        if (!success) throw new Exception("bitmap compress failed");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            contentResolver.update(insertUri, values, null, null);
        }
        mSaveViewModel.setSaveUri(insertUri);
    }


}

