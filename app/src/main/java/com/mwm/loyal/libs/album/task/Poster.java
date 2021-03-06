package com.mwm.loyal.libs.album.task;

import android.os.Handler;
import android.os.Looper;

public class Poster extends Handler {

    private static Poster instance;

    /**
     * Get single object.
     *
     * @return {@link Poster}.
     */
    public static Poster getInstance() {
        if (instance == null)
            synchronized (Poster.class) {
                if (instance == null)
                    instance = new Poster();
            }
        return instance;
    }

    private Poster() {
        super(Looper.getMainLooper());
    }
}
