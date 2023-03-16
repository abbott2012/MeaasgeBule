package com.guoji.mobile.cocobee.utils;

import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;


public class LanguageUtil {
    /**
     * 获取手机设置的语言国家
     */
    public static String getCountry() {
        Locale locale;
        //7.0以上和7.0以下获取系统语言方式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        return locale.getCountry();
    }
}
