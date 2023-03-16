package com.guoji.mobile.cocobee.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.guoji.mobile.cocobee.btreader.BlueToothHelper;

import java.util.Stack;

/**
 * Created by _H_JY on 2017/3/11.
 */
public class AppManager {

    // Activity栈
    private static Stack<Activity> activityStack;
    // 单例模式
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0; i < activityStack.size(); i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }


    public void releaseBluetoothResource(Context context) {
        //退出APP前释放蓝牙连接资源
        BlueToothHelper blueToothHelper = BlueToothHelper.getInstance(context, new Handler());
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_CONNECT_STATUS, Context.MODE_PRIVATE);
        boolean status = sp.getBoolean(Constant.CONNECT_STATUS, false);

        if (blueToothHelper.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC && status && (blueToothHelper.getSocket() == null || !blueToothHelper.getSocket().isConnected())) { //如果当前标记已连接，但是socket连接实际已经断了
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(Constant.CONNECT_STATUS, false);
            editor.commit();
            status = false;
        }

        if (status) {//如果连接
            if (blueToothHelper.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) { //经典蓝牙
                blueToothHelper.close();
            } else if (blueToothHelper.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_LE) { //低功耗蓝牙
                blueToothHelper.closeBLE();
            }
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(Constant.CONNECT_STATUS, false);
            editor.commit();
        }
    }


    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {

        try {
            releaseBluetoothResource(context); //释放蓝牙资源
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ElectricVehicleApp.setFlag(false);
            System.exit(0);
        }

    }
}
