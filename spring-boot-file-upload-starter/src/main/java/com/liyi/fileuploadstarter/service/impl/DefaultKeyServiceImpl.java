package com.liyi.fileuploadstarter.service.impl;

import cn.hutool.core.io.resource.ResourceUtil;

import com.liyi.fileuploadstarter.service.KeyService;
import com.liyi.fileuploadstarter.util.RsaUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;

/**
 * @author liyi
 */
public class DefaultKeyServiceImpl implements KeyService {
    private static final Logger log = LoggerFactory.getLogger(DefaultKeyServiceImpl.class);

    private final String pemPublicKey;
    private final PrivateKey privateKey;

    public DefaultKeyServiceImpl(String publicKeyPath, String privateKeyPath) {
        this.pemPublicKey = ResourceUtil.readUtf8Str(publicKeyPath);
        String pemPrivateKey = ResourceUtil.readUtf8Str(privateKeyPath);
        try {
            this.privateKey = RsaUtils.loadPrivateKey(pemPrivateKey);
        } catch (Exception e) {
            log.error("RSA 密钥加载失败", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPemPublicKey() {
        return pemPublicKey;
    }

    @Override
    public byte[] decrypt(byte[] bytes) throws Exception {
        return RsaUtils.decryptWithPrivateKey(bytes, privateKey);
    }
}
