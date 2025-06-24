package com.liyi.fileupload.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.SecureUtil;
import com.liyi.fileupload.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author liyi
 * @date 2025-06-22
 */
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final String localStorageDir = "D:/temp";

    @Override
    public void saveChunk(MultipartFile file, String fileId, String chunkMD5, Integer chunkIndex, Integer totalChunks) throws IOException {
        byte[] bytes = file.getBytes();
        String md5;
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            // 先将整个流读入内存
            md5 = SecureUtil.md5(is);
        }
        if (chunkIndex >= totalChunks) {
            String errMsg = String.format("非法的分片序号: %d, 总分片数: %d", chunkIndex, totalChunks);
            throw new RuntimeException(errMsg);
        }
        if (!CharSequenceUtil.equals(md5, chunkMD5)) {
            throw new RuntimeException("非法分片文件, md5值不一致!");
        }
        FileUtil.writeBytes(bytes, Paths.get(localStorageDir, fileId, chunkIndex + "").toAbsolutePath().toFile());
    }

    @Override
    public void mergeChunks(String fileId, Integer totalChunks) {
        boolean flag = false;
        String fileDirPath = Paths.get(localStorageDir, fileId).toFile().getAbsolutePath();
        File tempFile = FileUtil.createTempFile();
        File destFile = Paths.get(fileDirPath, fileId).toAbsolutePath().toFile();
        List<String> filenames = FileUtil.listFileNames(fileDirPath);
        try {
            if (filenames.size() != totalChunks) {
                String errMsg = String.format("总分片数对不上, 当前需要 %d 个分片, 但目前 %s 文件夹下有 %d 个分片", totalChunks, fileDirPath,
                        filenames.size());
                throw new RuntimeException(errMsg);
            }
            if (CollUtil.isNotEmpty(filenames)) {
                // 按照自己的逻辑对分片进行排序，例如按照数字顺序或创建时间等
                filenames.sort((a, b) -> Integer.valueOf(a).compareTo(Integer.parseInt(b)));

                // 创建目标文件输出流
                for (String filename : filenames) {
                    File file = Paths.get(fileDirPath, filename).toFile();
                    // 将每个分片追加到目标文件末尾
                    byte[] bytes = FileUtil.readBytes(file);
                    FileUtil.writeBytes(bytes, tempFile, 0, bytes.length, true);
                }
                flag = true;
            }
        } finally {
            FileUtil.del(fileDirPath);
            if (flag) {
                FileUtil.moveContent(tempFile, destFile, true);
            } else {
                FileUtil.del(tempFile);
            }
        }
    }

    @Override
    public void check(String fileId, String fileMD5) {
        File file = Paths.get(localStorageDir, fileId, fileId).toFile();
        String md5 = SecureUtil.md5(file);
        if (!CharSequenceUtil.equals(md5, fileMD5)) {
            throw new RuntimeException("非法文件, md5值不一致!");
        }
    }
}
