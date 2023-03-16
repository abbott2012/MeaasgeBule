package com.bql.convenientlog;

import android.util.Log;

/**
 * Created by Cyarie on 2015/12/28.
 */
public class BaseLog {

    public static void printDefault(int type, String tag, String msg) {

        int index = 0;
        int maxLength = 4000;
        int countOfSub = msg.length() / maxLength;

        if (countOfSub > 0) {
            for (int i = 0; i < countOfSub; i++) {
                String sub = msg.substring(index, index + maxLength);
                printSub(type, tag, sub);
                index += maxLength;
            }
            printSub(type, tag, msg.substring(index, msg.length()));
        } else {
            printSub(type, tag, msg);
        }
    }

    private static void printSub(int type, String tag, String sub) {
        switch (type) {
            case CLog.V:
                Log.v(tag, sub);
                break;
            case CLog.D:
                Log.d(tag, sub);
                break;
            case CLog.I:
                Log.i(tag, sub);
                break;
            case CLog.W:
                Log.w(tag, sub);
                break;
            case CLog.E:
                Log.e(tag, sub);
                break;
            case CLog.A:
                Log.wtf(tag, sub);
                break;
        }
    }
}
