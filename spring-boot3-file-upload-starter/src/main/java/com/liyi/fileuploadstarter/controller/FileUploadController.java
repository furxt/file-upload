package com.liyi.fileuploadstarter.controller;

import com.liyi.fileuploadstarter.service.FileUploadService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liyi
 * @date 2025-06-14
 */
@RestController
@RequestMapping("${ly.file-upload.url-prefix:ly}/upload")
public class FileUploadController {
    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/chunk/{fileId}")
    public Map<String, Object> upload(
            @RequestPart("file") MultipartFile file,
            @PathVariable String fileId,
            String chunkMD5,
            Integer chunkIndex,
            Integer totalChunks)
            throws IOException {
        fileUploadService.saveChunk(file, fileId, chunkMD5, chunkIndex, totalChunks);
        Map<String, Object> result = new HashMap<>(2);
        result.put("code", 1);
        result.put("msg", "分片上传成功");
        return result;
    }

    @GetMapping("/merge/{fileId}")
    public Map<String, Object> merge(@PathVariable String fileId, Integer totalChunks)
            throws IOException {
        fileUploadService.mergeChunks(fileId, totalChunks);
        Map<String, Object> result = new HashMap<>(2);
        result.put("code", 1);
        result.put("msg", "所有分片合成成功");
        return result;
    }

    @GetMapping("/check/{fileId}")
    public Map<String, Object> check(@PathVariable String fileId, String fileMD5)
            throws IOException {
        fileUploadService.check(fileId, fileMD5);
        Map<String, Object> result = new HashMap<>(2);
        result.put("code", 1);
        result.put("msg", "文件检查通过");
        return result;
    }

    @ExceptionHandler(RuntimeException.class)
    public Map<String, Object> handle(RuntimeException e) {
        log.error("分片上传流程出现异常:\n", e);
        Map<String, Object> result = new HashMap<>(2);
        result.put("code", 0);
        result.put("msg", e.getMessage());
        return result;
    }
}
