package com.bql.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: ThreadPool <br>
 * Description: 线程池<br>
 * Author: Cyarie <br>
 * Created: 2016/8/10 17:23 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class ThreadPool {

    private static RejectedExecutionHandler mRejectedHandler = new RejectedExecutionHandler() {
        public void rejectedExecution(final Runnable r, final ThreadPoolExecutor executor) {
            ThreadPool.runOnWorker(new Runnable() {
                @Override
                public void run() {
                    try {
                        executor.getQueue().put(r);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private static HandlerThread sHandlerThread = null;
    private static ExecutorService sIOPool = null;
    private static ExecutorService sPool = null;
    private static Handler sUiHandler = null;
    private static Handler sWorkerHandler = null;
    private static ScheduledThreadPoolExecutor scheduledPool = new ScheduledThreadPoolExecutor(1);


    public static Future<?> runOnPool(Runnable r) {
        if (sPool != null) {
            return sPool.submit(r);
        }
        return null;
    }

    public static void runOnUi(Runnable r) {
        sUiHandler.post(r);
    }

    public static void runOnWorker(Runnable r) {
        sWorkerHandler.post(r);
    }

    public static void postOnWorkerDelayed(Runnable r, int delay) {
        sWorkerHandler.postDelayed(r, (long) delay);
    }

    public static void postOnUiDelayed(Runnable r, int delay) {
        sUiHandler.postDelayed(r, (long) delay);
    }

    public static Looper getWorkerLooper() {
        return sHandlerThread.getLooper();
    }

    public static Handler getWorkerHandler() {
        return sWorkerHandler;
    }

    public static ScheduledFuture<?> schedule(Runnable r, long initialDelay, long period, TimeUnit unit) {
        return scheduledPool.scheduleAtFixedRate(r, initialDelay, period, TimeUnit.SECONDS);
    }

    public static ExecutorService getThreadPoolExecutor() {
        return sPool;
    }

    public static ExecutorService getThreadPoolIOExecutor() {
        return sIOPool;
    }

    public static void startup() {
        ThreadUtils.ensureUiThread();
        ThreadFactory factory = new ThreadFactory() {
            int count = 0;

            public Thread newThread(Runnable r) {
                this.count++;
                Thread thr = new Thread(r, "generic-pool-" + this.count);
                thr.setDaemon(false);
                thr.setPriority(3);
                return thr;
            }
        };
        int cpu_cores = Runtime.getRuntime().availableProcessors();
        int maxThreads = cpu_cores * 32;
        sPool = new ThreadPoolExecutor(cpu_cores, maxThreads, 15, TimeUnit.SECONDS, new LinkedBlockingQueue(maxThreads), factory, mRejectedHandler);
        sIOPool = new ThreadPoolExecutor(5, 10, 15, TimeUnit.SECONDS, new LinkedBlockingQueue(maxThreads), factory, mRejectedHandler);
        sUiHandler = new Handler();
        sHandlerThread = new HandlerThread("internal");
        sHandlerThread.setPriority(4);
        sHandlerThread.start();
        sWorkerHandler = new Handler(sHandlerThread.getLooper());
    }

    public static void shutdown() {
        sPool.shutdown();
        sHandlerThread.quit();
    }

    public static Handler getUiHandler() {
        return sUiHandler;
    }

}
