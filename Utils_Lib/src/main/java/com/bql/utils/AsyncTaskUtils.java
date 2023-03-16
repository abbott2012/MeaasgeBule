package com.bql.utils;

import android.os.AsyncTask;
import android.os.Build;

import com.bql.convenientlog.CLog;

import java.util.concurrent.RejectedExecutionException;

/**
 * ClassName: AsyncTaskUtils <br>
 * Description: AsyncTask<br>
 * Author: Cyarie <br>
 * Created: 2016/8/10 17:22 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public abstract class AsyncTaskUtils {

    public static <Params, Progress, Result> void exeIOTask(AsyncTask<Params, Progress, Result> asyncTask, Params... params) {
        try {
            if (Build.VERSION.SDK_INT >= 11) {
                asyncTask.executeOnExecutor(ThreadPool.getThreadPoolIOExecutor(), params);
            } else {
                asyncTask.execute(params);
            }
        } catch (RejectedExecutionException e) {
            CLog.v("async task pool full");
        }
    }


    public static <Params, Progress, Result> void exe(AsyncTask<Params, Progress, Result> asyncTask, Params... params) {
        try {
            if (Build.VERSION.SDK_INT >= 11) {
                asyncTask.executeOnExecutor(ThreadPool.getThreadPoolExecutor(), params);
            } else {
                asyncTask.execute(params);
            }
        } catch (RejectedExecutionException e) {
            CLog.v("async task pool full");
        }
    }

}
