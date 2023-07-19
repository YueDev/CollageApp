package com.example.collageapp.activity;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.airbnb.lottie.LottieAnimationView;
import com.example.collageapp.R;
import com.example.collageapp.adapter.GalleryAdapter;
import com.example.collageapp.bean.AlbumBean;
import com.example.collageapp.bean.GalleryBean;
import com.example.collageapp.fragment.GalleryManagerDialogFragment;
import com.example.collageapp.util.GridItemDecoration;
import com.example.collageapp.util.ImageUtil;
import com.example.collageapp.util.ShareUtil;
import com.example.collageapp.util.SizeUtil;
import com.example.collageapp.util.WindowSizeClass;
import com.example.collageapp.view.AlbumView;
import com.example.collageapp.viewmodel.GalleryViewModel;

import java.util.ArrayList;
import java.util.List;


public class GalleryActivity extends BaseActivity {

    public static final String KEY_RETURN_URIS = "key_return_uris_gallery_activity";

    private static final String KEY_ADD_URIS = "key_add_uris";
    private static final String KEY_IS_ADD = "key_is_add";

    private GalleryViewModel mViewModel;

    //相册rv
    private RecyclerView mGalleryRecyclerView;
    private GalleryAdapter mGalleryAdapter;


    //专辑文字
    private TextView mAlbumTextView;
    private View mArrowView;
    private AlbumView mAlbumView;

    private LottieAnimationView mNumLottieView;

    //图片管理 张数
    private TextView mManagerTextView;

    //下一步
    private ImageView mNextImageView;

    //宽度size
    private WindowSizeClass mWidthWindowSizeClass = WindowSizeClass.COMPACT;


    private int mMax = ImageUtil.sMaxNum;
    //添加图片 选择完成set Result ok
    private boolean mIsAdd = false;

    private String mCanNotSelectString;


    ActivityResultLauncher<Intent> mGooglePhotosLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() != RESULT_OK) return;
        Intent intent = result.getData();
        if (intent == null) return;
        ClipData clipData = intent.getClipData();
        if (clipData == null || clipData.getItemCount() == 0) return;
        int allowNum = mMax - mViewModel.getSelectNum();
        if (allowNum <= 0) return;
        List<Uri> uris = new ArrayList<>();
        allowNum = Math.min(allowNum, clipData.getItemCount());
        for (int i = 0; i < allowNum; i++) {
            ClipData.Item item = clipData.getItemAt(i);
            if (item == null) continue;
            Uri uri = item.getUri();
            if (uri == null) continue;
            uris.add(uri);
        }
        mViewModel.addOutUris(uris);
    });

    //编辑页面返回
    ActivityResultLauncher<Intent> mXCollageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() != RESULT_OK) return;
        Intent intent = result.getData();
        if (intent == null) return;
        List<Uri> uriList = intent.getParcelableArrayListExtra(XCollageActivity.KEY_RETURN_URIS);
        if (uriList == null || uriList.size() == 0) {
            mViewModel.reset(getContentResolver());
        }

    });


    public static void startNewInstance(Context context) {
        Intent intent = new Intent(context, GalleryActivity.class);
        context.startActivity(intent);
    }

    public static Intent getAddUrisIntent(Context context, int maxNum) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(KEY_IS_ADD, true);
        intent.putExtra(KEY_ADD_URIS, maxNum);
        return intent;
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_gallery;
    }

    @Override
    protected int setContentIdRes() {
        return R.id.layout_content;
    }

    @Override
    protected void create() {
        Intent intent = getIntent();
        if (intent != null) {
            mMax = getIntent().getIntExtra(KEY_ADD_URIS, mMax);
            mIsAdd = getIntent().getBooleanExtra(KEY_IS_ADD, false);
        }

        mViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        View decorView = getWindow().getDecorView();
        decorView.post(() -> {
            int measuredWidth = decorView.getMeasuredWidth();
            mWidthWindowSizeClass = SizeUtil.computeWindowSizeClasses(measuredWidth);
            initView();
            initObserver();
        });

    }


    private void initView() {

        findViewById(R.id.image_view_close).setOnClickListener(v -> finish());

        mAlbumView = findViewById(R.id.view_album);

        mGalleryRecyclerView = findViewById(R.id.recycler_view_gallery);
        int spanCount = 3 ;
        if (mWidthWindowSizeClass == WindowSizeClass.MEDIUM) {
            spanCount = 4;
        } else if (mWidthWindowSizeClass == WindowSizeClass.EXPANDED) {
            spanCount = 5;
        }
        mGalleryRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));
        mGalleryRecyclerView.setHasFixedSize(true);
        try {
            ((SimpleItemAnimator) mGalleryRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GridItemDecoration itemDecoration = new GridItemDecoration(SizeUtil.dp2px(150f), spanCount);
        mGalleryRecyclerView.addItemDecoration(itemDecoration);

        mGalleryAdapter = new GalleryAdapter();
        mGalleryRecyclerView.setAdapter(mGalleryAdapter);
        mGalleryAdapter.setClickListener(this::clickAdd);

        mAlbumTextView = findViewById(R.id.text_view_album);
        mAlbumTextView.setOnClickListener(this::clickAlbum);
        mArrowView = findViewById(R.id.image_view_arrow);
        mArrowView.setOnClickListener(this::clickAlbum);

        mNumLottieView = findViewById(R.id.lottie_view_num);

        mManagerTextView = findViewById(R.id.text_view_manager);
        mManagerTextView.setOnClickListener(this::clickManager);

        mNextImageView = findViewById(R.id.image_view_next);
        mNextImageView.setOnClickListener(this::clickNext);

    }


    //初始化观察者
    private void initObserver() {
        //相册beans
        mViewModel.getGalleryBeans(getContentResolver()).observe(this, pair -> {
            if (pair == null) return;
            List<GalleryBean> beans = pair.first;
            if (pair.second) {
                mGalleryAdapter.submitList(beans, () -> mGalleryRecyclerView.scrollToPosition(0));
            } else {
                mGalleryAdapter.submitList(beans);
            }
        });

        //专辑名称
        mViewModel.getAlbumName().observe(this, name -> mAlbumTextView.setText(name));

        //专辑bean
        mViewModel.getAlbumBeans(this).observe(this, albumBeans -> {
                    List<AlbumBean> beans = new ArrayList<>(albumBeans);
                    if (mIsAdd) {
                        //关闭当前activity 会失去对google相册uri的控制权，
                        //因此会关闭的相册页面，不显示google photos.
                        for (AlbumBean bean : beans) {
                            if (bean.isGooglePhotos()) {
                                beans.remove(bean);
                                break;
                            }
                        }
                    }

                    mAlbumView.setData(beans, new AlbumView.ClickListener() {
                        @Override
                        public void onHidden() {
                            mArrowView.post(() -> mArrowView.setRotation(180));
                        }

                        @Override
                        public void onShow() {
                            mArrowView.post(() -> mArrowView.setRotation(0));
                        }

                        @Override
                        public void clickBean(AlbumBean bean) {
                            mAlbumView.hidden();
                            if (bean.isGooglePhotos()) {
                                if (mViewModel.getSelectNum() == mMax) {
                                    Toast.makeText(GalleryActivity.this, "最多添加" + mMax, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                //选择google photos
                                Intent intent = ShareUtil.getGooglePhotosIntent(GalleryActivity.this);
                                if (intent == null) {
                                    Toast.makeText(GalleryActivity.this, "未安装Google Photos", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                mGooglePhotosLauncher.launch(intent);
                            } else {
                                //选择专辑
                                mViewModel.changeAlbum(getContentResolver(), bean);
                            }
                        }
                    });
                }

        );

        //选择的uri
        mViewModel.getSelectUris().observe(this, uris -> {
            if (uris == null) return;
            if (uris.size() == 0) {
                mNextImageView.setImageResource(R.drawable.icon_gallery_next_unuse);
                mManagerTextView.setTextColor(Color.parseColor("#A4A7A8"));
            } else {
                mNextImageView.setImageResource(R.drawable.icon_gallery_next);
                mManagerTextView.setTextColor(Color.parseColor("#495052"));

            }
            mManagerTextView.setText(uris.size() + "");
            if (uris.size() > 0) mNumLottieView.playAnimation();
        });
    }


    //点击管理
    private void clickManager(View v) {
        if (mViewModel.getSelectNum() == 0) return;
        GalleryManagerDialogFragment.newInstance().show(getSupportFragmentManager(), GalleryManagerDialogFragment.TAG);
    }

    //点击专辑
    private void clickAlbum(View v) {
        if (mAlbumView.isNeedHidden()) {
            mAlbumView.post(() -> mAlbumView.hidden());
        }
        mAlbumView.show();
    }


    //点击下一步
    private void clickNext(View v) {
        List<Uri> uris = mViewModel.getSelectUris().getValue();
        if (uris == null || uris.size() == 0) return;
        if (mIsAdd) {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(KEY_RETURN_URIS, new ArrayList<>(uris));
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Intent intent = XCollageActivity.getReturnUrisIntent(this, uris);
            mXCollageLauncher.launch(intent);
        }
    }

    //点击rv 添加图片
    private void clickAdd(GalleryBean bean) {
        if (mViewModel.getSelectNum() >= mMax) {
            if (mCanNotSelectString == null) {
                mCanNotSelectString = getString(R.string.can_not_select_20);
                mCanNotSelectString = mCanNotSelectString.replace("20", mMax + "");
            }
            Toast.makeText(this, mCanNotSelectString, Toast.LENGTH_SHORT).show();
            return;
        }
        mViewModel.addUri(bean);
    }


    @Override
    public void onBackPressed() {
        if (mAlbumView.isNeedHidden()) {
            mAlbumView.hidden();
            return;
        }
        super.onBackPressed();
    }
}