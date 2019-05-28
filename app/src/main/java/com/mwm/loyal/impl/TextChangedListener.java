package com.mwm.loyal.impl;

import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

public class TextChangedListener implements TextWatcher {
    private final ImageView imageView;
    private final AppCompatEditText editText;

    public TextChangedListener(ImageView imageView) {
        this(null, imageView);
    }

    public TextChangedListener(AppCompatEditText editText, ImageView imageView) {
        this.editText = editText;
        this.imageView = imageView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable != null) {
            imageView.setVisibility(editable.length() > 0 ? View.VISIBLE : View.GONE);
            if (null != editText)
                editText.setSelection(editable.length());
        }
    }
}