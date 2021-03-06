package com.mwm.loyal.base;

import android.view.View;

import com.loyal.basex.ui.fragment.ABasicFragment;
import com.mwm.loyal.impl.IContactImpl;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends ABasicFragment implements View.OnClickListener, IContactImpl {

    private Unbinder unbinder;

    @Override
    public void bindViews(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void unbind() {
        if (null != unbinder)
            unbinder.unbind();
    }
}
