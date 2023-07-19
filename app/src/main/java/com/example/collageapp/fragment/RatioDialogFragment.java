package com.example.collageapp.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.collageapp.R;
import com.example.collageapp.activity.XCollageActivity;
import com.example.collageapp.adapter.RatioPickerAdapter;

import cn.simonlee.widget.scrollpicker.ScrollPickerView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RatioDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatioDialogFragment extends DialogFragment {


    private static final String KEY_RATIO_DIALOG_W = "KEY_RATIO_DIALOG_W";
    private static final String KEY_RATIO_DIALOG_H = "KEY_RATIO_DIALOG_H";

    private int mW = 5;
    private int mH = 16;

    public static final String TAG = "TAG_RATIO_DIALOG_FRAGMENT";
    private ScrollPickerView mPickerW;
    private ScrollPickerView mPickerH;
    private RatioPickerAdapter mAdapter;


    public RatioDialogFragment() {
        // Required empty public constructor
    }

    public static RatioDialogFragment newInstance() {
        return newInstance(1, 1);
    }

    public static RatioDialogFragment newInstance(int w, int h) {
        RatioDialogFragment fragment = new RatioDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_RATIO_DIALOG_W, w);
        bundle.putInt(KEY_RATIO_DIALOG_H, h);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments == null) return;
        mW = arguments.getInt(KEY_RATIO_DIALOG_W, 1);
        mH = arguments.getInt(KEY_RATIO_DIALOG_H, 1);
    }

    @Override
    public void onStart() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            //设置dialog背景透明，这样圆角才能正常显示
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //默认dialog出现没有动画，这里设置一个渐变动画，必须在onStart之前设置 动画才能显示
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogFragmentWindowAnimation;
        }
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ratio_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new RatioPickerAdapter();

        mPickerW = view.findViewById(R.id.view_picker_w);
        mPickerW.setAdapter(mAdapter);
        int pW = mAdapter.getPosition(mW);
        if (pW < 0 || pW > mAdapter.getCount()) {
            pW = 0;
        }
        mPickerW.setSelectedPosition(pW);

        mPickerH = view.findViewById(R.id.view_picker_h);
        mPickerH.setAdapter(mAdapter);
        int pH = mAdapter.getPosition(mH);
        if (pH < 0 || pH > mAdapter.getCount()) {
            pH = 0;
        }
        mPickerH.setSelectedPosition(pH);

        View okView = view.findViewById(R.id.image_view_ok);
        okView.setOnClickListener(this::changeCustomRatio);

        View cancelView = view.findViewById(R.id.image_view_cancel);
        cancelView.setOnClickListener(v -> {
            cancel();
        });
    }


    private void changeCustomRatio(View v) {
        try {
            int w = mAdapter.getPosition(mPickerW.getSelectedPosition());
            int h = mAdapter.getPosition(mPickerH.getSelectedPosition());
            ((XCollageActivity)requireActivity()).changeCustomRatio(w, h);
            cancel();
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