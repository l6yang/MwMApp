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
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.mob.tools.MobLog;
import com.mob.tools.utils.ReflectHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ApkUtil {

    // 安装一个apk文件
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

    //卸载一个app
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

    //检查手机上是否安装了指定的软件
    private static boolean isAvailable(Context context, String packageName) {

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

    //检查手机上是否安装了指定的软件
    public static boolean isAvailable(Context context, File file) {
        try {
            return !TextUtils.isEmpty(getPackageName(context, file.getAbsolutePath())) && isAvailable(context, getPackageName(context, file.getAbsolutePath()));
        } catch (Exception e) {
            return false;
        }
    }

    //根据文件路径获取包名
    private static String getPackageName(Context context, String filePath) {
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
    public static int getScreenWidth(Activity context) {
        try {
            Point size = new Point();
            context.getWindowManager().getDefaultDisplay().getSize(size);
            return size.x;
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getScreenHeight(Activity context) {
        try {
            Point size = new Point();
            context.getWindowManager().getDefaultDisplay().getSize(size);
            return size.y;
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getMacAddressFromIp() {
        String hostName = getIpAddress();
        if (TextUtils.isEmpty(hostName))
            return "";
        try {
            NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(hostName));
            byte[] mac = ne.getHardwareAddress();
            return byte2hex(mac).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String byte2hex(byte[] byteArray) {
        StringBuffer buffer = new StringBuffer(byteArray.length);
        String temp;
        for (byte aByte : byteArray) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1)
                buffer = buffer.append("0").append(temp).append(":");
            else {
                buffer = buffer.append(temp).append(":");
            }
        }
        String macAddress = buffer.toString();
        if (macAddress.endsWith(":")) {
            macAddress = macAddress.substring(0, macAddress.length() - ":".length());
        }
        return macAddress;
    }

    public static int getSimState(Context context) {
        try {
            // 取得相关系统服务
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSimState();
        } catch (Exception e) {
            return TelephonyManager.SIM_STATE_UNKNOWN;
        }
    }

    public static String getDeviceID() {
        return Build.MANUFACTURER + "(" + Build.MODEL + ")";
    }

    /**
     * 获取手机状态权限之后即可读取到序列号信息
     */
    @SuppressLint("HardwareIds")
    public static String getDeviceSerial() {
        return Build.SERIAL;
    }

    //不过我自己在做项目过程中，用另外一种方法也解决了android4.0获取IP错误的问题:
    //获取本地IP
    private static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface inter = en.nextElement();
                for (Enumeration<InetAddress> addresses = inter
                        .getInetAddresses(); addresses.hasMoreElements(); ) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    public String getMacAddress(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            String t;
            try {
                t = this.getHardwareAddressFromShell("wlan0");
            } catch (Throwable var9) {
                MobLog.getInstance().d(var9);
                t = null;
            }
            if (t == null) {
                try {
                    t = this.getCurrentNetworkHardwareAddress();
                } catch (Throwable var8) {
                    MobLog.getInstance().d(var8);
                    t = null;
                }
            }

            if (t == null) {
                try {
                    String[] sb = this.listNetworkHardwareAddress();
                    if (sb.length > 0) {
                        t = sb[0];
                    }
                } catch (Throwable var7) {
                    MobLog.getInstance().d(var7);
                    t = null;
                }
            }
            if (t != null) {
                return t;
            }
        }
        try {
            Object t1 = this.getSystemService(context, "wifi");
            if (t1 == null) {
                return null;
            }

            String sb1 = "ge" +
                    "tC" +
                    "on" +
                    "ne" +
                    "ct" +
                    "io" +
                    "nI" +
                    "nf" +
                    "o";
            Object info = ReflectHelper.invokeInstanceMethod(t1, sb1);
            if (info != null) {
                String sb2 = "ge" +
                        "tM" +
                        "ac" +
                        "Ad" +
                        "dr" +
                        "es" +
                        "s";
                String mac = ReflectHelper.invokeInstanceMethod(info, sb2);
                return mac == null ? null : mac;
            }
        } catch (Throwable var6) {
            MobLog.getInstance().w(var6);
        }
        return null;
    }

    private String getHardwareAddressFromShell(String networkCard) {
        String line = null;
        BufferedReader br = null;
        try {
            String t = "ca" +
                    "t " +
                    "/s" +
                    "ys" +
                    "/c" +
                    "la" +
                    "ss" +
                    "/n" +
                    "et" +
                    "/" +
                    networkCard +
                    "/a" +
                    "dd" +
                    "re" +
                    "ss";
            Process p = Runtime.getRuntime().exec(t);
            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            br = new BufferedReader(isr);
            line = br.readLine();
        } catch (Throwable var15) {
            MobLog.getInstance().d(var15);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Throwable var14) {
                    //
                }
            }

        }
        return TextUtils.isEmpty(line) ? null : line;
    }

    private String getCurrentNetworkHardwareAddress() throws Throwable {
        Enumeration nis = NetworkInterface.getNetworkInterfaces();
        if (nis == null) {
            return null;
        } else {
            ArrayList interfaces = Collections.list(nis);
            Iterator i$ = interfaces.iterator();

            while (true) {
                NetworkInterface intf;
                Enumeration ias;
                do {
                    if (!i$.hasNext()) {
                        return null;
                    }

                    intf = (NetworkInterface) i$.next();
                    ias = intf.getInetAddresses();
                } while (ias == null);

                ArrayList addrs = Collections.list(ias);
                Iterator i$1 = addrs.iterator();

                while (i$1.hasNext()) {
                    InetAddress add = (InetAddress) i$1.next();
                    if (!add.isLoopbackAddress() && add instanceof Inet4Address) {
                        byte[] mac = intf.getHardwareAddress();
                        if (mac != null) {
                            StringBuilder buf = new StringBuilder();
                            int len$ = mac.length;
                            for (int i$2 = 0; i$2 < len$; ++i$2) {
                                byte aMac = mac[i$2];
                                buf.append(String.format("%02x:", new Object[]{Byte.valueOf(aMac)}));
                            }

                            if (buf.length() > 0) {
                                buf.deleteCharAt(buf.length() - 1);
                            }

                            return buf.toString();
                        }
                    }
                }
            }
        }
    }

    private String[] listNetworkHardwareAddress() throws Throwable {
        Enumeration nis = NetworkInterface.getNetworkInterfaces();
        if (nis == null) {
            return null;
        } else {
            ArrayList interfaces = Collections.list(nis);
            HashMap<String,String> macs = new HashMap<>();
            Iterator names = interfaces.iterator();
            while (true) {
                NetworkInterface wlans;
                byte[] eths;
                do {
                    if (!names.hasNext()) {
                        ArrayList<String> var14 = new ArrayList<>(macs.keySet());
                        ArrayList<String> var15 = new ArrayList<>();
                        ArrayList<String> var16 = new ArrayList<>();
                        ArrayList<String> var17 = new ArrayList<>();
                        ArrayList<String> var18 = new ArrayList<>();
                        ArrayList<String> var19 = new ArrayList<>();
                        ArrayList<String> var20 = new ArrayList<>();
                        ArrayList<String> var21 = new ArrayList<>();

                        while (var14.size() > 0) {
                            String macArr = var14.remove(0);
                            if (macArr.startsWith("wlan")) {
                                var15.add(macArr);
                            } else if (macArr.startsWith("eth")) {
                                var16.add(macArr);
                            } else if (macArr.startsWith("rev_rmnet")) {
                                var17.add(macArr);
                            } else if (macArr.startsWith("dummy")) {
                                var18.add(macArr);
                            } else if (macArr.startsWith("usbnet")) {
                                var19.add(macArr);
                            } else if (macArr.startsWith("rmnet_usb")) {
                                var20.add(macArr);
                            } else {
                                var21.add(macArr);
                            }
                        }

                        Collections.sort(var15);
                        Collections.sort(var16);
                        Collections.sort(var17);
                        Collections.sort(var18);
                        Collections.sort(var19);
                        Collections.sort(var20);
                        Collections.sort(var21);
                        var14.addAll(var15);
                        var14.addAll(var16);
                        var14.addAll(var17);
                        var14.addAll(var18);
                        var14.addAll(var19);
                        var14.addAll(var20);
                        var14.addAll(var21);
                        String[] var22 = new String[var14.size()];

                        for (int i = 0; i < var22.length; ++i) {
                            var22[i] = macs.get(var14.get(i));
                        }

                        return var22;
                    }

                    wlans = (NetworkInterface) names.next();
                    eths = wlans.getHardwareAddress();
                } while (eths == null);

                StringBuilder rmnets = new StringBuilder();
                byte[] dummys = eths;
                int usbs = eths.length;

                for (int rmnetUsbs = 0; rmnetUsbs < usbs; ++rmnetUsbs) {
                    byte others = dummys[rmnetUsbs];
                    rmnets.append(String.format("%02x:", new Object[]{Byte.valueOf(others)}));
                }

                if (rmnets.length() > 0) {
                    rmnets.deleteCharAt(rmnets.length() - 1);
                }

                macs.put(wlans.getName(), rmnets.toString());
            }
        }
    }

    private Object getSystemService(Context context, String name) {
        try {
            return context.getSystemService(name);
        } catch (Throwable var3) {
            MobLog.getInstance().w(var3);
            return null;
        }
    }
}
