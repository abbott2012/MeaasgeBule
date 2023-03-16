/**
 * @Title: AESOperator.java
 * @Package com.protruly.showWeb.common.utils.aes;
 * @Description: 对接andriod/ios接口通用aes加解密工具类
 * @author 彭彩云
 * @date 2016年4月28日
 * Ltd. Copyright (c) 2016 深圳市保千里电子有限公司版权所有,All rights reserved
 */
package com.bql.utils;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 是一种可逆加密算法，对用户的敏感信息加密处理
 */
public class AESOperator {

    private final static String MODECODE = "AES/CBC/PKCS5Padding";
    private final static String CHARSET = "UTF-8";
    // 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
    public final static String sKey = "9C51959C55E2DD5C";
    private final static String ivParameter = "LUCKSHOW20160428";

    public static String encrypt(String content, String secretKey) throws Exception {
        if (secretKey == null) {
            return null;
        }
        if (secretKey.length() != 16) {
            return null;
        }
        Cipher cipher = Cipher.getInstance(MODECODE);
        byte[] raw = secretKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(content.getBytes(CHARSET));
        // 此处使用BASE64做转码。
        // return new BASE64Encoder().encode(encrypted);
        return new String(parseByte2HexStr(encrypted));
    }

    // 加密
    public static String encrypt(String content) throws Exception {
        Cipher cipher = Cipher.getInstance(MODECODE);
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(content.getBytes(CHARSET));
        return new String(parseByte2HexStr(encrypted));
        // 此处使用BASE64做转码。
        // return new BASE64Encoder().encode(encrypted);
    }

    // 解密
    public static String decrypt(String content) throws Exception {
        try {
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance(MODECODE);
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            // 先用base64解密
            // byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);
            byte[] original = cipher.doFinal(parseHexStr2Byte(content));
            String originalString = new String(original, CHARSET);
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }

    public static String decrypt(String sSrc, String key) throws Exception {
        try {
            byte[] raw = key.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance(MODECODE);
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            // 先用base64解密
            // byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);
            byte[] original = cipher.doFinal(parseHexStr2Byte(sSrc));
            String originalString = new String(original, CHARSET);
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * MD5加密
     *
     * @author 彭彩云
     * @date 2016年4月28日
     */
    public static String encodeMD5(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest code = MessageDigest.getInstance("MD5");
            code.update(str.getBytes());
            byte[] bs = code.digest();
            for (int i = 0; i < bs.length; i++) {
                int v = bs[i] & 0xFF;
                if (v < 16) {
                    sb.append(0);
                }
                sb.append(Integer.toHexString(v));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    //	public static void main(String[] args) throws Exception {
    //		String sKey1 = "9C51959C55E2DD5C";
    //		// 需要加密的字串
    //		String cSrc = "hello world中文";
    //		// 加密
    //		long lStart = System.currentTimeMillis();
    //		String enString = encrypt(cSrc, sKey1);
    //		System.out.println("加密后的字串是：" + enString);
    //
    //		long lUseTime = System.currentTimeMillis() - lStart;
    //		System.out.println("加密耗时：" + lUseTime + "毫秒");
    //		// 解密
    //		lStart = System.currentTimeMillis();
    //		String DeString = decrypt(enString, sKey1);
    //		System.out.println("解密后的字串是：" + DeString);
    //		lUseTime = System.currentTimeMillis() - lStart;
    //		System.out.println("解密耗时：" + lUseTime + "毫秒");
    //		System.out.println(encodeMD5("123456"));
    //	}

}