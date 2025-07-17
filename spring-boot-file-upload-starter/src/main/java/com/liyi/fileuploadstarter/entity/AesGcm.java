package com.liyi.fileuploadstarter.entity;

/**
 * @author liyi
 * @date 2025-07-10
 */
public class AesGcm {
    /** AES密钥 */
    private final byte[] aesKeyBytes;

    /** IV向量 */
    private final byte[] ivBytes;

    public AesGcm(byte[] aesKeyBytes, byte[] ivBytes) {
        this.aesKeyBytes = aesKeyBytes;
        this.ivBytes = ivBytes;
    }

    public byte[] getAesKeyBytes() {
        return aesKeyBytes;
    }

    public byte[] getIvBytes() {
        return ivBytes;
    }
}
