package com.yixi.yipan.service;

import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {

    @Autowired
    private GitHub gitHub;

    @Value("${github.username}")
    private String username;

    @Value("${github.repo}")
    private String repo;

    public String uploadImage(MultipartFile file) {
        try {
            validateFile(file);

            GHRepository repository = gitHub.getRepository(username + "/" + repo);
            String fileName = generateUniqueFileName(file);
            String path = "images/" + fileName;

            // 直接上传文件内容，不进行base64编码
            repository.createContent()
                    .path(path)
                    .content(file.getBytes())  // 直接使用文件的字节数组
                    .message("Upload image: " + fileName)
                    .branch("main")
                    .commit();

            // 返回GitHub原始文件URL
            return String.format("https://raw.githubusercontent.com/%s/%s/main/%s",
                    username, repo, path);

        } catch (Exception e) {
            log.error("图片上传失败", e);
            throw new RuntimeException("上传失败: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("只能上传图片文件");
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("图片大小不能超过2MB");
        }
    }

    private String generateUniqueFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }
}