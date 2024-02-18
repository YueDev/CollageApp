package com.example.collageapp.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collageapp.R;
import com.example.collageapp.activity.GalleryActivity;
import com.example.collageapp.activity.PicEditActivity;
import com.example.collageapp.adapter.PicManagerAdapter;
import com.example.collageapp.bean.PicManagerBean;
import com.example.collageapp.util.ImageUtil;
import com.example.collageapp.util.SizeUtil;
import com.example.collageapp.util.WindowSizeClass;
import com.example.collageapp.viewmodel.PicManagerViewModel;
import com.example.collageapp.viewmodel.XCollageViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



/**
 * <p>A fragment that shows a recycler_view_album of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     AlbumBeansDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class PicManagerDialogFragment extends BottomSheetDialogFragment {

    private XCollageViewModel mActivityViewModel;
    private PicManagerViewModel mViewModel;

    public static final String TAG = "TAG_PIC_MANAGER_DIALOG_FRAGMENT";
    private static final String KEY = "KEY_PIC_MANAGER_DIALOG_URI_LIST";

    private List<Uri> mUris;

    private WindowSizeClass mWidthWindowSizeClass = WindowSizeClass.COMPACT;

    public PicManagerDialogFragment() {

    }

    //添加多图
    ActivityResultLauncher<Intent> mAddUrisLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() != RESULT_OK) return;
        Intent intent = result.getData();
        if (intent == null) return;
        List<Uri> uris = intent.getParcelableArrayListExtra(GalleryActivity.KEY_RETURN_URIS);
        mViewModel.addUris(uris);
    });


    //编辑图片
    ActivityResultLauncher<Intent> mPicEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() != RESULT_OK) return;
        Intent intent = result.getData();
        if (intent == null) return;
        String key = intent.getStringExtra(PicEditActivity.KEY_RETURN_KEY_PIC_EDIT);
        float[] values = intent.getFloatArrayExtra(PicEditActivity.KEY_RETURN_MATRIX_VALUE_PIC_EDIT);
        Matrix matrix = new Matrix();
        matrix.setValues(values);
        mViewModel.changeBitmap(key, matrix);
    });


    public static PicManagerDialogFragment newInstance(Collection<Uri> imageUris) {
        final PicManagerDialogFragment fragment = new PicManagerDialogFragment();
        Bundle bundle = new Bundle();
        ArrayList<Uri> uris = new ArrayList<>(imageUris);
        bundle.putParcelableArrayList(KEY, uris);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置背景圆角
        setStyle(STYLE_NORMAL, R.style.AppBottomSheet);
        Bundle arguments = getArguments();
        if (arguments == null) return;
        mUris = arguments.getParcelableArrayList(KEY);
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            BottomSheetBehavior<FrameLayout> behavior = ((BottomSheetDialog) getDialog()).getBehavior();
            behavior.setHideable(true);
//            int maxH = SizeUtil.dp2px(750f);
            behavior.setPeekHeight(1080);
//            behavior.setMaxHeight(maxH);
//            behavior.setSkipCollapsed(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_gallery_manager_dialog, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mActivityViewModel = new ViewModelProvider(requireActivity()).get(XCollageViewModel.class);
        mViewModel = new ViewModelProvider(this).get(PicManagerViewModel.class);
        if (mUris == null || mUris.size() == 0) return;
        mViewModel.init(mUris);

        view.post(() -> {
            mWidthWindowSizeClass = SizeUtil.computeWindowSizeClasses(view.getMeasuredWidth());
            initView(view);
        });



    }

    private void initView(View view) {

        View okView = view.findViewById(R.id.image_view_ok);
        okView.setOnClickListener(v -> cancel());

        TextView numText = view.findViewById(R.id.text_view_num);
        TextView textView2 = view.findViewById(R.id.text_view_2);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_image);
        int num = SizeUtil.getGalleryDiallogPicNumPerColumn(mWidthWindowSizeClass);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), num));

        PicManagerAdapter adapter = new PicManagerAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(new PicManagerAdapter.PicManagerClickListener() {
            @Override
            public void delete(PicManagerBean bean) {
                mViewModel.deleteBean(bean);
            }

            @Override
            public void click(PicManagerBean bean) {
                if (bean == null) return;
                Uri uri = bean.getUri();
                if (uri == null) return;
                mPicEditLauncher.launch(PicEditActivity.getUriIntent(requireContext(), uri, bean.getId()));
            }

            @Override
            public void add() {
                int num = ImageUtil.sMaxNum - mViewModel.getNum();
                if (num <= 0) return;
                Intent intent = GalleryActivity.getAddUrisIntent(requireContext(), num);
                mAddUrisLauncher.launch(intent);
            }
        });

        mViewModel.getPicManagerBeans().observe(getViewLifecycleOwner(), beans -> {
            if (beans == null) return;
            numText.setText(GalleryManagerDialogFragment.getGalleryNumText(beans.size()));
            List<PicManagerBean> newBeans = new ArrayList<>(beans);
            boolean showDelete = newBeans.size() != 1;
            if (showDelete) {
                int color = Color.parseColor("#858585");
                textView2.setTextColor(color);
                numText.setTextColor(color);
            } else {
                int color = Color.parseColor("#DA2C2C");
                textView2.setTextColor(color);
                numText.setTextColor(color);
            }
            if (newBeans.size() < ImageUtil.sMaxNum) {
                newBeans.add(PicManagerBean.getAddBean());
            }
            adapter.submitList(newBeans);
            adapter.setShowDelete(showDelete);
        });


        //取消监听
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setOnCancelListener(d -> {
                mActivityViewModel.changeUris(mViewModel.getPicManagerBeans().getValue());
            });
        }
    }



    private void cancel() {
        Dialog dialog = getDialog();
        if (dialog == null) return;
        dialog.cancel();
    }

}