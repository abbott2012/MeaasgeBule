package com.bql.utils.cipher;

/**
 * 解密接口
 * Created by Cyarie on 2016/1/20.
 */
public interface Decrypt {

    /**
     * 解密
     *
     * @param res
     * @return
     */
    public byte[] decrypt(byte[] res);
}
