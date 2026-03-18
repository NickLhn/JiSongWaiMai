package com.jswm.controller;

import com.jswm.common.Result;
import com.jswm.exception.BusinessException;
import com.jswm.utils.OssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1")
public class FileUploadController {

    @Autowired
    private OssUtil ossUtil;

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    @Value("${upload.url-prefix:/uploads}")
    private String urlPrefix;

    @PostConstruct
    public void init() {
        // 创建上传目录
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "directory", defaultValue = "images") String directory) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }
        directory = sanitizeDirectory(directory);

        // 获取文件后缀
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            return Result.error("文件名不合法");
        }
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 检查文件类型
        String lowerSuffix = suffix.toLowerCase();
        if (!lowerSuffix.matches("\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
            return Result.error("不支持的文件类型，仅支持图片文件");
        }

        try {
            // 如果OSS启用，使用OSS上传
            if (ossUtil.isEnabled()) {
                log.info("使用OSS上传文件");
                String fileUrl = ossUtil.uploadFile(file, directory);
                return Result.success("上传成功", fileUrl);
            } else {
                // 使用本地存储
                log.info("使用本地存储上传文件");
                String fileUrl = uploadToLocal(file, directory);
                return Result.success("上传成功", fileUrl);
            }
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传到本地存储
     */
    private String uploadToLocal(MultipartFile file, String directory) throws IOException {
        // 创建目录
        Path basePath = Paths.get(uploadPath).toAbsolutePath().normalize();
        Path dirPath = basePath.resolve(directory).normalize();
        if (!dirPath.startsWith(basePath)) {
            throw new IOException("非法上传目录");
        }
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + suffix;

        // 保存文件
        Path filePath = dirPath.resolve(newFileName);
        Files.copy(file.getInputStream(), filePath);

        // 返回访问URL
        return urlPrefix + "/" + directory + "/" + newFileName;
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/upload")
    public Result<Void> deleteFile(@RequestParam("url") String fileUrl) {
        try {
            // 如果是OSS文件
            if (ossUtil.isEnabled() && fileUrl.contains(ossUtil.getBucketName())) {
                ossUtil.deleteFile(fileUrl);
                return Result.success("删除成功", null);
            }

            // 本地文件删除
            if (fileUrl.startsWith(urlPrefix)) {
                String relativePath = fileUrl.substring(urlPrefix.length());
                if (relativePath.startsWith("/")) {
                    relativePath = relativePath.substring(1);
                }
                Path basePath = Paths.get(uploadPath).toAbsolutePath().normalize();
                Path filePath = basePath.resolve(relativePath).normalize();
                if (!filePath.startsWith(basePath)) {
                    return Result.error("非法文件路径");
                }
                Files.deleteIfExists(filePath);
                return Result.success("删除成功", null);
            }

            return Result.error("不支持的文件路径");
        } catch (Exception e) {
            log.error("文件删除失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    private String sanitizeDirectory(String directory) {
        if (directory == null || directory.trim().isEmpty()) {
            return "images";
        }
        String normalized = directory.trim();
        if (!normalized.matches("[a-zA-Z0-9/_-]+")) {
            throw new BusinessException(400, "非法目录名称");
        }
        return normalized;
    }
}
