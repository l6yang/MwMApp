package com.mwm.loyal.base;

import android.view.View;

import butterknife.ButterKnife;

public class BaseViewHolder {
    private View itemView;

    public BaseViewHolder(View view) {
        ButterKnife.bind(this, view);
        this.itemView = view;
    }

    public View getView() {
        return itemView;
    }
}
