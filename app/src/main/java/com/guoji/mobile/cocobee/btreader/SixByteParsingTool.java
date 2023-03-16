package com.guoji.mobile.cocobee.btreader;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 6个字节解析工具类
 */
public class SixByteParsingTool {


    public static boolean hasPakageStart(String strArr) {
        return strArr.contains("0B");
    }

    public static boolean containList(String[] arr, String targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }

    public static String getStringTextId(List<String> stringList) {
        if (stringList == null && stringList.isEmpty()) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (String item : stringList) {
            stringBuffer.append(item);
        }
        return stringBuffer.toString();
    }


    /**
     * 分割字符串
     *
     * @param text 需要分割的字符串
     * @param type 根据什么类分割
     * @return 返回的数组
     */
    public static List<String> spiltCode(String text, String type) {
        List<String> list = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(text, type);
        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }
        return list;
    }

    public static List<Integer> spiltIdOfData(String text, String type) {
        List<Integer> list = new ArrayList<Integer>();
        StringTokenizer st = new StringTokenizer(text, type);
        while (st.hasMoreTokens()) {
            list.add(Integer.parseInt(st.nextToken()));
        }
        return list;
    }

    /**
     * 16进制转成10进制
     *
     * @param num
     * @return
     */
    public static Integer get10HexNum(String num) {
        return Integer.parseInt(num.substring(0), 16);

    }

}
