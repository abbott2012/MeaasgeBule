package com.guoji.mobile.cocobee.common;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by _H_JY on 2016/1/6.
 * <p/>
 * 软件崩溃处理器
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    /**
     * 是否开启日志输出,在Debug状态下开启,
     * 在Release状态下关闭以提高程序性能
     */
    public static final boolean DEBUG = true;

    /**
     * 系统默认的UncaughtException处理类
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /**
     * CrashHandler实例
     */
    private static CrashHandler instance;

    private Context mContext;

    private Handler handler = new Handler();

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {

    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (instance == null) {
            synchronized (CrashHandler.class) {
                if (instance == null) {
                    instance = new CrashHandler();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化,注册Context对象,
     * 获取系统默认的UncaughtException处理器,
     * 设置该CrashHandler为程序的默认处理器
     *
     * @param ctx
     */
    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {  //如果自己处理了异常，则不会弹出错误对话框，则需要手动退出app
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            AppManager.getAppManager().AppExit(mContext);
            //android.os.Process.killProcess(android.os.Process.myPid());
            //System.exit(1); //非0表示程序是非正常退出的
        }
    }


    /**
     * 自定义错误处理,收集错误信息
     * 发送错误报告等操作均在此完成.
     * 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @return true代表处理该异常，不再向上抛异常，
     * false代表不处理该异常(可以将该log信息存储起来)然后交给上层(这里就到了系统的异常处理)去处理，
     * 简单来说就是true不会弹出那个错误提示框，false就会弹出
     */
    public boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }

        //final String msg = ex.getLocalizedMessage();
        final StackTraceElement[] stack = ex.getStackTrace();
        //final String message = ex.getMessage();

       new Thread(new Runnable() {
           @Override
           public void run() {
               Looper.prepare();
               Toast.makeText(mContext, "很抱歉，应用程序运行异常，即将关闭", Toast.LENGTH_LONG).show();
               Looper.loop();
           }
       }).start();



        return true;
    }

}
