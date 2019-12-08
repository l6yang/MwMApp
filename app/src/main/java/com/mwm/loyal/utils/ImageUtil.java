package com.mwm.loyal.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.loyal.kit.OutUtil;
import com.loyal.kit.StreamUtil;
import com.mwm.loyal.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ImageUtil {

    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;
        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int[] vmin = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int[] dv = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }
        yw = yi = 0;
        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;
        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;
            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = r1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return bitmap;
    }

    public static Bitmap createScaledBitmap(Bitmap src, int dstWidth, int dstHeight, boolean filter) {
        int scaleRatio = 10;
        int blurRadius = 8;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(src,
                dstWidth / scaleRatio,
                dstHeight / scaleRatio,
                filter);
        return doBlur(scaledBitmap, blurRadius, true);
    }

    public static Bitmap releasePics(String path, int baseSize) {
        Bitmap bmp;
        // 图片参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 只计算几何尺寸，不返回bitmap,不占内存.
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // 得到图片的宽、高
        int w = options.outWidth;
        int h = options.outHeight;
        OutUtil.println("--img src,w:" + w + " h:" + h);
        // 最小边
        int min = w < h ? w : h;
        // 压缩比。
        int rate = min / baseSize;
        if (rate <= 0) {
            rate = 1;
        }
        // 设置压缩参数。
        options.inSampleSize = rate;
        options.inJustDecodeBounds = false;
        // 压缩。
        bmp = BitmapFactory.decodeFile(path, options);
        if (bmp != null) {
            OutUtil.println("--img dst,w:" + bmp.getWidth() + " h:" + bmp.getHeight());
        }
        return bmp;
    }

    public static String saveToFile(String fileFolderStr, Bitmap croppedImage) {
        try {
            File jpgFile = new File(fileFolderStr);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            int options = 100;
            // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            while (baos.toByteArray().length / 1024 > 80) {
                // 重置baos即清空baos
                baos.reset();
                // 每次都减少10
                options -= 10;
                // 这里压缩options%，把压缩后的数据存放到baos中
                croppedImage.compress(Bitmap.CompressFormat.JPEG, options, baos);
            }
            FileOutputStream outputStream = new FileOutputStream(jpgFile);
            outputStream.write(baos.toByteArray());
            StreamUtil.closeStream(outputStream);
            FileUtil.deleteFile(FileUtil.path_temp, "camera_tmp.jpg");
            return jpgFile.getPath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String saveToFile(File jpgFile, InputStream inputStream) {
        if (inputStream == null)
            return "";
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            byte[] bytes = new byte[1024];
            // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            while ((len = inputStream.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
            FileOutputStream outputStream = new FileOutputStream(jpgFile);
            outputStream.write(bos.toByteArray());
            StreamUtil.closeStream(outputStream);
            return jpgFile.getPath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            StreamUtil.closeStream(inputStream);
        }
    }

    public static String saveToFile(File jpgFile, byte[] bytes) {
        if (null == bytes)
            return "";
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            bos.write(bytes, 0, bytes.length);
            FileOutputStream outputStream = new FileOutputStream(jpgFile);
            outputStream.write(bos.toByteArray());
            StreamUtil.closeStream(outputStream);
            return jpgFile.getPath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
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
        return doBlur(scaledBitmap, blurRadius, true);
    }

    public static void clearFrescoTemp() {
        Fresco.getImagePipeline().clearCaches();
        Fresco.getImagePipeline().clearDiskCaches();
        Fresco.getImagePipeline().clearMemoryCaches();
    }
}