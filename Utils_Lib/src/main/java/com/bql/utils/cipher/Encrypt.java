package com.bql.utils.cipher;

/**
 * 加密接口
 * Created by Cyarie on 2016/1/20.
 */
public interface Encrypt {

    /**
     * 加密
     * @param res
     * @return
     */
    public byte[] encrypt(byte[] res);
}
