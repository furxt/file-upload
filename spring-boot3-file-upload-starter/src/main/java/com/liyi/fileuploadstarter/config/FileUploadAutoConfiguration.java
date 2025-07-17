package com.liyi.fileuploadstarter.config;

import com.liyi.fileuploadstarter.controller.FileUploadController;
import com.liyi.fileuploadstarter.controller.KeyController;
import com.liyi.fileuploadstarter.service.FileUploadCacheService;
import com.liyi.fileuploadstarter.service.FileUploadCompletedCallback;
import com.liyi.fileuploadstarter.service.FileUploadService;
import com.liyi.fileuploadstarter.service.KeyService;
import com.liyi.fileuploadstarter.service.impl.DefaultKeyServiceImpl;
import com.liyi.fileuploadstarter.service.impl.FileUploadCacheServiceImpl;
import com.liyi.fileuploadstarter.service.impl.FileUploadServiceImpl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author liyi
 */
@Import(FileUploadProperties.class)
@ConditionalOnProperty(name = "ly.file-upload.enabled", havingValue = "true")
public class FileUploadAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(FileUploadCompletedCallback.class)
    public FileUploadCompletedCallback fileUploadCompletedCallback() {
        return (filePath, md5) -> {};
    }

    @Bean
    @ConditionalOnMissingBean(KeyService.class)
    public KeyService keyService(FileUploadProperties fileUploadProperties) {
        return new DefaultKeyServiceImpl(
                fileUploadProperties.getPublicKeyPath(), fileUploadProperties.getPrivateKeyPath());
    }

    @Bean
    @ConditionalOnMissingBean(FileUploadCacheService.class)
    public FileUploadCacheService fileUploadCacheService() {
        return new FileUploadCacheServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(FileUploadService.class)
    public FileUploadService fileUploadService(
            FileUploadProperties fileUploadProperties,
            FileUploadCompletedCallback fileUploadCompletedCallback,
            FileUploadCacheService fileUploadCacheService) {
        return new FileUploadServiceImpl(
                fileUploadProperties.getLocalStoragePath(),
                fileUploadCompletedCallback,
                fileUploadCacheService);
    }

    @Bean
    public FileUploadController fileUploadController(FileUploadService fileUploadService) {
        return new FileUploadController(fileUploadService);
    }

    @Bean
    public KeyController keyController(
            KeyService keyService, FileUploadCacheService fileUploadCacheService) {
        return new KeyController(keyService, fileUploadCacheService);
    }
}
