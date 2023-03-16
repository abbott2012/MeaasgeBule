package com.bql.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.bql.convenientlog.CLog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * crash收集
 * Created by Cyarie on 2016/1/29.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler mInstance;// CrashHandler实例

    private String directoryName;//文件夹名称

    private Context mContext;

    private CrashHandler(Context context, String directoryName) {
        this.directoryName = directoryName;
        this.mContext = context;
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     *
     * @return
     */
    public static CrashHandler getInstance(Context context, String directoryName) {
        synchronized (CrashHandler.class) {
            if (mInstance == null) {
                mInstance = new CrashHandler(context, directoryName);
            }
        }
        return mInstance;
    }


    /**
     * 当UncaughtException发生时会转入该重写的方法来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        if (ex == null || !SDCardUtils.isSDCardAvailable()) {
            // 退出程序
            EventBus.getDefault().post(new EventManager());
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        // 循环着把所有的异常信息写入writer中
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();// 记得关闭
        final String crashMsg = writer.toString();
        // 保存日志文件
        saveCrashInfo2File(getDeviceInfo() + crashMsg);
        // 退出程序
        EventBus.getDefault().post(new EventManager());
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    /**
     * 获取手机信息
     *
     * @return
     */
    private String getDeviceInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("OS:Android " + Build.VERSION.RELEASE + "\r\n");//Android操作系统版本
        sb.append("model:" + Build.MODEL + "\r\n");//手机型号
        sb.append("brand:" + Build.BRAND + "\r\n");//手机品牌
        sb.append("SDK:" + Build.VERSION.SDK_INT + "\r\n");//SDK版本

        String pkgName = null;
        String versionName = null;
        int versionCOde = 0;
        try {
            pkgName = mContext.getPackageName();
            versionName = mContext.getPackageManager().getPackageInfo(
                    pkgName, 0).versionName;
            versionCOde = mContext.getPackageManager()
                    .getPackageInfo(pkgName, 0).versionCode;
        } catch (Exception e) {
        }
        if (!CheckUtils.isEmpty(pkgName)) {
            sb.append("packageName:" + pkgName + "\r\n");//应用包名
        }
        if (!CheckUtils.isEmpty(versionName)) {
            sb.append("versionName:" + versionName + "\r\n");//版本名称
        }
        if (versionCOde != 0) {
            sb.append("versionCode:" + versionCOde + "\r\n");//版本号
        }
        return sb.toString();
    }


    /**
     * 保存crash信息
     *
     * @param crashMsg
     * @return
     */
    private void saveCrashInfo2File(String crashMsg) {
        CLog.e(crashMsg);
        // 保存文件
        String folderName = DateUtils.format(System.currentTimeMillis(), "yyyy-MM-dd");
        String time = DateUtils.format(System.currentTimeMillis(), "HH:mm:ss");
        String fileName = time + ".log";
        String filePath = CheckUtils.isEmpty(directoryName) ? "/crash/" + folderName : directoryName + "/" + folderName;
        try {
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(new File(dir, fileName));
            fos.write(crashMsg.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
