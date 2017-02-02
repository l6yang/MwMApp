package com.mwm.loyal.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import com.mwm.loyal.R;

public class ResUtil {
    /**
     * @param variableName video
     * @param c            mipmap.class
     */
    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * @param fileName json/index.json
     */
    public static InputStream openAssetFile(Context context, String fileName) {
        try {
            return context.getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getStrFromRes(InputStream is) {
        if (is == null)
            return "";
        StringBuilder str = new StringBuilder();
        byte b[] = new byte[1024];
        int len;
        try {
            while ((len = is.read(b)) != -1) {
                str.append(new String(b, 0, len, "utf-8"));
            }
            return str.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * {@link #openAssetFile(Context, String)}
     */
    public static String getStrFromRes(Context context, String fileName) {
        InputStream is = openAssetFile(context, fileName);
        if (is == null)
            return "";
        StringBuilder str = new StringBuilder();
        byte b[] = new byte[1024];
        int len;
        try {
            while ((len = is.read(b)) != -1) {
                str.append(new String(b, 0, len, "utf-8"));
            }
            return str.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Drawable getBackground(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.img_bg_login);
        Bitmap newBitmap = createScaledBitmap(bitmap, true);
        if (bitmap != null)
            bitmap.recycle();
        return new BitmapDrawable(context.getResources(), newBitmap);
    }

    private static Bitmap createScaledBitmap(Bitmap src, boolean filter) {
        int scaleRatio = 10;
        int blurRadius = 8;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(src,
                src.getWidth() / scaleRatio,
                src.getHeight() / scaleRatio,
                filter);
        return ImageUtil.doBlur(scaledBitmap, blurRadius, true);
    }
}
