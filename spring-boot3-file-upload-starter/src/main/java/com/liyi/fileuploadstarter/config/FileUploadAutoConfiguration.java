package com.liyi.fileuploadstarter.config;

import com.liyi.fileuploadstarter.controller.FileUploadController;
import com.liyi.fileuploadstarter.service.FileUploadCompletedCallback;
import com.liyi.fileuploadstarter.service.FileUploadService;
import com.liyi.fileuploadstarter.service.impl.FileUploadServiceImpl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author liyi
 * @date 2025-06-25
 */
@Import(FileUploadProperties.class)
@ConditionalOnProperty(name = "ly.file-upload.enabled", havingValue = "true")
public class FileUploadAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(FileUploadCompletedCallback.class)
    public FileUploadCompletedCallback fileUploadCompletedCallback() {
        return (filePath, fileMD5) -> System.err.println("成功之后我什么也不做");
    }

    @Bean
    @ConditionalOnBean(FileUploadCompletedCallback.class)
    @ConditionalOnMissingBean(FileUploadService.class)
    public FileUploadService fileUploadService(
            FileUploadProperties fileUploadProperties,
            FileUploadCompletedCallback fileUploadCompletedCallback) {
        return new FileUploadServiceImpl(
                fileUploadProperties.getLocalStoragePath(), fileUploadCompletedCallback);
    }

    @Bean
    @ConditionalOnBean(FileUploadService.class)
    public FileUploadController fileUploadController(FileUploadService fileUploadService) {
        return new FileUploadController(fileUploadService);
    }
}
