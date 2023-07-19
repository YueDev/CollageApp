package com.example.collageapp.fragment;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.example.collageapp.R;
import com.example.collageapp.util.SizeUtil;
import com.example.collageapp.viewmodel.ProViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;




/**
 * <p>A fragment that shows a recycler_view_album of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     ProDialogFragment.newInstance().show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class ProDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = "TAG_PRO_DIALOG_FRAGMENT";
    private static final String KEY_REMOVE_WATER = "key_remove_water_pro_dialog";

    private ProViewModel mProViewModel;

    private boolean mIsShowRemoveWater;

    public ProDialogFragment() {

    }

    public static ProDialogFragment newInstance(boolean showRemoveWater) {
        final ProDialogFragment fragment = new ProDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_REMOVE_WATER, showRemoveWater);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheet);
        Bundle arguments = getArguments();
        if (arguments != null) mIsShowRemoveWater = arguments.getBoolean(KEY_REMOVE_WATER, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            BottomSheetBehavior<FrameLayout> behavior = ((BottomSheetDialog) getDialog()).getBehavior();
            behavior.setHideable(true);
            int maxH = SizeUtil.dp2px(420f);
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

        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_pro_dialog, container, false);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mProViewModel = new ViewModelProvider(requireActivity()).get(ProViewModel.class);

        //是否显示移除水印
        View removeWaterLayout = view.findViewById(R.id.layout_remove_water);
        removeWaterLayout.setVisibility(mIsShowRemoveWater ? View.VISIBLE : View.GONE);

        view.findViewById(R.id.image_view_close).setOnClickListener(v -> cancel());

        LottieAnimationView mLottieView = view.findViewById(R.id.lottie_remove_water);
        mLottieView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mLottieView.getSpeed() > 0) {
                    //正序播放完成
                    mLottieView.setProgress(0f);
                    mProViewModel.removeWater();
                    cancel();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        View removeView = view.findViewById(R.id.text_view_remove);

        removeView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mLottieView.setSpeed(1.0f);
                mLottieView.playAnimation();
            }
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                mLottieView.setSpeed(-2.0f);
            }
            return true;
        });

    }


    private void cancel() {
        Dialog dialog = getDialog();
        if (dialog == null) return;
        dialog.cancel();
    }

}