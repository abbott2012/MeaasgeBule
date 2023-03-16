package com.guoji.mobile.cocobee.utils;

import com.bql.utils.ToastUtils;
import com.guoji.mobile.cocobee.common.ElectricVehicleApp;

/**
 * ClassName: XToastUtils <br>
 * Description: <br>
 * Author: Cyarie <br>
 * Created: 2016/7/15 16:57 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class XToastUtils {

    /**
     * 显示短时间通知  使用全局的context防止activity关闭时导致内存泄漏
     *
     * @param str
     */
    public static void showShortToast(String str) {
        ToastUtils.showShort(ElectricVehicleApp.getApp(), str);
    }

    /**
     * 显示短时间通知
     *
     * @param resId 资源ID
     */
    public static void showShortToast(int resId) {
        showShortToast(ElectricVehicleApp.getApp().getString(resId));
    }

    /**
     * 显示长时间通知  使用全局的context防止activity关闭时导致内存泄漏
     *
     * @param str
     */
    public static void showLongToast(String str) {
        ToastUtils.showLong(ElectricVehicleApp.getApp(), str);
    }

    /**
     * 显示长时间通知
     *
     * @param resId 资源ID
     */
    public static void showLongToast(int resId) {
        showLongToast(ElectricVehicleApp.getApp().getString(resId));
    }
}
