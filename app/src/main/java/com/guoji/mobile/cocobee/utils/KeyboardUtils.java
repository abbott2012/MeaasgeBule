package com.guoji.mobile.cocobee.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


/**
 * ClassName: KeyboardUtils <br>
 * Description: 键盘工具类<br>
 * Author: Cyarie <br>
 * Created: 2016/8/10 17:43 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class KeyboardUtils {

    private static final String PREF_KEY_KEYBOARD_HEIGHT = "pref_key_keyboard_height";
    private static final String TAG = KeyboardUtils.class.getSimpleName();
    private static int sDefaultPickerHeight = 0;

    public static int getDefaultPickerHeight() {
        return sDefaultPickerHeight;
    }

    /**
     * 显示键盘
     *
     * @param context
     * @param editText
     */
    public static void showKeyboard(final Context context, final EditText editText) {
        editText.requestFocus();
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                showKeyboard(context);
            }
        }, 200);
    }


    /**
     * 显示键盘
     *
     * @param context
     */
    public static void showKeyboard(Context context) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(2, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏键盘
     *
     * @param context
     * @param editText
     */
    public static void hideKeyboard(Context context, EditText editText) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    /**
     * 隐藏键盘
     *
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * 键盘是否显示
     *
     * @param context
     * @param editText
     * @return
     */
    public static boolean isKeyboardShown(Context context, EditText editText) {
        return isKeyboardShown(context, editText);
    }

    /**
     * 键盘是否显示
     *
     * @param context
     * @param view
     * @return
     */
    public static boolean isKeyboardShown(Context context, View view) {
        return ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).isActive(view);
    }


    /**
     * 获取键盘的高度
     *
     * @param activity
     * @return
     */
//    public static int getKeyboardHeight(Activity activity) {
//        if (sDefaultPickerHeight == 0) {
//            sDefaultPickerHeight = (int) activity.getResources().getDimension(R.dimen.size270);
//        }
//        if (activity == null) {
//            return sDefaultPickerHeight;
//        }
//        int savedHeight = LeYaoGoApplication.getDataKeeper().getInt(PREF_KEY_KEYBOARD_HEIGHT, 0);
//        Rect r = new Rect();
//        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
//        int keyboardHeight = ((getScreenHeight(activity) - r.top) - r.height()) - getBottomBarHeight(activity);
//        if (keyboardHeight != 0) {
//            keyboardHeight = Math.max(keyboardHeight, sDefaultPickerHeight);
//            LeYaoGoApplication.getDataKeeper().putInt(PREF_KEY_KEYBOARD_HEIGHT, keyboardHeight);
//            return keyboardHeight;
//        } else if (keyboardHeight != 0 || savedHeight == 0) {
//            return sDefaultPickerHeight;
//        } else {
//            return Math.max(savedHeight, sDefaultPickerHeight);
//        }
//    }

    /**
     * 获取键盘上的高度
     *
     * @param activity
     * @return
     */
//    public static int getAboveLayoutHeight(Activity activity) {
//        CLog.d(TAG, "getScreenHeight = " + getScreenHeight(activity) + " getStatusBarHeight = " + DeviceUtils.getStatusBarHeight(activity) + " getKeyboardHeight = " + getKeyboardHeight(activity));
//        return ((getScreenHeight(activity) - DeviceUtils.getStatusBarHeight(activity)) - getKeyboardHeight(activity)) - getBottomBarHeight(activity);
//    }

    /**
     * 获取屏幕高度
     *
     * @param activity
     * @return
     */
    public static int getScreenHeight(Activity activity) {
        return activity.getWindow().getWindowManager().getDefaultDisplay().getHeight();
    }

    /**
     * 获取底部Bar高度
     *
     * @param activity
     * @return
     */
    public static int getBottomBarHeight(Activity activity) {
        return getScreenHeight(activity) - activity.getWindow().getDecorView().getHeight();
    }


}
