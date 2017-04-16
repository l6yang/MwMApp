package com.mwm.loyal.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mwm.loyal.R;
import com.mwm.loyal.imp.OperaOnClickListener;

public class ToastUtil {
    private static Toast toast = null;

    public static void showToast(Context context, String text, int gravity) {
        if (toast == null)
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        else {
            toast.setText(text);
            toast.setGravity(gravity, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void showToast(Context context, String text) {
        if (toast == null)
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void showToast(Context context, CharSequence text) {
        if (toast == null)
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void showToast(Context context, int resId) {
        if (toast == null)
            toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        else {
            toast.setText(resId);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void showToast(Context context, String text, Drawable drawable) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.setGravity(Gravity.CENTER, 0, 0);
            LinearLayout toastView = (LinearLayout) toast.getView();
            ImageView imageCodeProject = new ImageView(context.getApplicationContext());
            imageCodeProject.setImageDrawable(drawable);
            toastView.addView(imageCodeProject, 0);
        }
        toast.show();
    }

    public static void showDialog(final Context context, String content, boolean isFinish) {
        final AlertDialog myDialog = new AlertDialog.Builder(context).create();
        if (myDialog.isShowing())
            myDialog.dismiss();
        myDialog.show();
        myDialog.setCanceledOnTouchOutside(true);
        myDialog.setCancelable(false);
        if (myDialog.getWindow() != null)
            myDialog.getWindow().setContentView(R.layout.dialog_permission);
        TextView mContent = (TextView) myDialog.getWindow().findViewById(R.id.dialog_tv_content);
        mContent.setText(content);
        Button btn_ok = (Button) myDialog.getWindow().findViewById(R.id.dialog_btn_ok);
        View view_ok = myDialog.getWindow().findViewById(R.id.dialog_layout_ok);
        View view_cancel = myDialog.getWindow().findViewById(R.id.dialog_layout_cancel);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myDialog.isShowing())
                    myDialog.dismiss();
                ((Activity) context).finish();
            }
        });
        Button btn_cancel = (Button) myDialog.getWindow().findViewById(R.id.dialog_btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myDialog.isShowing())
                    myDialog.dismiss();
            }
        });
        view_ok.setVisibility(isFinish ? View.VISIBLE : View.GONE);
        view_cancel.setVisibility(isFinish ? View.GONE : View.VISIBLE);
        btn_cancel.setText(isFinish ? "取消" : "确定");
    }

    public static
    @NonNull
    OperateDialog operateDialog(@NonNull Context context, OperaOnClickListener listener) {
        return new OperateDialog(context, listener);
    }

    public static void hideInput(Context context, IBinder token) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        System.out.println(im.isAcceptingText());
        System.out.println(im.isActive());
    }

    public static void alwaysHideInput(Context context, IBinder token) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(token, 0);
    }

    public static void hideInputPan(Activity context, IBinder binder) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (im.isActive()) {
            im.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
        }
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void showInputPan(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
