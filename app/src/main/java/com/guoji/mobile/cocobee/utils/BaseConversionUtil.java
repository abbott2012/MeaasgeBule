package com.guoji.mobile.cocobee.utils;

import java.io.UnsupportedEncodingException;

/**
 * 进制转换
 */
public class BaseConversionUtil {
    /**
     * 16进制中的字符集
     */
    private static final String HEX_CHAR = "0123456789ABCDEF";

    /**
     * 16进制中的字符集对应的字节数组
     */
    private static final byte[] HEX_STRING_BYTE = HEX_CHAR.getBytes();

    /**
     * 10进制字节数组转换为16进制字节数组
     * <p>
     * byte用二进制表示占用8位，16进制的每个字符需要用4位二进制位来表示，则可以把每个byte
     * 转换成两个相应的16进制字符，即把byte的高4位和低4位分别转换成相应的16进制字符，再取对应16进制字符的字节
     *
     * @param b 10进制字节数组
     * @return 16进制字节数组
     */
    public static byte[] byte2hex(byte[] b) {
        int length = b.length;
        byte[] b2 = new byte[length << 1];
        int pos;
        for (int i = 0; i < length; i++) {
            pos = 2 * i;
            b2[pos] = HEX_STRING_BYTE[(b[i] & 0xf0) >> 4];
            b2[pos + 1] = HEX_STRING_BYTE[b[i] & 0x0f];
        }
        return b2;
    }

    /**
     * 16进制字节数组转换为10进制字节数组
     * <p>
     * 两个16进制字节对应一个10进制字节，则将第一个16进制字节对应成16进制字符表中的位置(0~15)并向左移动4位，
     * 再与第二个16进制字节对应成16进制字符表中的位置(0~15)进行或运算，则得到对应的10进制字节
     *
     * @param b 10进制字节数组
     * @return 16进制字节数组
     */
    public static byte[] hex2byte(byte[] b) {
        if (b.length % 2 != 0) {
            throw new IllegalArgumentException("byte array length is not even!");
        }

        int length = b.length >> 1;
        byte[] b2 = new byte[length];
        int pos;
        for (int i = 0; i < length; i++) {
            pos = i << 1;
            b2[i] = (byte) (HEX_CHAR.indexOf(b[pos]) << 4 | HEX_CHAR.indexOf(b[pos + 1]));
        }
        return b2;
    }

    /**
     * 将16进制字节数组转成10进制字符串
     *
     * @param b 16进制字节数组
     * @return 10进制字符串
     */
    public static String hex2Str(byte[] b) {
        return new String(hex2byte(b));
    }

    /**
     * 将10进制字节数组转成16进制字符串
     *
     * @param b 10进制字节数组
     * @return 16进制字符串
     */
    public static String byte2HexStr(byte[] b) {
        return Integer.toHexString(Integer.parseInt(new String(b)));
    }


    public static byte[] hexString2Bytes(String hex) {

        if ((hex == null) || (hex.equals(""))) {
            return null;
        } else if (hex.length() % 2 != 0) {
            return null;
        } else {
            hex = hex.toUpperCase();
            int len = hex.length() / 2;
            byte[] b = new byte[len];
            char[] hc = hex.toCharArray();
            for (int i = 0; i < len; i++) {
                int p = 2 * i;
                b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p + 1]));
            }
            return b;
        }
    }

    /*
     * 字符转换为字节
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 字节数组转为普通字符串（ASCII对应的字符）
     *
     * @param bytearray byte[]
     * @return String
     */
    public static String bytetoString(byte[] bytearray) {
        String result = "";
        char temp;
        int length = bytearray.length;
        for (int i = 0; i < length; i++) {
            temp = (char) bytearray[i];
            result += temp;
        }
        return result;
    }

    /**
     * 数字字符串转ASCII码字符串
     * @param content 字符串
     * @return ASCII字符串
     */
    public static String StringToAsciiString(String content) {
        String result = "";
        int max = content.length();
        for (int i = 0; i < max; i++) {
            char c = content.charAt(i);
            String b = Integer.toHexString(c);
            result = result + b;
        }
        return result;
    }

    /**
     * 十六进制字符串装十进制
     *
     * @param hex 十六进制字符串
     * @return 十进制数值
     */
    public static int hexStringToAlgorism(String hex) {
        hex = hex.toUpperCase();
        int max = hex.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = hex.charAt(i - 1);
            int algorism = 0;
            if (c >= '0' && c <= '9') {
                algorism = c - '0';
            } else {
                algorism = c - 55;
            }
            result += Math.pow(16, max - i) * algorism;
        }
        return result;
    }

    /**
     * ASCII码字符串转数字字符串
     *
     * @param content ASCII字符串
     * @return 字符串
     */
    public static String AsciiStringToString(String content) {
        String result = "";
        int length = content.length() / 2;
        for (int i = 0; i < length; i++) {
            String c = content.substring(i * 2, i * 2 + 2);
            int a = hexStringToAlgorism(c);
            char b = (char) a;
            String d = String.valueOf(b);
            result += d;
        }
        return result;
    }

    /***
     * Ascii转16进制
     * @param str
     * @return
     */
    public static String convertStringToHex(String str) {

        char[] chars = str.toCharArray();

        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((byte) chars[i]));
        }
        return hex.toString();
    }

    /***
     * 16进制转ASCII
     * @param hex
     * @return
     */
    public static String convertHexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);

            temp.append(decimal);
        }

        return sb.toString();
    }

    /***
     * byte[] z转int
     * @param b
     * @return
     */
    public static int bytes2int(byte[] b) {
        int mask = 0xff;
        int temp = 0;
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res <<= 8;
            temp = b[i] & mask;
            res |= temp;
        }
        return res;
    }

    public static byte[] int2bytes(int num) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (num >>> (24 - i * 8));
        }
        return b;
    }

    /**
     * 中文转Unicode
     *
     * @param gbString
     * @return
     */
    public static String UnicodeEncoding(String gbString) {   //gbString = "测试"
        char[] utfBytes = gbString.toCharArray();   //utfBytes = [测, 试]
        String unicodeBytes = "";
        for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
            String hexB = Integer.toHexString(utfBytes[byteIndex]);   //转换为16进制整型字符串
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }

    /**
     * Unicode转中文
     */
    public static String decodeUnicode(String dataStr) {
        int start = 0;
        int end = 0;
        StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }


    /**
     * gb2312编码
     */
    public static String gb2312decode(String string) throws UnsupportedEncodingException {
        byte[] bytes = new byte[string.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            byte high = Byte.parseByte(string.substring(i * 2, i * 2 + 1), 16);
            byte low = Byte.parseByte(string.substring(i * 2 + 1, i * 2 + 2), 16);
            bytes[i] = (byte) (high << 4 | low);
        }
        return new String(bytes, "gb2312");
    }

    /**
     * gb2312解码
     */
    public static String gb2312eecode(String string) throws UnsupportedEncodingException {
        StringBuffer gbkStr = new StringBuffer();
        byte[] gbkDecode = string.getBytes("gb2312");
        for (byte b : gbkDecode) {
            gbkStr.append(Integer.toHexString(b & 0xFF));
        }
        return gbkStr.toString();
    }

    /**
     * 和校验
     * SUM（cmd, Length, Data0…DataN）^0xFF
     */
    public static byte getCheckSum(byte[] packBytes) {
        int checkSum = 0;

        for (int i = 0; i < packBytes.length; i++) {
            checkSum += packBytes[i];
        }
        checkSum &= 0xff;
        return (byte) checkSum;
    }

}