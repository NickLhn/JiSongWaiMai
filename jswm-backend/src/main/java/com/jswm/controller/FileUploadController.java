package com.jswm.controller;

import com.jswm.common.Result;
import com.jswm.utils.OssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/v1")
public class FileUploadController {

    @Autowired
    private OssUtil ossUtil;

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "directory", defaultValue = "images") String directory) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        // 检查OSS是否启用
        if (!ossUtil.isEnabled()) {
            return Result.error("文件存储服务未启用，请联系管理员配置OSS");
        }

        // 获取文件后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 检查文件类型
        String lowerSuffix = suffix.toLowerCase();
        if (!lowerSuffix.matches("\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
            return Result.error("不支持的文件类型，仅支持图片文件");
        }

        try {
            log.info("使用OSS上传文件");
            String fileUrl = ossUtil.uploadFile(file, directory);
            return Result.success("上传成功", fileUrl);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/upload")
    public Result<Void> deleteFile(@RequestParam("url") String fileUrl) {
        // 检查OSS是否启用
        if (!ossUtil.isEnabled()) {
            return Result.error("文件存储服务未启用");
        }

        try {
            ossUtil.deleteFile(fileUrl);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            log.error("文件删除失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
