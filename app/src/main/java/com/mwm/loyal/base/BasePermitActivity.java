package com.mwm.loyal.base;

import android.databinding.ViewDataBinding;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

public abstract class BasePermitActivity<T extends ViewDataBinding> extends BaseActivity<T> implements RationaleListener {

    public final void requestPermissions(@IntRange int reqCode, @Size(min = 1) @NonNull String[] permissions) {
        AndPermission.with(this)
                .requestCode(reqCode)
                .permission(permissions)
                .rationale(this)
                .callback(this)
                .start();
    }

    @Override
    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
        AndPermission.rationaleDialog(this, rationale).show();
    }
}
