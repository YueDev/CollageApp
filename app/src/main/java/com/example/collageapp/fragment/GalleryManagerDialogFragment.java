package com.example.collageapp.fragment;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collageapp.R;
import com.example.collageapp.adapter.GalleryManagerAdapter;
import com.example.collageapp.util.SizeUtil;
import com.example.collageapp.util.WindowSizeClass;
import com.example.collageapp.viewmodel.GalleryViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;



/**
 * <p>A fragment that shows a recycler_view_album of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     AlbumBeansDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class GalleryManagerDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = "TAG_GALLERY_MANAGER_DIALOG_FRAGMENT";

    private GalleryViewModel mViewModel;

    private WindowSizeClass mWidthWindowSizeClass = WindowSizeClass.COMPACT;


    public GalleryManagerDialogFragment() {

    }

    public static GalleryManagerDialogFragment newInstance() {
        final GalleryManagerDialogFragment fragment = new GalleryManagerDialogFragment();
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
            int maxH = SizeUtil.dp2px(550f);
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

        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_gallery_manager_dialog, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(GalleryViewModel.class);

        View okView = view.findViewById(R.id.image_view_ok);
        okView.setOnClickListener(v -> cancel());

        view.post(() -> {
            mWidthWindowSizeClass = SizeUtil.computeWindowSizeClasses(view.getMeasuredWidth());
            initView(view);
        });


    }

    private void initView(View view) {
        //        View deleteView = view.findViewById(R.id.image_view_delete);
//        deleteView.setOnClickListener(v -> {
//            mViewModel.deleteAll();
//            cancel();
//        });

        TextView numText = view.findViewById(R.id.text_view_num);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_image);
        int num = SizeUtil.getGalleryDiallogPicNumPerColumn(mWidthWindowSizeClass);

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), num));

        GalleryManagerAdapter adapter = new GalleryManagerAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(uri -> {
            boolean isEmpty = mViewModel.delete(uri);
            if (isEmpty) cancel();
        });

        mViewModel.getSelectUris().observe(getViewLifecycleOwner(), uris -> {
            if (uris == null) return;
            numText.setText(getGalleryNumText(uris.size()));
            List<Uri> newUris = new ArrayList<>(uris);
            adapter.submitList(newUris);
        });

    }

    private void cancel() {
        Dialog dialog = getDialog();
        if (dialog == null) return;
        dialog.cancel();
    }


    public static String getGalleryNumText(int size) {
        return "(" + size + ")";
    }
}