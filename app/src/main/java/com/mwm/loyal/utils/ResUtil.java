package com.mwm.loyal.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.mwm.loyal.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

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
    private static InputStream openAssetFile(Context context, String fileName) {
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
        BufferedReader bufferedReader = null;
        try {
            StringBuilder buffer = new StringBuilder();
            bufferedReader = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            IOUtil.closeStream(bufferedReader);
            IOUtil.closeStream(is);
        }
    }

    /**
     * {@link #openAssetFile(Context, String)}
     */
    public static String getStrFromRes(Context context, String fileName) {
        InputStream is = openAssetFile(context, fileName);
        if (is == null)
            return "";
        return getStrFromRes(is);
    }

    public static Drawable getBackground(Context context) {
        return getBackground(context, R.mipmap.img_blue_background, true);
    }

    public static Drawable getBackground(Context context, int resId, boolean scale) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        if (!scale)
            return new BitmapDrawable(context.getResources(), bitmap);
        else {
            Bitmap newBitmap = createScaledBitmap(bitmap, true);
            if (bitmap != null)
                bitmap.recycle();
            return new BitmapDrawable(context.getResources(), newBitmap);
        }
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
