package com.bql.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Toast工具类
 * Created by Cyarie on 2016/1/18.
 */
public class ToastUtils {

    private static Toast toast;// Toast

    /**
     * 显示Toast
     *
     * @param context    上下文
     * @param imageResId 图片资源
     * @param message    显示信息
     * @param duration   显示时长
     */
    public static void show(Context context, int imageResId, String message, int duration) {
        if (toast == null) {
            View view = LayoutInflater.from(context).inflate(R.layout.toast_view, null);
            toast = new Toast(context);
            toast.setView(view);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(duration);
        }
        if (imageResId == -1) {
            toast.getView().findViewById(R.id.iv_picture).setVisibility(View.GONE);
        } else {
            toast.getView().findViewById(R.id.iv_picture).setVisibility(View.VISIBLE);
            ((ImageView) toast.getView().findViewById(R.id.iv_picture)).setImageResource(imageResId);
        }
        ((TextView) toast.getView().findViewById(R.id.tv_message)).setText(message);
        toast.show();
    }


    /**
     * 短时间显示 Toast
     *
     * @param context 上下文
     * @param resId   显示文字资源
     */
    public static void showShort(Context context, int resId) {
        show(context, -1, context.getString(resId), Toast.LENGTH_SHORT);
    }

    /**
     * 短时间显示 Toast
     *
     * @param context 上下文
     * @param message 显示文字
     */
    public static void showShort(Context context, String message) {
        show(context, -1, message, Toast.LENGTH_SHORT);
    }

    /**
     * 短时间显示 Toast 带有图片
     *
     * @param context
     * @param imageResId
     * @param resId
     */
    public static void showShortWithImage(Context context, int imageResId, int resId) {
        show(context, imageResId, context.getString(resId), Toast.LENGTH_SHORT);

    }


    /**
     * 短时间显示 Toast 带有图片
     *
     * @param context
     * @param imageResId
     * @param message
     */
    public static void showShortWithImage(Context context, int imageResId, String message) {
        show(context, imageResId, message, Toast.LENGTH_SHORT);

    }

    /**
     * 长时间显示 Toast
     *
     * @param context 上下文
     * @param resId   显示文字资源
     */
    public static void showLong(Context context, int resId) {
        show(context, -1, context.getString(resId), Toast.LENGTH_LONG);
    }


    /**
     * 长时间显示 Toast
     *
     * @param context 上下文
     * @param message 显示文字
     */
    public static void showLong(Context context, String message) {
        show(context, -1, message, Toast.LENGTH_LONG);
    }


    /**
     * @throws
     * @Title: cancel
     * @Description: 取消Toast
     * @param:
     * @return: void
     */
    public static void cancel() {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
    }
}
