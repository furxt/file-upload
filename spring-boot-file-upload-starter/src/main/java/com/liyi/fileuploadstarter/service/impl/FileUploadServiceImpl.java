package com.liyi.fileuploadstarter.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.SecureUtil;

import com.liyi.fileuploadstarter.entity.AesGcm;
import com.liyi.fileuploadstarter.service.FileUploadCacheService;
import com.liyi.fileuploadstarter.service.FileUploadCompletedCallback;
import com.liyi.fileuploadstarter.service.FileUploadService;
import com.liyi.fileuploadstarter.util.AesGcmUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author liyi
 */
public class FileUploadServiceImpl implements FileUploadService {
    private static final Logger log = LoggerFactory.getLogger(FileUploadServiceImpl.class);
    private final String localStoragePath;
    private final FileUploadCompletedCallback callback;
    private final FileUploadCacheService fileUploadCacheService;

    public FileUploadServiceImpl(
            String localStoragePath,
            FileUploadCompletedCallback callback,
            FileUploadCacheService fileUploadCacheService) {
        this.localStoragePath = localStoragePath;
        this.callback = callback;
        this.fileUploadCacheService = fileUploadCacheService;
    }

    @Override
    public void saveChunk(
            MultipartFile file,
            String fileId,
            String chunkMD5,
            Integer chunkIndex,
            Integer totalChunks)
            throws Exception {
        byte[] bytes = file.getBytes();
        // 1.获取AES密钥和IV
        AesGcm aesGcm = fileUploadCacheService.get(fileId);
        byte[] aesKeyBytes = aesGcm.getAesKeyBytes();
        byte[] ivBytes = aesGcm.getIvBytes();

        if (aesKeyBytes == null || ivBytes == null) {
            throw new IllegalArgumentException("AES key or IV not found for fileId: " + fileId);
        }

        // 2.解密
        byte[] decryptedBytes = AesGcmUtils.decrypt(bytes, aesKeyBytes, ivBytes);

        // 3.md5校验
        String md5;
        try (InputStream is = new ByteArrayInputStream(decryptedBytes)) {
            md5 = SecureUtil.md5(is);
        }

        // 4.合规性校验
        if (chunkIndex >= totalChunks) {
            String errMsg = String.format("非法的分片序号: %d, 总分片数: %d", chunkIndex, totalChunks);
            throw new RuntimeException(errMsg);
        }
        if (!CharSequenceUtil.equals(md5, chunkMD5)) {
            throw new RuntimeException("非法分片文件, md5值不一致!");
        }

        // 5.保存分片文件
        FileUtil.writeBytes(
                decryptedBytes,
                Paths.get(localStoragePath, fileId, chunkIndex + "").toAbsolutePath().toFile());
    }

    @Override
    public void mergeChunks(String fileId, Integer totalChunks) {
        boolean flag = false;
        String fileDirPath =
                Paths.get(localStoragePath, fileId).toAbsolutePath().toFile().getAbsolutePath();
        File tempFile = FileUtil.createTempFile();
        File destFile = Paths.get(fileDirPath, fileId).toAbsolutePath().toFile();
        List<String> filenames = FileUtil.listFileNames(fileDirPath);
        try {
            if (filenames.size() != totalChunks) {
                String errMsg =
                        String.format(
                                "总分片数对不上, 当前需要 %d 个分片, 但目前 %s 文件夹下有 %d 个分片",
                                totalChunks, fileDirPath, filenames.size());
                throw new RuntimeException(errMsg);
            }
            if (CollUtil.isNotEmpty(filenames)) {
                // 按照自己的逻辑对分片进行排序，例如按照数字顺序或创建时间等
                filenames.sort((a, b) -> Integer.valueOf(a).compareTo(Integer.parseInt(b)));

                // 创建目标文件输出流
                for (String filename : filenames) {
                    File file = Paths.get(fileDirPath, filename).toAbsolutePath().toFile();
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
                log.info("文件保存于: {}", destFile.getAbsolutePath());
            } else {
                FileUtil.del(tempFile);
            }
        }
    }

    @Override
    public void check(String fileId, String fileMD5) {
        File file = Paths.get(localStoragePath, fileId, fileId).toAbsolutePath().toFile();
        String md5 = SecureUtil.md5(file);
        if (!CharSequenceUtil.equals(md5, fileMD5)) {
            throw new RuntimeException("非法文件, md5值不一致!");
        }
        callback.onComplete(file.getAbsolutePath(), md5);
    }
}
