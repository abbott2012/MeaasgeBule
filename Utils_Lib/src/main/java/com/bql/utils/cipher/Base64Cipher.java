package com.bql.utils.cipher;


import com.bql.utils.Base64;

/**
 * 基于Base64的加密
 * Created by Cyarie on 2016/1/20.
 */
public class Base64Cipher extends Cipher {

    private Cipher cipher;

    public Base64Cipher() {
    }

    public Base64Cipher(Cipher cipher) {
        this.cipher = cipher;
    }

    @Override
    public byte[] decrypt(byte[] res) {
        if (cipher != null)
            res = cipher.decrypt(res);
        return Base64.decode(res, Base64.DEFAULT);
    }

    @Override
    public byte[] encrypt(byte[] res) {
        if (cipher != null)
            res = cipher.encrypt(res);
        return Base64.encode(res, Base64.DEFAULT);
    }
}
