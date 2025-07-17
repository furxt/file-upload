package com.liyi.fileuploadstarter.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.liyi.fileuploadstarter.entity.AesGcm;
import com.liyi.fileuploadstarter.service.FileUploadCacheService;

import java.util.concurrent.TimeUnit;

/**
 * @author liyi
 */
public class FileUploadCacheServiceImpl implements FileUploadCacheService {
    /** 创建一个 Caffeine 缓存实例 最大容量为 100 写入后 30 分钟过期 */
    private static final Cache<String, AesGcm> CACHE =
            Caffeine.newBuilder()
                    // 设置最大容量
                    .maximumSize(100)
                    // 设置写入后过期时间
                    .expireAfterWrite(15, TimeUnit.MINUTES)
                    .build();

    @Override
    public void put(String key, AesGcm value) {
        CACHE.put(key, value);
    }

    @Override
    public AesGcm get(String key) {
        return CACHE.getIfPresent(key);
    }
}
