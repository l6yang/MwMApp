package com.mwm.loyal.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ApkUtil {

    /**
     * 安装一个apk文件
     */
    public static void install(Context context, File uriFile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, "com.mwm.loyal.fileprovider", uriFile);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(uriFile), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("安装程序失败");
        }
    }

    /**
     * 卸载一个app
     */
    public static void uninstall(Context context, String packageName) {
        try {
            //通过程序的包名创建URI
            Uri packageURI = Uri.parse("package:" + packageName);
            //创建Intent意图
            Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
            //执行卸载程序
            context.startActivity(intent);
        } catch (Exception e) {
            ToastUtil.showToast(context, "卸载程序失败");
        }
    }

    /**
     * 检查手机上是否安装了指定的软件
     */
    public static boolean isAvailable(Context context, String packageName) {

        try {// 获取packageManager
            final PackageManager packageManager = context.getPackageManager();
            // 获取所有已安装程序的包信息
            List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
            // 用于存储所有已安装程序的包名
            List<String> packageNames = new ArrayList<>();
            // 从pinfo中将包名字逐一取出，压入pName list中
            if (packageInfos != null) {
                for (int i = 0; i < packageInfos.size(); i++) {
                    String packName = packageInfos.get(i).packageName;
                    packageNames.add(packName);
                }
            }
            // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
            return packageNames.contains(packageName);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查手机上是否安装了指定的软件
     */
    public static boolean isAvailable(Context context, File file) {
        try {
            return !TextUtils.isEmpty(getPackageName(context, file.getAbsolutePath())) && isAvailable(context, getPackageName(context, file.getAbsolutePath()));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据文件路径获取包名
     */
    public static String getPackageName(Context context, String filePath) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                return appInfo.packageName;  //得到安装包名称
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 从apk中获取版本信息
     */
    public static String getChannelFromApk(Context context, String channelPrefix) {
        try {
            //从apk包中获取
            ApplicationInfo appInfo = context.getApplicationInfo();
            String sourceDir = appInfo.sourceDir;
            //默认放在meta-inf/里， 所以需要再拼接一下
            String key = "META-INF/" + channelPrefix;
            String ret = "";
            ZipFile zipfile = null;
            try {
                zipfile = new ZipFile(sourceDir);
                Enumeration<?> entries = zipfile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = ((ZipEntry) entries.nextElement());
                    String entryName = entry.getName();
                    if (entryName.startsWith(key)) {
                        ret = entryName;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (zipfile != null) {
                    try {
                        zipfile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            String[] split = ret.split(channelPrefix);
            String channel = "";
            if (split.length >= 2) {
                channel = ret.substring(key.length());
            }
            return channel;
        } catch (Exception e) {
            return "";
        }
    }

    // 获得当前具体版本名
    public static String getApkVersion(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi;
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            // getPackageName()是你当前类的包名,0代表是获取版本信息
            return pi.versionName;
        } catch (Exception e) {
            return "1.0.0";
        }
    }

    // 获得屏幕界面的尺寸
    public static int getSize_X(Activity context) {
        try {
            Point size = new Point();
            context.getWindowManager().getDefaultDisplay().getSize(size);
            return size.x;
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getSize_Y(Activity context) {
        try {
            Point size = new Point();
            context.getWindowManager().getDefaultDisplay().getSize(size);
            return size.y;
        } catch (Exception e) {
            return 0;
        }
    }

    private static String getMacAddress() {
        String macSerial = "";
        try {
            String str = "";
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (TextUtils.isEmpty(macSerial)) {
            String mac = loadFileAsString("/sys/class/net/eth0/address").toUpperCase();
            if (TextUtils.isEmpty(mac))
                return "02:00:00:00:00:00";
            else
                return mac.substring(0, 17);
        } else
            return macSerial;
    }

    @SuppressLint("HardwareIds")
    private static String getMacAddress(Context context) {
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            return info.getMacAddress();
        } catch (Exception e) {
            return "02:00:00:00:00:00";
        }
    }

    private static String loadFileAsString(String fileName) {
        try {
            FileReader reader = new FileReader(fileName);
            String text = loadReaderAsString(reader);
            reader.close();
            return text;
        } catch (Exception e) {
            return "";
        }
    }

    private static String loadReaderAsString(Reader reader) {
        try {
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[4096];
            int readLength = reader.read(buffer);
            while (readLength >= 0) {
                builder.append(buffer, 0, readLength);
                readLength = reader.read(buffer);
            }
            return builder.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static int getSimState(Context con) {
        try {
            // 取得相关系统服务
            TelephonyManager tm = (TelephonyManager) con
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSimState();
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getDeviceID() {
        return Build.MANUFACTURER + "(" + Build.MODEL + ")";
    }

    public static String getMAC(Context context) {
        String mac = getMacAddress(context);
        System.out.println("getMacAddress(context)::"+mac);
        if (TextUtils.isEmpty(mac) || TextUtils.equals(mac, "02:00:00:00:00:00")) {
            mac = getMacAddress();
            System.out.println("getMacAddress::" + mac);
        }
        return mac;
    }

    //不过我自己在做项目过程中，用另外一种方法也解决了android4.0获取IP错误的问题:
    //获取本地IP
    public static String getLocalIpAddress123() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface inter = en.nextElement();
                for (Enumeration<InetAddress> addresses = inter
                        .getInetAddresses(); addresses.hasMoreElements(); ) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference", ex.toString());
        }
        return "192.168.xxx.xxx";
    }
}
