package com.liyi.fileuploadstarter.service;

/**
 * @author liyi
 */
public interface FileUploadCompletedCallback {
    /**
     * 文件上传完成回调
     *
     * @param filePath 文件绝对路径
     * @param md5 文件md5
     */
    void onComplete(String filePath, String md5);
}
