package com.example.collageapp.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collageapp.R;
import com.example.collageapp.activity.HomeActivity;
import com.example.collageapp.activity.XCollageActivity;
import com.example.collageapp.adapter.ShareAdapter;
import com.example.collageapp.bean.LoadingState;
import com.example.collageapp.bean.SaveBean;
import com.example.collageapp.bean.ShareBean;
import com.example.collageapp.util.ShareUtil;
import com.example.collageapp.util.SizeUtil;
import com.example.collageapp.viewmodel.ProViewModel;
import com.example.collageapp.viewmodel.SaveViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;



/**
 * <p>A fragment that shows a recycler_view_album of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     AlbumBeansDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class SaveDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = "TAG_SAVE_DIALOG_FRAGMENT";

    private SaveViewModel mSaveViewModel;
    private ProViewModel mProViewModel;

    //文字层
    private View mHighView;
    private View mNormalView;
    private View mLowView;

    private TextView mHighTextView;
    private TextView mHighTextView2;
    private TextView mNormalTextView;
    private TextView mNormalTextView2;
    private TextView mLowTextView;
    private TextView mLowTextView2;



    private View mSaveProView;
    //pro选中
    private Drawable mProIconDrawable1;
    //pro未选中
    private Drawable mProIconDrawable2;


    //颜色 选中 未选中
    private int[] mSaveTypeColor = new int[]{
            Color.parseColor("#54B3A8"), Color.parseColor("#999999")
    };


    //save按钮层
    private View mSaveButton;

    private View mSaveView;
    private View mLoadingView;
    private View mSavedView;


    //保存按钮的背景  未保存 已保存
    private int[] mSaveTypeBgResId = new int[]{
            R.drawable.bg_save, R.drawable.bg_saved
    };


    //分享rv
    private RecyclerView mShareRecyclerView;
    private ShareAdapter mShareAdapter;

    public SaveDialogFragment() {

    }

    public static SaveDialogFragment newInstance() {
        final SaveDialogFragment fragment = new SaveDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheet);
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            BottomSheetBehavior<FrameLayout> behavior = ((BottomSheetDialog) getDialog()).getBehavior();
            behavior.setHideable(true);
            int maxH = SizeUtil.dp2px(415f);
            behavior.setPeekHeight(maxH);
            behavior.setMaxHeight(maxH);
//            behavior.setSkipCollapsed(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_save_dialog, container, false);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSaveViewModel = new ViewModelProvider(requireActivity()).get(SaveViewModel.class);
        mProViewModel = new ViewModelProvider(requireActivity()).get(ProViewModel.class);

        mProIconDrawable1 = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_save_pro_1, null);
        mProIconDrawable2 = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_save_pro_2, null);

        mSaveProView = view.findViewById(R.id.image_view_save_pro);
        mSaveProView.setOnClickListener(v -> {
            ProDialogFragment.newInstance(false).show(getParentFragmentManager(), ProDialogFragment.TAG);
        });

        //文字层
        mHighView = view.findViewById(R.id.layout_high);
        mNormalView = view.findViewById(R.id.layout_normal);
        mLowView = view.findViewById(R.id.layout_low);

        mHighTextView = view.findViewById(R.id.text_view_high);
        mHighTextView2 = view.findViewById(R.id.text_view_high2);
        mNormalTextView = view.findViewById(R.id.text_view_normal);
        mNormalTextView2 = view.findViewById(R.id.text_view_normal2);
        mLowTextView = view.findViewById(R.id.text_view_low);
        mLowTextView2 = view.findViewById(R.id.text_view_low2);

        mSaveButton = view.findViewById(R.id.layout_save);

        mSaveView = view.findViewById(R.id.text_view_save);
        mLoadingView = view.findViewById(R.id.progress_bar_loading);
        mSavedView = view.findViewById(R.id.text_view_saved);


        mProViewModel.getIsProUser().observe(getViewLifecycleOwner(), isProUser -> {
            if (isProUser) {
                mNormalTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                mHighTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                mSaveProView.setVisibility(View.GONE);
            }
        });

        mSaveViewModel.getSaveBean().observe(getViewLifecycleOwner(), saveBean -> {
            if (saveBean == null) return;

            switch (saveBean.getSaveType()) {
                case LOW:
                    mLowTextView.setTextColor(mSaveTypeColor[0]);
                    mLowTextView2.setTextColor(mSaveTypeColor[0]);
                    mHighTextView.setTextColor(mSaveTypeColor[1]);
                    mHighTextView2.setTextColor(mSaveTypeColor[1]);
                    mNormalTextView.setTextColor(mSaveTypeColor[1]);
                    mNormalTextView2.setTextColor(mSaveTypeColor[1]);
                    if (mProViewModel.isProUser()) {
                        mNormalTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        mHighTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    } else {
                        mNormalTextView.setCompoundDrawablesWithIntrinsicBounds(mProIconDrawable2, null, null, null);
                        mHighTextView.setCompoundDrawablesWithIntrinsicBounds(mProIconDrawable2, null, null, null);
                    }
                    mSaveProView.setVisibility(View.GONE);
                    break;
                case NORMAL:
                    mNormalTextView.setTextColor(mSaveTypeColor[0]);
                    mNormalTextView2.setTextColor(mSaveTypeColor[0]);
                    mLowTextView.setTextColor(mSaveTypeColor[1]);
                    mLowTextView2.setTextColor(mSaveTypeColor[1]);
                    mHighTextView.setTextColor(mSaveTypeColor[1]);
                    mHighTextView2.setTextColor(mSaveTypeColor[1]);
                    if (mProViewModel.isProUser()) {
                        mNormalTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        mHighTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        mSaveProView.setVisibility(View.GONE);
                    } else {
                        mNormalTextView.setCompoundDrawablesWithIntrinsicBounds(mProIconDrawable1, null, null, null);
                        mHighTextView.setCompoundDrawablesWithIntrinsicBounds(mProIconDrawable2, null, null, null);
                        mSaveProView.setVisibility(View.VISIBLE);
                    }
                    break;
                case HIGH:
                    mHighTextView.setTextColor(mSaveTypeColor[0]);
                    mHighTextView2.setTextColor(mSaveTypeColor[0]);
                    mNormalTextView.setTextColor(mSaveTypeColor[1]);
                    mNormalTextView2.setTextColor(mSaveTypeColor[1]);
                    mLowTextView.setTextColor(mSaveTypeColor[1]);
                    mLowTextView2.setTextColor(mSaveTypeColor[1]);
                    if (mProViewModel.isProUser()) {
                        mNormalTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        mHighTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        mSaveProView.setVisibility(View.GONE);
                    } else {
                        mNormalTextView.setCompoundDrawablesWithIntrinsicBounds(mProIconDrawable2, null, null, null);
                        mHighTextView.setCompoundDrawablesWithIntrinsicBounds(mProIconDrawable1, null, null, null);
                        mSaveProView.setVisibility(View.VISIBLE);
                    }
                    break;
            }

            switch (saveBean.getSaveSate()) {
                case INIT:
                    enableCancel();
                    mSaveView.setVisibility(View.VISIBLE);
                    mLoadingView.setVisibility(View.GONE);
                    mSavedView.setVisibility(View.GONE);
                    ViewCompat.setBackground(mSaveButton, ContextCompat.getDrawable(requireContext(), mSaveTypeBgResId[0]));
                    break;
                case LOADING:
                    disableCancel();
                    mSaveView.setVisibility(View.GONE);
                    mLoadingView.setVisibility(View.VISIBLE);
                    mSavedView.setVisibility(View.GONE);
                    ViewCompat.setBackground(mSaveButton, ContextCompat.getDrawable(requireContext(), mSaveTypeBgResId[0]));
                    break;
                case SUCCESS:
                    enableCancel();
                    mSaveView.setVisibility(View.GONE);
                    mLoadingView.setVisibility(View.GONE);
                    mSavedView.setVisibility(View.VISIBLE);
                    ViewCompat.setBackground(mSaveButton, ContextCompat.getDrawable(requireContext(), mSaveTypeBgResId[1]));
                    break;
                case ERROR:
                    enableCancel();
                    mSaveView.setVisibility(View.VISIBLE);
                    mLoadingView.setVisibility(View.GONE);
                    mSavedView.setVisibility(View.GONE);
                    ViewCompat.setBackground(mSaveButton, ContextCompat.getDrawable(requireContext(), mSaveTypeBgResId[0]));
                    break;
            }
        });


        mLowView.setOnClickListener(v -> {
                    LiveData<SaveBean> saveBean = mSaveViewModel.getSaveBean();
                    if (saveBean == null) return;
                    SaveBean value = saveBean.getValue();
                    if (value == null) return;
                    LoadingState saveSate = value.getSaveSate();
                    if (saveSate == LoadingState.LOADING) return;
                    mSaveViewModel.setLow();
                }
        );

        mNormalView.setOnClickListener(v -> {
                    LiveData<SaveBean> saveBean = mSaveViewModel.getSaveBean();
                    if (saveBean == null) return;
                    SaveBean value = saveBean.getValue();
                    if (value == null) return;
                    LoadingState saveSate = value.getSaveSate();
                    if (saveSate == LoadingState.LOADING) return;
                    mSaveViewModel.setNormal();
                }
        );

        mHighView.setOnClickListener(v -> {
            LiveData<SaveBean> saveBean = mSaveViewModel.getSaveBean();
            if (saveBean == null) return;
            SaveBean value = saveBean.getValue();
            if (value == null) return;
            LoadingState saveSate = value.getSaveSate();
            if (saveSate == LoadingState.LOADING) return;
            mSaveViewModel.setHigh();
        });

        mSaveButton.setOnClickListener(v -> {
            LoadingState saveState = mSaveViewModel.getSaveState();
            if (saveState == LoadingState.LOADING || saveState == LoadingState.SUCCESS) return;

            try {
                ((XCollageActivity) requireActivity()).save();
            } catch (Exception e) {
                e.printStackTrace();
                mSaveViewModel.setSaveState(LoadingState.ERROR);
            }
        });

        mShareRecyclerView = view.findViewById(R.id.recycler_view_share);
        mShareRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 5));

        mShareAdapter = new ShareAdapter(shareBean -> {
            if (shareBean == null) return;
            switch (shareBean.getShareType()) {

                case INSTAGRAM:
                    Uri uri1 = mSaveViewModel.getSaveUri();
                    if (uri1 == null) return;
                    ShareUtil.shareToInstagram(getContext(), uri1);
                    break;
                case WHATSAPP:
                    Uri uri2 = mSaveViewModel.getSaveUri();
                    if (uri2 == null) return;
                    ShareUtil.shareToWhatsApp(getContext(), uri2);
                    break;
                case MESSENGER:
                    Uri uri3 = mSaveViewModel.getSaveUri();
                    if (uri3 == null) return;
                    ShareUtil.shareToMessenger(getContext(), uri3);
                    break;
                case FACEBOOK:
                    Uri uri4 = mSaveViewModel.getSaveUri();
                    if (uri4 == null) return;
                    ShareUtil.shareToFacebook(getContext(), uri4);
                    break;
                case MORE:
                    Uri uri5 = mSaveViewModel.getSaveUri();
                    if (uri5 == null) return;
                    ShareUtil.shareToOther(getContext(), uri5);
                    break;
                case ANOTHER:
                    ((XCollageActivity) requireActivity()).makeAnother();
                    break;
                case HOME:
                    Intent intent = new Intent(requireContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    break;
            }
        });
        mShareRecyclerView.setAdapter(mShareAdapter);


        List<ShareBean> shareBeans = ShareUtil.getShareBeans();
        mShareAdapter.submitList(shareBeans);

//        Uri uri = mSaveViewModel.getSaveUri();
//        if (uri == null) return;
//        ShareUtil.shareToInstagram(getContext(), uri);


    }


    //开启拖动 点击取消
    private void enableCancel() {
        try {
            Dialog dialog = getDialog();
            dialog.setCancelable(true);
            BottomSheetBehavior<FrameLayout> behavior = ((BottomSheetDialog) dialog).getBehavior();
            behavior.setDraggable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //禁止拖动和点击取消
    private void disableCancel() {
        try {
            Dialog dialog = getDialog();
            dialog.setCancelable(false);
            BottomSheetBehavior<FrameLayout> behavior = ((BottomSheetDialog) dialog).getBehavior();
            behavior.setDraggable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void cancel() {
        Dialog dialog = getDialog();
        if (dialog == null) return;
        dialog.cancel();
    }


}