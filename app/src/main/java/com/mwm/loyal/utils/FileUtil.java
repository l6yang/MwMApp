package com.mwm.loyal.utils;

import android.os.Environment;

import com.loyal.kit.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    // sd卡路径
    private static final String SD_PATH = Environment.getExternalStorageDirectory().getPath();
    private static final String path_main = SD_PATH + File.separator + "com.mwm.loyal" + File.separator;
    // 本地保存文件
    public static final String path_local = path_main + "local" + File.separator;
    public static final String path_icon = path_main + "icon" + File.separator;
    public static final String path_temp = path_main + "temp" + File.separator;
    public static final String pic_UCrop = "UCrop.jpg";
    public static final String pic_temp = "temp.jpg";
    public static final String pic_tmp = "tmp.jpg";
    // apk更新下载的更新文件存放的目录
    public static final String path_apk = path_main + "apk" + File.separator;
    public static final String path_share = path_main + "share" + File.separator;
    public static final String apkFileName = "mwm.apk";

    public static void createFiles() {
        File file = new File(path_local);
        createFiles(file);
        file = new File(path_icon);
        createFiles(file);
        file = new File(path_apk);
        createFiles(file);
        file = new File(path_temp);
        createFiles(file);
        file = new File(path_share);
        createFiles(file);
    }

    private static boolean createFiles(File file) {
        return !file.exists() && file.mkdirs();
    }

    // 保存IP文件到本地 这里只需要把文件名传进去就行，不管保存的类型
    public static void createFiles(String path, String fileName, String content) {
        File file = new File(path + fileName);
        boolean delete = deleteFile(file);
        if (delete)
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(content.getBytes());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public static void writeFile(InputStream in, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024 * 128];
            int len;
            while ((len = in.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            fos.close();
            in.close();
        } catch (IOException e) {
            //
        } finally {
            IOUtil.closeStream(fos);
            IOUtil.closeStream(in);
        }
    }

    // 将保存在文件的数据读载到所需要的控件中
    public static String getFileContent(String path, String fileName) {
        File f = new File(path + "/" + fileName);
        try {
            FileInputStream fis;
            fis = new FileInputStream(f);
            byte[] b = new byte[1024];
            int len;
            StringBuilder sb = new StringBuilder();
            while ((len = fis.read(b)) != -1) {
                sb.append(new String(b, 0, len, "utf-8"));
            }
            fis.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * @param file Eg:"F:\ss\新建文本文档.txt"
     * @ 删除文件
     */
    public static boolean deleteFile(File file) {
        try {
            return !file.exists() || file.delete();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean deleteFile(String path) {
        return deleteFile(new File(path));
    }

    public static boolean deleteFile(String path, String fileName) {
        return deleteFile(new File(path, fileName));
    }
}
