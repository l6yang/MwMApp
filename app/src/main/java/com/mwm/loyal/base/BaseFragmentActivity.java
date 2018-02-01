package com.mwm.loyal.base;

import android.databinding.ViewDataBinding;

import com.loyal.base.ui.activity.ABasicFragActivity;

public abstract class BaseFragmentActivity<T extends ViewDataBinding> extends ABasicFragActivity {
    @Override
    public boolean isTransStatus() {
        return false;
    }
}
