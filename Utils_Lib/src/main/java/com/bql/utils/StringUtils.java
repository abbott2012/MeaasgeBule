package com.bql.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * 字符处理工具类
 * Created by Cyarie on 2016/1/5.
 */
public class StringUtils {

    private final static String TAG = StringUtils.class.getSimpleName();

    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String WWW_LOWER = "www.";
    public static final String WWW_UPPER = "WWW.";

    static HashMap<CharSequence, Integer> CHAR_MAP = new HashMap<CharSequence, Integer>(9);

    static {
        CHAR_MAP.put("一", 1);
        CHAR_MAP.put("二", 2);
        CHAR_MAP.put("三", 3);
        CHAR_MAP.put("四", 4);
        CHAR_MAP.put("五", 5);
        CHAR_MAP.put("六", 6);
        CHAR_MAP.put("七", 7);
        CHAR_MAP.put("八", 8);
        CHAR_MAP.put("九", 9);
    }

    /**
     * 获取阿拉伯数字 0~9
     *
     * @param ch 汉子数字
     * @return
     */
    private static int getIntFromMap(char ch) {
        Integer result = CHAR_MAP.get(ch);
        return result == null ? 0 : result;
    }

    /**
     * 将0~9999以内的小写汉字转化为数字
     * 例如：三千四百五十六转化为3456
     *
     * @param number 汉子数字
     * @return
     */
    public static int parseChineseNumber(String number) {

        if (CheckUtils.isEmpty(number)) {
            return -1;
        }
        int result = 0;
        int index = -1;
        index = number.indexOf('千');
        if (index > 0) {
            result += getIntFromMap(number.charAt(index - 1)) * 1000;
        }
        index = number.indexOf('百');
        if (index > 0) {
            result += getIntFromMap(number.charAt(index - 1)) * 100;
        }
        index = number.indexOf('十');
        if (index > 0) {
            result += getIntFromMap(number.charAt(index - 1)) * 10;
        } else if (index == 0) {
            result += 10;
        }
        index = number.length();
        if (index > 0) {
            result += getIntFromMap(number.charAt(index - 1));
        }
        return result;
    }

    /**
     * 获取字符串的长度
     *
     * @param str
     * @return
     */
    public static int length(CharSequence str) {
        return str == null ? 0 : str.length();
    }


    /**
     * UTF-8编码
     *
     * @param str
     * @return
     */
    public static String utf8Encode(String str) {
        if (!CheckUtils.isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UnsupportedEncodingException occurred. ", e);
            }
        }
        return str;
    }


    /**
     * UTF-8编码
     *
     * @param str
     * @param defultReturn
     * @return
     */
    public static String utf8Encode(String str, String defultReturn) {
        if (!CheckUtils.isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return defultReturn;
            }
        }
        return str;
    }


    /**
     * 格式化double数字  保留两位小数
     *
     * @param num
     * @return
     */
    public static String formatDouble(double num) {
        String pattern = "#0.00";
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(num);

    }


    /**
     * 将IP地址转化为string类型
     *
     * @param i int IP地址
     * @return
     */
    public static String transformIPToString(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append(i & 0xFF);
        sb.append(".");
        sb.append((i >> 8) & 0xFF);
        sb.append(".");
        sb.append((i >> 16) & 0xFF);
        sb.append(".");
        sb.append((i >> 24) & 0xFF);
        return sb.toString();
    }


    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }


    /**
     * 将字节转化为十六进制数
     *
     * @param buf
     * @return
     */
    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    /**
     * 将十六进制数转化为字节
     *
     * @param hexString
     * @return
     */
    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    /**
     * url编码
     *
     * @param url
     * @return
     */
    public static String urlEncode(String url) {
        StringBuffer urlB = new StringBuffer();
        StringTokenizer st = new StringTokenizer(url, "/ ", true);
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            if (tok.equals("/"))
                urlB.append("/");
            else if (tok.equals(" "))
                urlB.append("%20");
            else {
                try {
                    urlB.append(URLEncoder.encode(tok, "UTF-8"));
                } catch (java.io.UnsupportedEncodingException uee) {

                }
            }
        }
        // Log.d(TAG, "urlEncode urlB:" + urlB.toString());
        return urlB.toString();
    }


    /**
     * html 编码
     *
     * @param s
     * @return
     */
    public static String htmlEncode(String s) {
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;"); //$NON-NLS-1$
                    break;
                case '>':
                    sb.append("&gt;"); //$NON-NLS-1$
                    break;
                case '&':
                    sb.append("&amp;"); //$NON-NLS-1$
                    break;
                case '\'':
                    sb.append("&apos;"); //$NON-NLS-1$
                    break;
                case '"':
                    sb.append("&quot;"); //$NON-NLS-1$
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }


    /**
     * 获得当前年份
     *
     * @return
     */
    public static int getCurrentYear() {

        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * 替换无效的字符串
     *
     * @param srcStr   原字符串
     * @param placeStr 替代字符串
     * @return
     */
    public static String isEmpty(String srcStr, String placeStr) {
        if (CheckUtils.isEmpty(srcStr)) {
            return placeStr;
        } else {
            return srcStr;
        }
    }


    /**
     * 设置一句话的第几个字的颜色
     */
    public static SpannableString setSelectTextColor(String content, int color, int start, int end) {
        if (CheckUtils.isEmpty(content)) {
            return null;
        }
        SpannableString sp = new SpannableString(content);
        sp.setSpan(new ForegroundColorSpan(color), start, end,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return sp;
    }

    /**
     * @param str 拼接多个string
     * @return
     */
    public static String append(String... str) {
        StringBuilder sb = new StringBuilder();
        for (String s : str) {
            sb.append(s);
        }
        return sb.toString();
    }



    private static String hexString = "0123456789abcdef";

    /*
     * 将字符串编码成16进制数字,适用于所有字符（包括中文）
     */
    public static String encode(String str) {
        //根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        //将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f)));
        }
        return sb.toString();
    }

    /*
     * 将16进制数字解码成字符串,适用于所有字符（包括中文）
     */
    public static String decode(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
        //将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
        return new String(baos.toByteArray());
    }

}
