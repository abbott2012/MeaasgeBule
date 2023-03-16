package com.bql.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.bql.convenientlog.CLog;
import com.bql.utils.cipher.Cipher;


/**
 * 数据存储工具
 * Created by Cyarie on 2016/1/4.
 */
public class DataKeeperUtils {


    private static final String TAG = DataKeeperUtils.class.getSimpleName();

    private SharedPreferences sp;

    public DataKeeperUtils(Context context, String fileName) {
        sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /**
     * *************** get ******************
     */


    public String get(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public boolean get(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public float get(String key, float defValue) {
        return sp.getFloat(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public long get(String key, long defValue) {
        return sp.getLong(key, defValue);
    }

    public Object get(String key) {
        return get(key, (Cipher) null);
    }

    /**
     * 解密密文
     *
     * @param key    键
     * @param cipher 加密器
     * @return
     */
    public Object get(String key, Cipher cipher) {
        try {
            String hex = get(key, (String) null);
            if (hex == null)
                return null;
            byte[] bytes = HexUtils.decodeHex(hex.toCharArray());
            if (cipher != null)
                bytes = cipher.decrypt(bytes);
            Object obj = ByteUtils.byteToObject(bytes);
            CLog.i(TAG, key + " get: " + obj);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密加密过的密文
     *
     * @param value  密文
     * @param cipher 解密器
     * @return
     */
    public Object getStr(String value, Cipher cipher) {
        try {
            String hex = value;
            if (hex == null)
                return null;
            byte[] bytes = HexUtils.decodeHex(hex.toCharArray());
            if (cipher != null)
                bytes = cipher.decrypt(bytes);
            Object obj = ByteUtils.byteToObject(bytes);
            CLog.i(TAG, value + " get: " + obj);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * *************** put ******************
     */
    public void put(String key, Object ser) {
        put(key, ser, null);
    }

    /**
     * 加密
     *
     * @param key
     * @param ser
     * @param cipher
     */
    public void put(String key, Object ser, Cipher cipher) {
        try {
            CLog.i(TAG, key + " put: " + ser);
            if (ser == null) {
                sp.edit().remove(key).apply();
            } else {
                byte[] bytes = ByteUtils.objectToByte(ser);
                if (cipher != null)
                    bytes = cipher.encrypt(bytes);
                put(key, HexUtils.encodeHexStr(bytes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void put(String key, String value) {
        if (value == null) {
            sp.edit().remove(key).apply();
        } else {
            sp.edit().putString(key, value).apply();
        }
    }

    public void put(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public void put(String key, float value) {
        sp.edit().putFloat(key, value).apply();
    }

    public void put(String key, long value) {
        sp.edit().putLong(key, value).apply();
    }

    public void putInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }


    /**
     * 移除指定key的值
     *
     * @param key
     */
    public void remove(String key) {
        sp.edit().remove(key).apply();

    }

    /**
     * 清除所有
     */
    public void clear() {
        sp.edit().clear().apply();
    }

}
