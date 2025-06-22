package com.liyi.fileupload.service;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author liyi
 * @date 2025-06-22
 */
public interface FileUploadService {
    void saveChunk(MultipartFile file,
                   String fileId,
                   String chunkMD5,
                   Integer chunkIndex, Integer totalChunks) throws IOException;

    void mergeChunks(String fileId, Integer totalChunks) throws IOException;

    void check(String fileId, String fileMD5) throws IOException;
}
