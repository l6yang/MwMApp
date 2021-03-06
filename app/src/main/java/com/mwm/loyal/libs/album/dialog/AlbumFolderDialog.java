package com.mwm.loyal.libs.album.dialog;

import android.content.Context;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.List;

import com.mwm.loyal.R;
import com.mwm.loyal.libs.album.adapter.DialogFolderAdapter;
import com.mwm.loyal.libs.album.entity.AlbumFolder;
import com.mwm.loyal.libs.album.impl.OnCompatItemClickListener;
import com.mwm.loyal.libs.album.task.Poster;
import com.mwm.loyal.libs.album.util.SelectorUtils;

public class AlbumFolderDialog extends BottomSheetDialog {

    private int checkPosition = 0;
    private BottomSheetBehavior bottomSheetBehavior;
    private OnCompatItemClickListener mItemClickListener;

    private boolean isOpen = true;

    public AlbumFolderDialog(@NonNull Context context, @ColorInt int toolbarColor, @Nullable List<AlbumFolder> albumFolders, @Nullable OnCompatItemClickListener itemClickListener) {
        super(context, R.style.AlbumDialogStyle_Folder);
        setContentView(R.layout.album_dialog_floder);
        mItemClickListener = itemClickListener;
        fixRestart();
        RecyclerView rvContentList = findViewById(R.id.rv_content_list);
        if (rvContentList != null) {
            rvContentList.setHasFixedSize(true);
            rvContentList.setLayoutManager(new LinearLayoutManager(getContext()));
            rvContentList.setAdapter(new DialogFolderAdapter(SelectorUtils.createColorStateList(ContextCompat.getColor(context, R.color.albumPrimaryBlack), toolbarColor), albumFolders, new OnCompatItemClickListener() {
                @Override
                public void onItemClick(final View view, final int position) {
                    if (isOpen) { // 反应太快，按钮点击效果出不来，故加延迟。
                        isOpen = false;
                        Poster.getInstance().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                behaviorHide();
                                if (mItemClickListener != null && checkPosition != position) {
                                    checkPosition = position;
                                    mItemClickListener.onItemClick(view, position);
                                }
                                isOpen = true;
                            }
                        }, 200);
                    }
                }
            }));
        }
        setStatusBarColor(toolbarColor);
    }

    private void setStatusBarColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            final Window window = getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
                window.setNavigationBarColor(ContextCompat.getColor(getContext(), R.color.albumPrimaryBlack));
            }
        }
    }

    /**
     * 修复不能重新显示的bug。
     */
    private void fixRestart() {
        View view = findViewById(R.id.design_bottom_sheet);
        if (view != null)
            bottomSheetBehavior = BottomSheetBehavior.from(view);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }


    /**
     * 关闭dialog。
     */
    public void behaviorHide() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}
