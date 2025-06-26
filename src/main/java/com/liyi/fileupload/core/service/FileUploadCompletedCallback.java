package com.liyi.fileupload.core.service;

/**
 * @author liyi
 * @date 2025-06-25
 */
public interface FileUploadCompletedCallback {
    /**
     * 文件上传完成回调
     *
     * @param filePath 文件绝对路径
     * @param fileMD5
     */
    void onComplete(String filePath, String fileMD5);
}
