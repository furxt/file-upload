package com.liyi.fileuploadstarter.util;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

/**
 * @author 11051
 */
public class RsaUtils {

    private static final String DEFAULT_ALGORITHM = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";

    /**
     * 生成 RSA 密钥对
     *
     * @param keySize 密钥长度（如 2048）
     * @return KeyPair 包含公钥和私钥
     * @throws NoSuchAlgorithmException 如果 RSA 算法不可用
     */
    public static KeyPair generateKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        // 通常使用 2048 位
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 将公钥转换为 Base64 字符串（便于存储或传输）
     *
     * @param publicKey 公钥
     * @return Base64 编码的公钥字符串
     */
    public static String publicKeyToBase64(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * 将私钥转换为 Base64 字符串（便于存储或传输）
     *
     * @param privateKey 私钥
     * @return Base64 编码的私钥字符串
     */
    public static String privateKeyToBase64(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * 从 Base64 字符串恢复公钥
     *
     * @param publicKeyBase64 Base64 编码的公钥字符串
     * @return PublicKey 对象
     * @throws Exception 如果解析失败
     */
    public static PublicKey getPublicKeyFromBase64(String publicKeyBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
        java.security.spec.X509EncodedKeySpec spec =
                new java.security.spec.X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    /**
     * 从 Base64 字符串恢复私钥
     *
     * @param privateKeyBase64 Base64 编码的私钥字符串
     * @return PrivateKey 对象
     * @throws Exception 如果解析失败
     */
    public static PrivateKey getPrivateKeyFromBase64(String privateKeyBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
        java.security.spec.PKCS8EncodedKeySpec spec =
                new java.security.spec.PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    public static PrivateKey loadPrivateKey(String pemKey) throws Exception {
        String content =
                pemKey.replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                        .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(content);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public static PublicKey loadPublicKey(String pemKey) throws Exception {
        String content =
                pemKey.replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(content);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public static void main(String[] args) throws Exception {
        // 1. 生成 RSA 密钥对（2048 位）
        KeyPair keyPair = generateKeyPair(2048);
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // 2. 将公钥和私钥转换为 Base64 字符串（便于存储或传输）
        String publicKeyBase64 = publicKeyToBase64(publicKey);
        String privateKeyBase64 = privateKeyToBase64(privateKey);

        System.out.println("公钥 (Base64):");
        System.out.println(publicKeyBase64);
        System.out.println("\n私钥 (Base64):");
        System.out.println(privateKeyBase64);

        // 3. 从 Base64 字符串恢复公钥和私钥（模拟存储/传输后重新加载）
        PublicKey restoredPublicKey = getPublicKeyFromBase64(publicKeyBase64);
        PrivateKey restoredPrivateKey = getPrivateKeyFromBase64(privateKeyBase64);

        // 4. 要加密的数据
        String originalData = "Hello, RSA OAEP 加密!";
        System.out.println("\n原始数据: " + originalData);

        // 5. 使用公钥加密数据（使用 OAEPWithSHA-1AndMGF1Padding）
        byte[] encryptedData = encryptWithPublicKey(originalData.getBytes(), restoredPublicKey);
        String encryptedDataBase64 = Base64.getEncoder().encodeToString(encryptedData);
        System.out.println("加密后的数据 (Base64): " + encryptedDataBase64);

        // 6. 使用私钥解密数据
        byte[] decryptedData = decryptWithPrivateKey(encryptedData, restoredPrivateKey);
        String decryptedString = new String(decryptedData);
        System.out.println("解密后的数据: " + decryptedString);
    }

    /**
     * 使用公钥加密数据（使用 OAEPWithSHA-1AndMGF1Padding）
     *
     * @param data 要加密的数据（字节数组）
     * @param publicKey 公钥
     * @return 加密后的字节数组
     * @throws Exception 如果加密失败
     */
    public static byte[] encryptWithPublicKey(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(DEFAULT_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * 使用私钥解密数据（使用 OAEPWithSHA-1AndMGF1Padding）
     *
     * @param encryptedData 加密后的字节数组
     * @param privateKey 私钥
     * @return 解密后的字节数组
     * @throws Exception 如果解密失败
     */
    public static byte[] decryptWithPrivateKey(byte[] encryptedData, PrivateKey privateKey)
            throws Exception {
        Cipher cipher = Cipher.getInstance(DEFAULT_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }
}
