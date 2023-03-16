package com.bql.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 辅助判断工具类
 * Created by Cyarie on 2016/1/18.
 */
public class CheckUtils {

    /**
     * 字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(CharSequence str) {
        return isNull(str) || str.toString().trim().length() == 0;
    }

    /**
     * 集合是否为空
     *
     * @param l
     * @return
     */
    public static boolean isEmpty(List<?> l) {
        return isNull(l) || l.isEmpty();
    }

    /**
     * map是否为空
     *
     * @param m
     * @return
     */
    public static boolean isEmpty(Map<?, ?> m) {
        return isNull(m) || m.isEmpty();
    }

    /**
     * 对象是否为空
     *
     * @param o
     * @return
     */
    public static boolean isNull(Object o) {
        return o == null;
    }


    /**
     * 功能：身份证的有效验证
     *
     * @param IDStr 身份证号
     * @return 有效：返回""   无效：返回String信息
     * @throws ParseException
     */
    public static String IDCardValidate(String IDStr) throws ParseException {
        String errorInfo = "";// 记录错误信息
        String[] ValCodeArr = {"1", "0", "X", "9", "8", "7", "6", "5", "4",
                "3", "2"};
        String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "10", "5", "8", "4", "2"};
        String Ai = "";
        // ================ 号码的长度 15位或18位 ================
        if (IDStr.length() != 15 && IDStr.length() != 18) {
            errorInfo = "身份证号码长度应该为15位或18位";
            return errorInfo;
        }
        // =======================(end)========================

        // ================ 数字 15位都应为数字  18位除最后一位都为数字 ================
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        if (isNumber(Ai) == false) {
            errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字";
            return errorInfo;
        }
        // =======================(end)========================

        // ================ 出生年月是否有效 ================
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份
        if (isDataFormat(strYear + "-" + strMonth + "-" + strDay) == false) {
            errorInfo = "身份证生日无效";
            return errorInfo;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                || (gc.getTime().getTime() - s.parse(
                strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
            errorInfo = "身份证生日不在有效范围";
            return errorInfo;
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            errorInfo = "身份证月份无效";
            return errorInfo;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            errorInfo = "身份证日期无效";
            return errorInfo;
        }
        // =====================(end)=====================

        // ================ 地区码是否有效 ================
        Hashtable<String, String> h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            errorInfo = "身份证地区编码错误";
            return errorInfo;
        }
        // ====================(end)==========================


        // ================ 判断最后一位的值 ================
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                    * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (IDStr.length() == 18) {
            if (Ai.equals(IDStr) == false) {
                errorInfo = "身份证无效，不是合法的身份证号码";
                return errorInfo;
            }
        } else {
            return "";
        }
        // =====================(end)=====================
        return "";
    }

    /**
     * 功能：设置地区编码
     *
     * @return Hashtable 对象
     */
    private static Hashtable<String, String> GetAreaCode() {
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    /**
     * 验证日期字符串是否是YYYY-MM-DD格式
     *
     * @param str
     * @return
     */
    public static boolean isDataFormat(String str) {
        boolean flag = false;
        // String
        // regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
        String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
        Pattern pattern1 = Pattern.compile(regxStr);
        Matcher isNo = pattern1.matcher(str);
        if (isNo.matches()) {
            flag = true;
        }
        return flag;
    }


    //    // 判断是否全是数字
    //    public static boolean isNumeric(String str) {
    //        Pattern pattern = Pattern.compile("[0-9]*");
    //        Matcher isNum = pattern.matcher(str);
    //        if (!isNum.matches()) {
    //            return false;
    //        }
    //        return true;
    //    }

    /**
     * 判断字符串是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        if (isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*.[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 比较日期大小
     *
     * @return 第一个值大于等于第二个值返回true, 异常也返回true
     */
    public static boolean compareDate(String DATE1, String DATE2) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() >= dt2.getTime()) {
                return true;
            } else if (dt1.getTime() < dt2.getTime()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断是否是密码  0~9 a~z A~Z 6至20位密码
     *
     * @param password
     * @return
     */
    public static boolean isPassword(String password) {
        if (Pattern.matches("^[0-9a-zA-Z]{6,20}$", password)) {
            return true;
        }
        return false;
    }


    /**
     * 判断是否是密码  0~9 a~z A~Z 半角符号 6至18位密码
     * <p/>
     * /[\u0000-\u00FF]/ 半角符号
     * <p/>
     * /[\u4E00-\u9FA5]/ 汉字
     * <p/>
     * /[\uFF00-\uFFFF]/ 全角符号
     *
     * @param password
     * @return
     */
    public static boolean isAllPassword(String password) {
        if (Pattern.matches("^[0-9a-zA-Z\\u0000-\\u00FF]{6,18}$", password)) {
            return true;
        }
        return false;
    }


    /**
     * 验证用户名 4-20位字符 字母开头
     *
     * @param name
     * @return
     */
    public static boolean isUserName(String name) {
        if (Pattern.matches("^[a-zA-Z]\\w{3,19}$", name)) {
            return true;
        }
        return false;
    }


    /**
     * 电话号码验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isLandLinePhone(String str) {
        Pattern p1 = null, p2 = null;
        Matcher m = null;
        boolean b = false;
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
        if (str.length() > 9) {
            m = p1.matcher(str);
            b = m.matches();
        } else {
            m = p2.matcher(str);
            b = m.matches();
        }
        return b;
    }

    /**
     * 验证手机号码
     *
     * @param phoneNo
     * @return
     */
    public static boolean isMobilePhone(String phoneNo) {
        boolean isV = false;
        if (phoneNo.matches("^(1[3|5|8|4|7]\\d{9})$")) {
            isV = true;
        }
        return isV;
    }

    /**
     * 验证是否为号码 包括电话号码和手机号码
     *
     * @param phone
     * @return
     */
    public static boolean isPhoneAll(String phone) {
        return isMobilePhone(phone) || isLandLinePhone(phone);
    }


    /**
     * 判断文件名是否符合给定的文件名后缀
     *
     * @param checkItsEnd 文件名
     * @param fileEndings 文件名后缀
     * @return
     */
    public static boolean checkEndsWithInStringArray(String checkItsEnd, String[] fileEndings) {

        for (String aEnd : fileEndings) {
            if (checkItsEnd.endsWith(aEnd))
                return true;
        }
        return false;
    }

    //在进程中去寻找当前APP的信息，判断是否在前台运行
    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();
        int myPid = android.os.Process.myPid();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (myPid == appProcess.pid & appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断是否是httpURL
     *
     * @param url
     * @return
     */
    public static final boolean isHttpUrl(String url) {
        String regEx = "^(https|http://){0,1}([a-zA-Z0-9]{1,}[a-zA-Z0-9\\-]{0,}\\.){0,4}([a-zA-Z0-9]{1,}[a-zA-Z0-9\\-]{0,}\\.[a-zA-Z0-9]{1,})/{0,1}$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(url);
        return m.find();
    }

    /**
     * 判断是否是Ip地址
     *
     * @param url
     * @return
     */
    public static final boolean isIPUrl(String url) {
        String regEx = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(url);
        return m.find();
    }

    /**
     * 判断字符串是否为URL
     *
     * @param url
     * @return
     */
    public static boolean isWebUrl(String url) {
        if (isEmpty(url)) {
            return false;
        }
        url = url.toLowerCase();
        return url.startsWith("http://") || url.startsWith("https://") || url.startsWith("ftp://");
    }

    /**
     * 是否为邮件地址
     *
     * @param email 邮件地址
     * @return
     */
    public static boolean isVaildEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        String emailPattern = "[a-zA-Z0-9_-|\\.]+@[a-zA-Z0-9_-]+.[a-zA-Z0-9_.-]+";
        boolean result = Pattern.matches(emailPattern, email);
        return result;
    }

    /**
     * 是否为十六进制数
     *
     * @param key
     * @return
     */
    public static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f')) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较两个字符串是否相等
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean equalsString(String str1, String str2) {
        if (str1 != null) {
            return str1.equals(str2);
        } else {
            return str2 == null;
        }
    }

    /**
     * 比较两个字符串忽略顺序
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean compareStringIgnoreOrder(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return str1 == str2;
        }
        if (str1.length() == str2.length()) {
            for (int i = 0; i < str1.length(); i++) {
                if (!str2.contains(String.valueOf(str1.charAt(i)))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 比较类似于3.5.0这样的版本号字符串的大小
     *
     * @param str1 版本号1
     * @param str2 版本号2
     * @return str1 > str2 返回 正数；str1 = str2 返回 0；str1 < str2 返回 负数；
     */
    public static int compareVerString(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return 0;
        }

        String[] cons1 = str1.split("\\.");
        String[] cons2 = str2.split("\\.");

        int i = 0;
        try {
            while (i < cons1.length && i < cons2.length) {
                int int1 = Integer.parseInt(cons1[i]);
                int int2 = Integer.parseInt(cons2[i]);
                int res = int1 - int2;
                if (res == 0) {
                    i++;
                    continue;
                } else {
                    return res;
                }
            }

            return cons1.length - cons2.length;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context
     * @param packageName 应用包名
     * @return
     */
    public static boolean isAPKExist(Context context, String packageName) {
        // 获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        // 从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    /**
     * 服务是否运行
     *
     * @param mContext
     * @param className
     * @return
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (serviceList.size() == 0) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 进程是否运行
     */
    public static boolean isProessRunning(Context context, String proessName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : lists) {
            if (info.processName.equals(proessName)) {
                isRunning = true;
                return isRunning;
            }
        }

        return isRunning;
    }

    /**
     * 判断是否有软控制键（手机底部几个按钮）
     *
     * @param activity
     * @return
     */
    public boolean isSoftKeyAvail(Activity activity) {
        final boolean[] isSoftkey = {false};
        final View activityRootView = (activity).getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int rootViewHeight = activityRootView.getRootView().getHeight();
                int viewHeight = activityRootView.getHeight();
                int heightDiff = rootViewHeight - viewHeight;
                if (heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.
                    isSoftkey[0] = true;
                }
            }
        });
        return isSoftkey[0];
    }

    /**
     * 判断输入法是否处于激活状态
     *
     * @param context
     * @return
     */
    public static boolean isActiveSoftInput(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

    /**
     * 判断当前设备是否为手机
     *
     * @param context
     * @return
     */
    public static boolean isPhone(Context context) {
        TelephonyManager telephony = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            return false;
        } else {
            return true;
        }
    }
}
