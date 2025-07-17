package com.liyi.fileuploadstarter.controller;

import com.liyi.fileuploadstarter.entity.AesGcm;
import com.liyi.fileuploadstarter.service.FileUploadCacheService;
import com.liyi.fileuploadstarter.service.KeyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liyi
 */
@RestController
@RequestMapping("${ly.file-upload.url-prefix:ly}/key")
public class KeyController {
    private static final Logger log = LoggerFactory.getLogger(KeyController.class);

    private final KeyService keyService;

    private final FileUploadCacheService fileUploadCacheService;

    public KeyController(KeyService keyService, FileUploadCacheService fileUploadCacheService) {
        this.keyService = keyService;
        this.fileUploadCacheService = fileUploadCacheService;
    }

    @GetMapping("publicKey")
    public Map<String, Object> getPublicKey() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "获取密钥成功");
        result.put("data", keyService.getPemPublicKey());
        return result;
    }

    @PostMapping("store")
    public Map<String, Object> storeAesKey(@RequestBody ReqStoreKey req) throws Exception {
        // 1. 解码 Base64
        byte[] encryptedAesKeyBytes = Base64.getDecoder().decode(req.getEncryptedAesKey());
        byte[] ivBytes = Base64.getDecoder().decode(req.getIv());

        byte[] aesKeyBytes = keyService.decrypt(encryptedAesKeyBytes);
        // 3. 存储 AES 密钥和 IV（示例：用 Map 模拟，实际应存数据库）
        fileUploadCacheService.put(req.getFileId(), new AesGcm(aesKeyBytes, ivBytes));

        Map<String, Object> response = new HashMap<>();
        response.put("code", 1);
        response.put("msg", "AES key stored successfully");
        return response;
    }

    @ExceptionHandler(RuntimeException.class)
    public Map<String, Object> handle(RuntimeException e) {
        log.error("密钥流程异常:\n", e);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", e.getMessage());
        return result;
    }

    public static class ReqStoreKey {
        private String fileId;
        private String encryptedAesKey; // Base64 编码
        private String iv; // Base64 编码

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        public String getEncryptedAesKey() {
            return encryptedAesKey;
        }

        public void setEncryptedAesKey(String encryptedAesKey) {
            this.encryptedAesKey = encryptedAesKey;
        }

        public String getIv() {
            return iv;
        }

        public void setIv(String iv) {
            this.iv = iv;
        }
    }
}
