package com.liyi.fileupload.controller;


import com.liyi.fileupload.Constant;
import com.liyi.fileupload.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liyi
 * @date 2025-06-14
 */
@Slf4j
@RestController
@RequestMapping(Constant.UPLOAD_PREFIX + "/upload")
public class FileUploadController {
    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/chunk/{fileId}")
    public Map<String, Object> upload(@RequestPart("file") MultipartFile file,
                                      @PathVariable String fileId,
                                      String chunkMD5,
                                      Integer chunkIndex, Integer totalChunks) throws IOException {
        log.info("fileId: {}, chunkIndex:{}, chunkMD5:{}, totalChunks:{}", fileId, chunkIndex, chunkMD5, totalChunks);
        fileUploadService.saveChunk(file, fileId, chunkMD5, chunkIndex, totalChunks);
        Map<String, Object> result = new HashMap<>(2);
        result.put("code", 1);
        result.put("msg", "分片上传成功");
        return result;
    }

    @GetMapping("/merge/{fileId}")
    public Map<String, Object> merge(@PathVariable String fileId, Integer totalChunks) throws IOException {
        log.info("fileId: {}, totalChunks: {}", fileId, totalChunks);
        fileUploadService.mergeChunks(fileId, totalChunks);
        Map<String, Object> result = new HashMap<>(2);
        result.put("code", 1);
        result.put("msg", "所有分片合成成功");
        return result;
    }

    @GetMapping("/check/{fileId}")
    public Map<String, Object> check(@PathVariable String fileId,
                                     String fileMD5) throws IOException {
        log.info("fileId: {}, fileMD5: {}", fileId, fileMD5);
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
