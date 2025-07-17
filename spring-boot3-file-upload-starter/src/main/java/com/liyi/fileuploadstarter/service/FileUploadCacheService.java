package com.liyi.fileuploadstarter.service;

import com.liyi.fileuploadstarter.entity.AesGcm;

/**
 * @author 11051
 */
public interface FileUploadCacheService {
    /**
     * 缓存AesGcm{key,iv}
     *
     * @param key 键值
     * @param value AesGcm
     */
    void put(String key, AesGcm value);

    /**
     * 获取AesGcm{key,iv}
     *
     * @param key 键值
     * @return AesGcm
     */
    AesGcm get(String key);
}
