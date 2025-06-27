package com.liyi.fileuploadstarter.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author liyi
 * @date 2025-06-22
 */
public interface FileUploadService {
    /**
     * 保存分片
     *
     * @param file 文件分片
     * @param fileId 文件临时id
     * @param chunkMD5 分片MD5
     * @param chunkIndex 当前分片下标
     * @param totalChunks 分片总数
     * @throws IOException IO异常
     */
    void saveChunk(
            MultipartFile file,
            String fileId,
            String chunkMD5,
            Integer chunkIndex,
            Integer totalChunks)
            throws IOException;

    /**
     * 合并分片
     *
     * @param fileId 文件临时id
     * @param totalChunks 分片总数
     * @throws IOException IO异常
     */
    void mergeChunks(String fileId, Integer totalChunks) throws IOException;

    /**
     * 校验文件
     *
     * @param fileId 文件临时id
     * @param fileMD5 文件MD5
     * @throws IOException IO异常
     */
    void check(String fileId, String fileMD5) throws IOException;
}
