package com.mwm.loyal.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.loyal.base.impl.CommandViewClickListener;
import com.loyal.base.impl.IBaseContacts;
import com.loyal.base.widget.CommandDialog;
import com.mwm.loyal.impl.OperaOnClickListener;

public class ToastUtil implements IBaseContacts {
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
        CommandDialog.Builder builder = new CommandDialog.Builder(context);
        builder.setContent(content).setOutsideCancel(false).setOutsideCancel(false);
        builder.setBottomBtnType(isFinish ? TypeImpl.RIGHT : TypeImpl.LEFT)
                .setBtnText(new String[]{"确 定"}).setClickListener(new CommandViewClickListener() {
            @Override
            public void onViewClick(CommandDialog dialog, View view, Object tag) {
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
        if (null != im)
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void alwaysHideInput(@NonNull Context context, @NonNull IBinder token) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != im)
            im.hideSoftInputFromWindow(token, 0);
    }

    public static void showInputPan(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != imm)
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
