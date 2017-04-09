package com.mwm.loyal.utils;

import java.io.IOException;
import java.net.InetAddress;

public class PingUtil {
    public static boolean isIpReachable(String ip) {
        try {
            InetAddress addr = InetAddress.getByName(ip);
            return (addr.isReachable(500));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
