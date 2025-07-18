package com.liyi.fileuploadstarter.service;

/**
 * @author liyi
 */
public interface KeyService {
    /**
     * 获取公钥
     *
     * @return PEM格式公钥
     */
    String getPemPublicKey();

    /**
     * 解密
     *
     * @param data 密文
     * @return 明文
     */
    byte[] decrypt(byte[] data);
}
