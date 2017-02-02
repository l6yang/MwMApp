package com.mwm.loyal.utils;

import android.app.Activity;
import android.content.Intent;

public class IntentUtil {
    public static void toStartActivity(Activity currentActivity, Class<?> nextActivity) {
        toStartActivity(currentActivity, new Intent(currentActivity, nextActivity));
    }

    public static void toStartActivityForResult(Activity currentActivity, Class<?> nextActivity, int reqCode) {
        toStartActivityForResult(currentActivity, new Intent(currentActivity, nextActivity), reqCode);
    }

    public static void toStartActivity(Activity currentActivity, Intent intent) {
        intent.putExtras(currentActivity.getIntent());
        currentActivity.startActivity(intent);
    }

    public static void toStartActivityForResult(Activity currentActivity, Intent intent, int reqCode) {
        intent.putExtras(currentActivity.getIntent());
        currentActivity.startActivityForResult(intent, reqCode);
    }
}
