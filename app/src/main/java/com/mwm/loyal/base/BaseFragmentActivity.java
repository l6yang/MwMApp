package com.mwm.loyal.base;

import android.databinding.ViewDataBinding;
import android.os.Bundle;

import com.mwm.loyal.imp.Frag2ActListener;

public abstract class BaseFragmentActivity<T extends ViewDataBinding> extends BaseActivity<T> implements Frag2ActListener {

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }
}
