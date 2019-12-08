package com.mwm.loyal.utils;

import android.os.Handler;
import androidx.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;

public class UIHandler {
    public static void delay2Enable(@NonNull final View view) {
        delay2Enable(view, 1000);
    }

    public static void delay2Enable(@NonNull final View view, long delayTime) {
        view.setEnabled(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, delayTime);
    }

    public static void delay2Enable(@NonNull final MenuItem menuItem) {
        delay2Enable(menuItem, 1000);
    }

    public static void delay2Enable(@NonNull final MenuItem menuItem, long delayTime) {
        menuItem.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                menuItem.setEnabled(true);
            }
        }, delayTime);
    }
}
