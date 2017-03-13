package com.mwm.loyal.libs.network.imp;

/**
 * 下载进度listener
 */
public interface DownLoadListener {
    void update(long bytesRead, long contentLength, boolean done);
}
