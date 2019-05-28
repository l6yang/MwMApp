package com.mwm.loyal.adapter;

import android.content.Context;
import android.view.View;

import com.mwm.loyal.BR;
import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseBindAdapter;
import com.mwm.loyal.beans.LinkBean;
import com.mwm.loyal.databinding.ActivitySettingsBinding;

public class SettingsLinkAdapter extends BaseBindAdapter<LinkBean, ActivitySettingsBinding> {
    public SettingsLinkAdapter(Context context, String json, Class<LinkBean> t, boolean fromRes) {
        super(context, json, t, fromRes);
    }

    @Override
    public int variableId() {
        return BR.linkBean;
    }

    @Override
    public int adapterLayout() {
        return R.layout.item_setting_link;
    }
}
