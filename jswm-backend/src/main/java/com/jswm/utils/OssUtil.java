package com.jswm.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.jswm.config.OssConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class OssUtil {

    @Autowired
    private OssConfig ossConfig;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        if (ossConfig.isEnabled()) {
            ossClient = new OSSClientBuilder().build(
                ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret()
            );
            log.info("OSS客户端初始化成功");
        }
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
            log.info("OSS客户端已关闭");
        }
    }

    /**
     * 上传文件到OSS
     */
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        if (!ossConfig.isEnabled()) {
            throw new RuntimeException("OSS未启用");
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + suffix;
        String objectName = "JiSongWaiMai/" + directory + "/" + newFileName;

        // 上传文件
        PutObjectRequest putObjectRequest = new PutObjectRequest(
            ossConfig.getBucketName(),
            objectName,
            file.getInputStream()
        );
        ossClient.putObject(putObjectRequest);

        // 返回访问URL
        if (ossConfig.getCustomDomain() != null && !ossConfig.getCustomDomain().isEmpty()) {
            return ossConfig.getCustomDomain() + "/" + objectName;
        } else {
            return "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/" + objectName;
        }
    }

    /**
     * 删除OSS文件
     */
    public void deleteFile(String fileUrl) {
        if (!ossConfig.isEnabled()) {
            return;
        }

        // 从URL中提取objectName
        String objectName = extractObjectName(fileUrl);
        if (objectName != null) {
            ossClient.deleteObject(ossConfig.getBucketName(), objectName);
            log.info("删除OSS文件: {}", objectName);
        }
    }

    /**
     * 从URL中提取objectName
     */
    private String extractObjectName(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }

        // 处理自定义域名
        if (ossConfig.getCustomDomain() != null && !ossConfig.getCustomDomain().isEmpty()) {
            String domain = ossConfig.getCustomDomain();
            if (fileUrl.startsWith(domain)) {
                return fileUrl.substring(domain.length() + 1);
            }
        }

        // 处理OSS默认域名
        String ossDomain = "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint();
        if (fileUrl.startsWith(ossDomain)) {
            return fileUrl.substring(ossDomain.length() + 1);
        }

        return null;
    }

    /**
     * 检查OSS是否启用
     */
    public boolean isEnabled() {
        return ossConfig.isEnabled();
    }
}
