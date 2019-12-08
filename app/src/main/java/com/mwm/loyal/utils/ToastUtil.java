package com.mwm.loyal.utils;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import android.view.inputmethod.InputMethodManager;

import com.mwm.loyal.impl.OperaOnClickListener;

public class ToastUtil {

    public static
    @NonNull
    OperateDialog operateDialog(@NonNull Context context, OperaOnClickListener listener) {
        return new OperateDialog(context, listener);
    }

    public static void showInputPan(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != imm)
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
