package com.liyi.fileuploadstarter.service;

/**
 * @author liyi
 * @date 2025-06-25
 */
public interface FileUploadCompletedCallback {
    /**
     * 文件上传完成回调
     *
     * @param filePath 文件绝对路径
     * @param fileMD5 文件md5值
     */
    void onComplete(String filePath, String fileMD5);
}
