package com.guoji.mobile.cocobee.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bql.utils.CheckUtils;
import com.bql.utils.DeviceUtils;
import com.bql.utils.NetworkUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.LoginActivity;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.ElectricVehicleApp;
import com.guoji.mobile.cocobee.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import Decoder.BASE64Encoder;

/**
 * Created by Administrator on 2017/4/1.
 */
public class Utils {

    //13：患者 14：老人 15：学生 16：易走失人员
    public static String getUserType(String userType) {
        if (TextUtils.equals("13", userType)) {
            return "患者";
        } else if (TextUtils.equals("14", userType)) {
            return "老人";
        } else if (TextUtils.equals("15", userType)) {
            return "学生";
        } else if (TextUtils.equals("16", userType)) {
            return "易走失人员";
        }
        return "";
    }

    //获取报警状态(报警状态：0：待确认，1：已确认，2：待审核，3：审核通过，4：退回)
    public static String getAlarmStatus(String status) {
        if (TextUtils.equals("0", status)) {
            return "待确认";
        } else if (TextUtils.equals("1", status)) {
            return "已确认";
        } else if (TextUtils.equals("2", status)) {
            return "待审核";
        } else if (TextUtils.equals("3", status)) {
            return "审核通过";
        } else if (TextUtils.equals("4", status)) {
            return "退回";
        }
        return "";
    }

    /**
     * 保存用户登录信息
     *
     * @param user
     */
    public static void putUserLoginInfo(User user) {
        ElectricVehicleApp.getDataKeeper().put(AppConstants.USER_LOGIN_INFO, user);
    }

    /**
     * 获得用户登录信息
     *
     * @return
     */
    public static User getUserLoginInfo() {
        return (User) ElectricVehicleApp.getDataKeeper().get(AppConstants.USER_LOGIN_INFO);
    }

    /**
     * 保存用户登录账号
     */
    public static void putUserLoginNum(String num) {
        ElectricVehicleApp.getDataKeeper().put(AppConstants.USER_LOGIN_NUM, num);
    }

    /**
     * 获得用户登录账号
     */
    public static String getUserLoginNum() {
        return ElectricVehicleApp.getDataKeeper().get(AppConstants.USER_LOGIN_NUM, "");
    }

    /**
     * 保存用户是否第一次进入应用
     *
     * @param b
     */
    public static void putUserIsFirst(boolean b) {
        ElectricVehicleApp.getDataKeeper().put(AppConstants.IS_USER_FIRST, b);
    }

    /**
     * 获取用户是否第一次进入应用
     */
    public static boolean getUserIsFirst() {
        return ElectricVehicleApp.getDataKeeper().get(AppConstants.IS_USER_FIRST, true);
    }

    /**
     * 保存用户选择的语言
     *
     * @param i
     */
    public static void putUserLuanguage(boolean isChinese) {
        ElectricVehicleApp.getDataKeeper().put(AppConstants.LANGUAGE_ID, isChinese);
    }

    /**
     * 获取用户设置的语言
     */
    public static boolean getUserLuanguage() {
        return ElectricVehicleApp.getDataKeeper().get(AppConstants.LANGUAGE_ID, true);
    }

    public static void setLanguageConfig(Context context, boolean isChinese) {
        Resources resources = context.getResources();//获得res资源对象
        Configuration config = resources.getConfiguration();//获得设置对象
        DisplayMetrics dm = resources.getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
        config.locale = isChinese ? Locale.CHINESE : Locale.ENGLISH;
        resources.updateConfiguration(config, dm);
        Utils.putUserLuanguage(isChinese);
    }


    private void setAppLanguage(Context context) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (LanguageUtil.getCountry().equals("CN")) {
            config.locale = Locale.CHINESE;
        } else {
            config.locale = Locale.ENGLISH;
        }
        resources.updateConfiguration(config, dm);
    }


    //进入登录界面
    public static void gotoLogin() {
        Intent intent = new Intent(ElectricVehicleApp.getApp(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ElectricVehicleApp.getApp().startActivity(intent);
    }

    //上传base64图片
    public static String base64Pic(String pic) {
        String base64Pic = "";
        if (!TextUtils.isEmpty(pic)) { //照片

            try {
                FileInputStream is = new FileInputStream(pic);
                byte[] picData = new byte[is.available()];
                is.read(picData);
                is.close();
                BASE64Encoder encoder = new BASE64Encoder();
                base64Pic = encoder.encode(picData);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return base64Pic;
    }

    //压缩图片
    public static String getPicUrl(String picStr) {
        try {
            File fileName = new File(picStr);
            if (FileOperateUtils.getFileSize(fileName) > Constant.PIC_SIZE_LIMIT) {
                Bitmap newBitmap = ImageUtils.compressImageFromFile(picStr);//获取压缩后的bitmap
                if (newBitmap != null) {
                    if (fileName.exists()) {
                        fileName.delete(); //删掉原文件
                    }
                    //创建新文件
                    ImageUtils.compressBmpToFile(newBitmap, new File(picStr));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return picStr;
    }

    /**
     * 清除用户相关数据
     *
     * @return
     */
    public static void clear() {
        ElectricVehicleApp.getDataKeeper().remove(AppConstants.USER_LOGIN_INFO);
    }

    /**
     * 高亮字体
     *
     * @param view    TextView
     * @param oriStr  源字符画
     * @param keyword 关键字符串
     * @param color   颜色
     */
    public static void highlightStr(TextView view, String oriStr, String keyword, int color) {
        int start = oriStr.indexOf(keyword);
        SpannableStringBuilder style = new SpannableStringBuilder(oriStr);
        style.setSpan(new ForegroundColorSpan(color), start, keyword.length() + start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(style);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        return DeviceUtils.dp2px(ElectricVehicleApp.getApp(), dpValue);
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        return ElectricVehicleApp.getApp().getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {
        return ElectricVehicleApp.getApp().getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 居中显示圆角dialog
     */
    public static AlertDialog showCornerDialog(Context context, View view, float width, float height) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Dialog);
        builder.setView(view);
        AlertDialog ad = builder.create();  //创建对话框
        ad.show();
        Window dialogWindow = ad.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = dp2px(width); // 宽度
        lp.height = dp2px(height); // 高度
        dialogWindow.setAttributes(lp);
        return ad;
    }

    /**
     * 居中显示圆角dialog
     * 大小屏幕宽高比例
     */
    public static AlertDialog showCornersDialog(Context context, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Dialog);
        builder.setView(view);
        AlertDialog ad = builder.create();  //创建对话框
        ad.show();
        Window dialogWindow = ad.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = (int) (getScreenWidth() * 0.8); // 宽度
        lp.height = (int) (getScreenHeight() * 0.8); // 高度
        dialogWindow.setAttributes(lp);
        return ad;
    }

    //判断应用是否处于前台
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return true;
                } else {
                    Log.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 网络是否连接
     *
     * @return
     */
    public static boolean isNetConnected() {
        return NetworkUtils.isConnected(ElectricVehicleApp.getApp());
    }

    /**
     * activity跳转
     *
     * @param nowActivity    当前的activity
     * @param targetActivity 目标的activity
     */
    public static void startActivity(Activity nowActivity, Class<?> targetActivity) {
        Intent intent = new Intent(nowActivity, targetActivity);
        nowActivity.startActivity(intent);
    }

    //拷贝assets文件到指定目录
    public static void copyBigDataToSD(Context context, String strOutFileName, String files) throws IOException {
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(strOutFileName);
        myInput = context.getAssets().open("vmp.zip");
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();
        unzipFile(files, strOutFileName);

    }

    //解压文件
    public static void unzipFile(String targetPath, String zipFilePath) {

        try {
            File zipFile = new File(zipFilePath);
            InputStream is = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(is);
            ZipEntry entry = null;
            System.out.println("开始解压:" + zipFile.getName() + "...");
            while ((entry = zis.getNextEntry()) != null) {
                String zipPath = entry.getName();
                try {

                    if (entry.isDirectory()) {
                        File zipFolder = new File(targetPath + File.separator
                                + zipPath);
                        if (!zipFolder.exists()) {
                            zipFolder.mkdirs();
                        }
                    } else {
                        File file = new File(targetPath + File.separator
                                + zipPath);
                        if (!file.exists()) {
                            File pathDir = file.getParentFile();
                            pathDir.mkdirs();
                            file.createNewFile();
                        }

                        FileOutputStream fos = new FileOutputStream(file);
                        int bread;
                        while ((bread = zis.read()) != -1) {
                            fos.write(bread);
                        }
                        fos.close();

                    }
                    System.out.println("成功解压:" + zipPath);

                } catch (Exception e) {
                    System.out.println("解压" + zipPath + "失败");
                    continue;
                }
            }
            zis.close();
            is.close();
            System.out.println("解压结束");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }


    /**
     * 隐藏手机号中间4位
     *
     * @param mobile
     * @return
     */
    public static String getPhoneFirstAndEnd(String mobile) {
        if (CheckUtils.isEmpty(mobile) || mobile.length() < 11) {
            return mobile;
        }
        String mobileFirst = mobile.substring(0, 3);
        String mobileEnd = mobile.substring(mobile.length() - 4, mobile.length());
        return mobileFirst + "****" + mobileEnd;
    }

    /**
     * 隐藏身份证中间8位
     *
     * @param idCard
     * @return
     */
    public static String getIdCardFirstAndEnd(String idCard) {
        if (CheckUtils.isEmpty(idCard) || idCard.length() < 15) {
            return idCard;
        }
        String idCardFirst = idCard.substring(0, 3);
        String idCardEnd = idCard.substring(idCard.length() - 4, idCard.length());
        return idCardFirst + "***********" + idCardEnd;
    }

    public static boolean equalList(List list1, List list2) {
        if (list1.size() != list2.size())
            return false;
        for (Object object : list1) {
            if (!list2.contains(object))
                return false;
        }
        return true;

    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPen(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    /**
     * 强制帮用户打开GPS
     *
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

//    public GeoPoint gpsToBaidu(String data) {//data格式  nmea标准数据  ddmm.mmmmm,ddmm.mmmm 如3030.90909,11449.1234
//
//        String[] p = data.split(",");
//
//        int lat = (int) (((int) (Float.valueOf(p[0]) /100) + (100* (Float//将ddmm.mmmm格式转成dd.ddddd
//
//                .valueOf(p[0]) /100.0- (int) (Float.valueOf(p[0]) /100)) /60.0)) * 1E6);
//
//        int lon = (int) (((int) (Float.valueOf(p[1]) /100) + (100* (Float
//
//                .valueOf(p[1]) /100.0- (int) (Float.valueOf(p[1]) /100)) /60.0)) * 1E6);
//
//        GeoPoint pt =new GeoPoint(lat, lon);
//
//        return CoordinateConvert.fromWgs84ToBaidu(pt);//转成百度坐标
//
//    }


}
