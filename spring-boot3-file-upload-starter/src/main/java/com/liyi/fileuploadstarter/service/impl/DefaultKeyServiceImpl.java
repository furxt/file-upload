package com.liyi.fileuploadstarter.service.impl;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

import com.liyi.fileuploadstarter.service.KeyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liyi
 */
public class DefaultKeyServiceImpl implements KeyService {
    private static final Logger log = LoggerFactory.getLogger(DefaultKeyServiceImpl.class);

    private final String pemPublicKey;
    private final RSA rsa;

    public DefaultKeyServiceImpl(String publicKeyPath, String privateKeyPath) {
        this.pemPublicKey = ResourceUtil.readUtf8Str(publicKeyPath);
        String pemPrivateKey = ResourceUtil.readUtf8Str(privateKeyPath);
        rsa =
                new RSA(
                        "RSA/ECB/OAEPWithSHA-1AndMGF1Padding",
                        pemToBase64(pemPrivateKey),
                        pemToBase64(this.pemPublicKey));
    }

    String pemToBase64(String pemKey) {
        return pemKey.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
    }

    @Override
    public String getPemPublicKey() {
        return pemPublicKey;
    }

    @Override
    public byte[] decrypt(byte[] bytes) {
        return rsa.decrypt(bytes, KeyType.PrivateKey);
    }
}
