package com.mwm.loyal.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.mwm.loyal.impl.IContact;
import com.mwm.loyal.impl.OperaOnClickListener;
import com.loyal.base.widget.BaseDialog;

public class ToastUtil implements IContact {
    private static Toast toast = null;

    public static void showToast(@NonNull Context context, @NonNull String text) {
        if (toast == null)
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void showToast(@NonNull Context context, @NonNull CharSequence text) {
        showToast(context, text.toString());
    }

    public static void showToast(@NonNull Context context, @StringRes int resId) {
        if (toast == null)
            toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        else {
            toast.setText(resId);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void showDialog(final Activity context, String content, final boolean isFinish) {
        BaseDialog.Builder builder = new BaseDialog.Builder(context);
        builder.setContent(content).setOutsideCancel(false).setOutsideCancel(false);
        builder.setBottomBtnType(isFinish ? TYPE.RIGHT : TYPE.LEFT).setBtnText(new String[]{"确定"}).setClickListener(new BaseDialog.DialogClickListener() {
            @Override
            public void onClick(BaseDialog dialog, View view, Object object) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                if (isFinish) context.finish();
            }
        });
        builder.create().show();
    }

    public static
    @NonNull
    OperateDialog operateDialog(@NonNull Context context, OperaOnClickListener listener) {
        return new OperateDialog(context, listener);
    }

    public static void hideInput(@NonNull Context context, @NonNull IBinder token) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void alwaysHideInput(@NonNull Context context, @NonNull IBinder token) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(token, 0);
    }

    public static void showInputPan(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
