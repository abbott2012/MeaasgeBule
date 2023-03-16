package com.bql.utils;

import android.os.Looper;

/**
 * ClassName: ThreadUtils <br>
 * Description: 线程工具类<br>
 * Author: Cyarie <br>
 * Created: 2016/7/27 14:16 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class ThreadUtils {

    public static void ensureUiThread() {
        if (!isUiThread()) {
            throw new IllegalStateException("ensureUiThread: thread check failed");
        }
    }

    public static void ensureNonUiThread() {
        if (isUiThread()) {
            throw new IllegalStateException("ensureNonUiThread: thread check failed");
        }
    }

    public static boolean isUiThread() {
        return Looper.getMainLooper().equals(Looper.myLooper());
    }

}
