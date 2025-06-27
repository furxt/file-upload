package com.liyi.fileuploadstarter.service;

/**
 * @author liyi
 * @date 2025-06-25
 */
public interface Callback {
    /**
     * 文件上传完成回调
     *
     * @param filePath 文件路径
     */
    void onComplete(String filePath);
}
