package com.mwm.loyal.impl;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Spinner;

public interface UIInterface extends Contact {
    String subEndTime(@NonNull String time);

    String getSpinSelectStr(Spinner spinner, @NonNull String key);

    String encodeStr2Utf(@NonNull String string);

    String decodeStr2Utf(@NonNull String string);

    void showToast(@NonNull String text);

    void showToast(@StringRes int resId);

    void showErrorDialog(@NonNull String text, boolean finish);

    void showErrorDialog(@NonNull String text);

    void showErrorDialog(@NonNull String text, Throwable e, boolean finish);

    void showErrorDialog(@NonNull String text, Throwable e);

    void showDialog(@NonNull String text);

    void showDialog(@NonNull String text, boolean finish);

    String replaceNull(CharSequence sequence);

}
