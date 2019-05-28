package com.mwm.loyal.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;

import com.loyal.base.ui.activity.ABasicFullScreenActivity;
import com.mwm.loyal.impl.IContactImpl;

import butterknife.ButterKnife;

public abstract class BaseFullScreenActivity<T extends ViewDataBinding> extends ABasicFullScreenActivity implements IContactImpl {
    protected T binding;

    @Override
    public void setViewByLayoutRes() {
        binding = DataBindingUtil.setContentView(this, actLayoutRes());
    }

    @Override
    public void bindViews() {
        ButterKnife.bind(this);
    }

}
