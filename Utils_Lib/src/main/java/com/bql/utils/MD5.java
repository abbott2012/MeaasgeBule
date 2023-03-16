package com.bql.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Cyarie on 2016/1/4.
 */
public class MD5 {

    // 返回32位md5加密秘文
    public static String md5(String s) {
        if (s == null)
            return null;
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuffer hexString = new StringBuffer();
            String temps = "";
            for (int i = 0; i < messageDigest.length; i++) {
                temps = Integer.toHexString(0xFF & messageDigest[i]);
                if (temps.length() == 1)
                    hexString.append("0");// 保证2位
                hexString.append(temps);
            }
            String getmd5 = hexString.toString();
            return getmd5;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }
}
