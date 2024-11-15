package com.yixi.yipan.service;

import com.yixi.yipan.mapper.UserAvatarMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
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

    @Resource
    private UserAvatarMapper userAvatarMapper;

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

    /**
     * 删除GitHub仓库中的图片
     * @param imageUrl 图片URL
     */
    public void deleteImage(String imageUrl) {
        try {
            // 从URL中提取文件路径
            String path = extractPathFromUrl(imageUrl);

            GHRepository repository = gitHub.getRepository(username + "/" + repo);

            // 获取文件信息
            GHContent content = repository.getFileContent(path);

            // 删除文件
            content.delete("Delete unused image: " + path);

            log.info("成功删除图片: {}", path);
        } catch (Exception e) {
            log.error("删除图片失败: {}", imageUrl, e);
            throw new RuntimeException("删除失败: " + e.getMessage());
        }
    }

    /**
     * 从URL中提取文件路径
     */
    private String extractPathFromUrl(String imageUrl) {
        String prefix = "https://raw.githubusercontent.com/" + username + "/" + repo + "/main/";
        return imageUrl.substring(prefix.length());
    }

    /**
     * 清理过期的头像
     * @param retainDays 保留天数
     */
    public void cleanupOldImages(int retainDays) {
        try {
            GHRepository repository = gitHub.getRepository(username + "/" + repo);
            String imagesPath = "images";

            // 获取images目录下的所有文件
            List<GHContent> contents = repository.getDirectoryContent(imagesPath);

            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retainDays);

            for (GHContent content : contents) {
                // 获取文件最后修改时间
                GHCommit lastCommit = repository.getCommit(content.getSha());
                LocalDateTime lastModified = lastCommit.getCommitDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                // 如果文件超过保留天数且不是当前使用的头像，则删除
                if (lastModified.isBefore(cutoffDate) && !isCurrentAvatar(content.getDownloadUrl())) {
                    content.delete("Delete old image: " + content.getName());
                    log.info("删除过期图片: {}", content.getName());
                }
            }
        } catch (Exception e) {
            log.error("清理过期图片失败", e);
            throw new RuntimeException("清理失败: " + e.getMessage());
        }
    }

    /**
     * 检查是否为当前在使用的头像
     */
    private boolean isCurrentAvatar(String imageUrl) {
        return userAvatarMapper.existsByAvatarUrl(imageUrl);
    }
}