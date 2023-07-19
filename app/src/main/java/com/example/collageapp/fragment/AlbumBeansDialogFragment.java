package com.example.collageapp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collageapp.R;
import com.example.collageapp.adapter.AlbumBeanAdapter;
import com.example.collageapp.util.SizeUtil;
import com.example.collageapp.viewmodel.GalleryViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;




/**
 * <p>A fragment that shows a recycler_view_album of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     AlbumBeansDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class AlbumBeansDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = "TAG_ALBUM_BEANS_DIALOG_FRAGMENT";

    private GalleryViewModel mViewModel;

    public AlbumBeansDialogFragment() {

    }

    public static AlbumBeansDialogFragment newInstance() {
        final AlbumBeansDialogFragment fragment = new AlbumBeansDialogFragment();
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
            int maxH = SizeUtil.dp2px(480f);
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

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_album_beans_dialog, container, false);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(GalleryViewModel.class);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_album);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        AlbumBeanAdapter adapter = new AlbumBeanAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setAlbumClickListener(bean -> {
            cancel();
            mViewModel.changeAlbum(requireContext().getContentResolver(), bean);
        });

        mViewModel.getAlbumBeans(requireContext()).observe(getViewLifecycleOwner(), adapter::setAlbumBeans);

    }


    private void cancel() {
        Dialog dialog = getDialog();
        if (dialog == null) return;
        dialog.cancel();
    }




}