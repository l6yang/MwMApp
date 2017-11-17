package com.mwm.loyal.libs.manager;

import android.content.Intent;
import android.net.Uri;

import com.mwm.loyal.impl.Contact;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class UtilsApp implements Contact {

    public static boolean copyFile(AppBean appInfo) {
        File initialFile = new File(appInfo.getSource());
        File finalFile = getOutputFilename(appInfo);
        if (!initialFile.exists())
            return false;
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(initialFile).getChannel();
            outputChannel = new FileOutputStream(finalFile).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            IOUtil.closeStream(inputChannel);
            IOUtil.closeStream(outputChannel);
        }
    }

    /**
     * Retrieve the name of the extracted APK
     *
     * @param appInfo AppInfo
     * @return String with the output name
     */
    public static String getAPKFilename(AppBean appInfo) {
        return appInfo.getApk() + "_" + appInfo.getVersion();
    }

    /**
     * Retrieve the name of the extracted APK with the path
     *
     * @param appInfo AppInfo
     * @return File with the path and output name
     */
    public static File getOutputFilename(AppBean appInfo) {
        return new File(FileUtil.path_share, getAPKFilename(appInfo) + ".apk");
    }

    public static Intent getShareIntent(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.setType("application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }
}
