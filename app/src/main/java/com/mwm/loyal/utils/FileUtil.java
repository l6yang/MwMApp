package com.mwm.loyal.utils;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
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
    public static final String path_voice = path_main + "voice" + File.separator;
    public static final String pic_UCrop = "UCrop.jpg";
    public static final String pic_temp = "temp.jpg";
    public static final String pic_tmp = "tmp.jpg";
    // apk更新下载的更新文件存放的目录
    public static final String path_apk = path_main + "apk" + File.separator;
    public static final String apkFileName = "mwm.apk";

    public static boolean fileCreated(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static void createFiles() {
        File file = new File(path_local);
        createFiles(file);
        file = new File(path_icon);
        createFiles(file);
        file = new File(path_apk);
        createFiles(file);
        file = new File(path_temp);
        createFiles(file);
        file = new File(path_voice);
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

    public static String createIcons(String path, String fileName, InputStream inputStream) {
        if (inputStream == null)
            return "";
        fileName = fileName + ".png";
        File file = new File(path + fileName);
        boolean delete = deleteFile(file);
        if (delete)
            try {
                FileOutputStream fos = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int len;
                while ((len = inputStream.read(bytes)) != -1) {
                    fos.write(bytes, 0, len);
                }
                inputStream.close();
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return file.getPath();
    }

    // 将保存在文件的数据读载到所需要的控件中
    public static String getFileContent(String path, String fileName) {
        File f = new File(path + "/" + fileName);
        try {
            FileInputStream fis;
            fis = new FileInputStream(f);
            byte b[] = new byte[1024];
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

    // @ 读取文件中的内容
    public static String readFileContent(String path, String fileName) {
        File file = new File(new File(path), fileName);
        try {
            BufferedReader br;
            StringBuilder sb = new StringBuilder();
            br = new BufferedReader(new FileReader(file));
            String readline;
            while ((readline = br.readLine()) != null) {
                sb.append(readline);
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
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

    public static boolean renameFile(File resFile, File goalFile) {
        try {
            return resFile.renameTo(goalFile);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean renameFile(String resFile, String goalFile) {
        try {
            File res = new File(resFile);
            File goal = new File(goalFile);
            return renameFile(res, goal);
        } catch (Exception e) {
            return false;
        }
    }
}
