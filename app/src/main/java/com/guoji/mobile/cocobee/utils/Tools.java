package com.guoji.mobile.cocobee.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/10/27.
 */
public class Tools {

    /**
     * 验证身份证号是否符合规则
     *
     * @param text 身份证号
     * @return
     */
    public static boolean personIdValidation(String text) {
        String regx = "[0-9]{17}x";
        String reg1 = "[0-9]{15}";
        String regex = "[0-9]{18}";
        return text.matches(regx) || text.matches(reg1) || text.matches(regex);
    }

    public static boolean isMobileNO(String mobiles) {
        String telRegex = "[1][3578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles))
            return false;
        else
            return mobiles.matches(telRegex);
    }

    public static int bytesToInt(byte[] ary, int offset) {
        int value;
        value = (int) ((ary[offset] & 0xFF)
                | ((ary[offset + 1] << 8) & 0xFF00)
                | ((ary[offset + 2] << 16) & 0xFF0000)
                | ((ary[offset + 3] << 24) & 0xFF000000));
        return value;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv + " ");
        }
        return stringBuilder.toString().toUpperCase();
    }


    public static String bytesToHexString1(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            String s = hv.toUpperCase();
            if (s.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(s + " ");
        }
        return stringBuilder.toString();
    }


    public static String bytesToHexString2(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    /**
     * String转指定长度的byte数组，不足位数补0
     *
     * @param s      s为要转换的string
     * @param length length为要转换成的byte数组的长度
     * @return 指定长度的字节数组
     */
    public static byte[] getBytes(String s, int length) {
        int fixLength = length - s.getBytes().length;
        if (s.getBytes().length < length) {
            byte[] S_bytes = new byte[length];
            System.arraycopy(s.getBytes(), 0, S_bytes, 0, s.getBytes().length);
            for (int x = length - fixLength; x < length; x++) {
                S_bytes[x] = 0x00;
            }
            return S_bytes;
        }
        return s.getBytes();
    }


    /**
     * 十进制转16进制
     *
     * @param n 10进制数据
     * @return 16进制
     */
    public static String intToHex(int n) {
        //StringBuffer s = new StringBuffer();
        StringBuilder sb = new StringBuilder(8);
        String a;
        char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        while (n != 0) {
            sb = sb.append(b[n % 16]);
            n = n / 16;
        }
        a = sb.reverse().toString();
        return a;
    }





    /**
     * 将给定的用十进制分段格式表示的ipv4地址字符串转换成字节数组
     */
    public static byte[] ipv4Address2BinaryArray(String ipAdd) {
        byte[] binIP = new byte[4];
        String[] strs = ipAdd.split("\\.");
        for (int i = 0; i < strs.length; i++) {
            binIP[i] = (byte) Integer.parseInt(strs[i]);
        }
        return binIP;
    }


    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
            b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return b;
    }

    public static int byte2ToUnsignedShort(byte[] bytes) {
        return byte2ToUnsignedShort(bytes, 0);
    }

    public static int byte2ToUnsignedShort(byte[] bytes, int off) {
        int high = bytes[off];
        int low = bytes[off + 1];
        return (high << 8 & 0xFF00) | (low & 0xFF);
    }

    public static int byte2int(byte[] res) {
        // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000

        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或
                | ((res[2] << 24) >>> 8) | (res[3] << 24);
        return targets;
    }


    public static byte[] int2byte(int res) {
        byte[] targets = new byte[4];
        targets[0] = (byte) (res & 0xff);// 最低位
        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
        return targets;
    }

    /**
     * 十六进制转换成字节数组
     *
     * @param hex
     * @return
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    /**
     * 十六进制转二进制
     */
    public static String HToB(String a) {
        String b = Integer.toBinaryString(Integer.valueOf(toD(a, 16)));
        return b;

    }

    /**
     * 二进制转十六进制
     */
    public static String BToH(String a) {
        // 将二进制转为十进制再从十进制转为十六进制
        String b = Integer.toHexString(Integer.valueOf(toD(a, 2)));
        return b;
    }

    /**
     * 任意进制数转为十进制数
     * a为16进制，b=16；三
     */
    public static String toD(String a, int b) {
        int r = 0;
        for (int i = 0; i < a.length(); i++) {
            r = (int) (r + formatting(a.substring(i, i + 1))
                    * Math.pow(b, a.length() - i - 1));
        }
        return String.valueOf(r);
    }


    /**
     * 将十六进制中的字母转为对应的数字
     */
    public static int formatting(String a) {
        int i = 0;
        for (int u = 0; u < 10; u++) {
            if (a.equals(String.valueOf(u))) {
                i = u;
            }
        }
        if (a.equals("a")) {
            i = 10;
        }
        if (a.equals("b")) {
            i = 11;
        }
        if (a.equals("c")) {
            i = 12;
        }
        if (a.equals("d")) {
            i = 13;
        }
        if (a.equals("e")) {
            i = 14;
        }
        if (a.equals("f")) {
            i = 15;
        }
        return i;
    }

    public static int ascii2int(int t) {
        if ((t >= 0x30) && (t <= 0x39)) {  // 是数字
            return (t - 0x30);
        } else if ((t >= 0x41) && (t <= 0x46)) { // A~F
            return (t - 0x41 + 0x0A);
        } else if ((t >= 0x61) && (t <= 0x66)) {// a~f
            return (t - 0x61 + 0x0A);
        } else {
            return 0;
        }

    }

    /**
     * 16进制的字符串 转成10进制的字符串
     *
     * @return 10进制的字符串
     */
    public static String getHexStringToString(byte[] src) {
        String byteString = bytesToHexString(src);
        if (TextUtils.isEmpty(byteString)) return "";
        BigInteger bigText = new BigInteger(byteString, 16);
        return String.valueOf(bigText);
    }


    public static int bytesAscii2int(byte[] b) {
        byte[] tmp = new byte[4];
        int x = 0, low_half_byte, high_half_byte;

        tmp[0] = b[0];
        low_half_byte = byte2int(tmp);
        tmp[0] = b[1];
        high_half_byte = byte2int(tmp);

//        Log.i("xy", "b0->" + b[0]);
//        Log.i("xy", "b1->" + b[1]);
//        Log.i("xy", "int1->" + low_half_byte);
//        Log.i("xy", "int2->" + high_half_byte);

        x = ((ascii2int(low_half_byte)) << 4) + (ascii2int(high_half_byte));

//        Log.i("xy", "int3->" + ascii2int(low_half_byte));
//        Log.i("xy", "int4->" + ascii2int(high_half_byte));
//        Log.i("xy", "x->" + x);

        return x;
    }

    public static int bytesAsciiToInt(byte[] b) {
        byte[] tmp = new byte[4];
        int x = 0, low_half_byte, mid_half_byte, high_half_byte;

        tmp[0] = b[0];
        low_half_byte = byte2int(tmp);

        tmp[0] = b[1];
        mid_half_byte = byte2int(tmp);

        tmp[0] = b[2];
        high_half_byte = byte2int(tmp);

        x = ((ascii2int(low_half_byte)) << 4) + (ascii2int(mid_half_byte)) + (ascii2int(high_half_byte));

        return x;
    }


    public static byte calCheck(byte[] data) {
        byte[] a = new byte[1];
        for (int i = 0; i < data.length - 1; i++) {
            a[0] += data[i];
        }

        a[0] = (byte) ((~a[0]) + 1);
        return a[0];
    }


    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * GPS坐标转百度地图坐标
     *
     * @param sourceLatLng
     * @return
     */
    public static LatLng gpsToBaidu(LatLng sourceLatLng) {

        // 将GPS设备采集的原始GPS坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        return converter.convert();

    }


    public static Object[] splitAry(byte[] ary, int subSize) {
        int count = ary.length % subSize == 0 ? ary.length / subSize : ary.length / subSize + 1;
        List<List<Byte>> subAryList = new ArrayList<List<Byte>>();
        for (int i = 0; i < count; i++) {
            int index = i * subSize;
            List<Byte> list = new ArrayList<Byte>();
            int j = 0;
            while (j < subSize && index < ary.length) {
                list.add(ary[index++]);
                j++;
            }
            subAryList.add(list);
        }

        Object[] subAry = new Object[subAryList.size()];
        for (int i = 0; i < subAryList.size(); i++) {
            List<Byte> subList = subAryList.get(i);
            byte[] subAryItem = new byte[subList.size()];
            for (int j = 0; j < subList.size(); j++) {
                subAryItem[j] = subList.get(j);
            }
            subAry[i] = subAryItem;
        }
        return subAry;
    }


    /**
     * 人民币转成大写
     *
     * @param value
     * @return String
     */
    public static String hangeToBig(double value) {
        char[] hunit = {'拾', '佰', '仟'}; // 段内位置表示
        char[] vunit = {'万', '亿'}; // 段名表示
        char[] digit = {'零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'}; // 数字表示
        long midVal = (long) (value * 100); // 转化成整形
        String valStr = String.valueOf(midVal); // 转化成字符串
        String head = valStr.substring(0, valStr.length() - 2); // 取整数部分
        String rail = valStr.substring(valStr.length() - 2); // 取小数部分
        String prefix = ""; // 整数部分转化的结果
        String suffix = ""; // 小数部分转化的结果
        // 处理小数点后面的数
        if (rail.equals("00")) { // 如果小数部分为0
            suffix = "整";
        } else {
            suffix = digit[rail.charAt(0) - '0'] + "角" + digit[rail.charAt(1) - '0'] + "分"; // 否则把角分转化出来
        }
        // 处理小数点前面的数
        char[] chDig = head.toCharArray(); // 把整数部分转化成字符数组
        char zero = '0'; // 标志'0'表示出现过0
        byte zeroSerNum = 0; // 连续出现0的次数
        for (int i = 0; i < chDig.length; i++) { // 循环处理每个数字
            int idx = (chDig.length - i - 1) % 4; // 取段内位置
            int vidx = (chDig.length - i - 1) / 4; // 取段位置
            if (chDig[i] == '0') { // 如果当前字符是0
                zeroSerNum++; // 连续0次数递增
                if (zero == '0') { // 标志
                    zero = digit[0];
                } else if (idx == 0 && vidx > 0 && zeroSerNum < 4) {
                    prefix += vunit[vidx - 1];
                    zero = '0';
                }
                continue;
            }
            zeroSerNum = 0; // 连续0次数清零
            if (zero != '0') { // 如果标志不为0,则加上,例如万,亿什么的
                prefix += zero;
                zero = '0';
            }
            prefix += digit[chDig[i] - '0']; // 转化该数字表示
            if (idx > 0)
                prefix += hunit[idx - 1];
            if (idx == 0 && vidx > 0) {
                prefix += vunit[vidx - 1]; // 段结束位置应该加上段名如万,亿
            }
        }
        if (prefix.length() > 0)
            prefix += '圆'; // 如果整数部分存在,则有圆的字样
        return prefix + suffix; // 返回正确表示
    }


    // 校验Tag Alias 只能是数字,英文字母和中文
    public static boolean isValidTagAndAlias(String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_!@#$&*+=.|]+$");
        Matcher m = p.matcher(s);
        return m.matches();
    }


    /**
     * 校验数据
     *
     * @param oldValue 需要校验的byte数组
     * @return 校验位的字符串
     */
    public static String checkBleData(byte[] oldValue) {
        byte[] rcvByteArr = new byte[oldValue.length - 1];
        for (int i = 0; i < oldValue.length - 1; i++) {
            rcvByteArr[i] = oldValue[i];
        }
        return sendRcvByteNum(rcvByteArr);
    }

    /**
     * 数据校验
     *
     * @param sendByte
     * @return checksum   是 sof +cmd+len+opt+data 的和校验
     */
    public static String sendRcvByteNum(byte[] sendByte) {
        byte sum = 0;
        for (int i = 0; i < sendByte.length; i++) {
            sum += sendByte[i];
        }
        byte checksumByte = (byte) (~sum + 1);
        return bytesToHexString(new byte[]{checksumByte});
    }


}
