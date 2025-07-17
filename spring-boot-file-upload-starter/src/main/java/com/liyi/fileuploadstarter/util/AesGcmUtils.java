package com.liyi.fileuploadstarter.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author liyi
 */
public class AesGcmUtils {
    /** GCM 默认 tag 长度为 128 bit */
    private static final int TAG_LENGTH_BIT = 128;

    private static final int AUTH_TAG_LENGTH = 16;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /** 解密从 Blob 传输过来的数据（格式：[密文] + [16字节 authTag]） */
    public static byte[] decrypt(byte[] encryptedWithAuthTag, byte[] keyBytes, byte[] ivBytes)
            throws Exception {
        if (encryptedWithAuthTag.length < AUTH_TAG_LENGTH) {
            throw new IllegalArgumentException("Encrypted data too short for GCM tag.");
        }

        // 1. 创建 SecretKey 和 GCM 参数
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, ivBytes);

        // 2. 初始化 Cipher（解密模式）
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

        // 3. 直接解密（无需手动拆分 cipherText 和 authTag）
        // Java 会自动从 encryptedWithAuthTag 末尾提取 authTag 并验证
        return cipher.doFinal(encryptedWithAuthTag);
    }
}
