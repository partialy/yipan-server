package com.yixi.yipan.controller;

import com.yixi.yipan.common.result.ResultMsg;
import com.yixi.yipan.service.ImageService;
import com.yixi.yipan.service.UserAvatarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@Slf4j
public class UploadController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserAvatarService userAvatarService;

    @Value("${github.token}")
    private String token;

    @Value("${github.username}")
    private String username;

    @PostMapping("/uploadAvatar")
    public ResultMsg<String> uploadToGithub(
            @RequestParam("userId") String userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("oldAvatarUrl") String oldAvatarUrl) {
        try {
            if(oldAvatarUrl.startsWith("https://raw.githubusercontent.com/")){
                imageService.deleteImage(oldAvatarUrl);
            }
            String imageUrl = imageService.uploadImage(file);
            userAvatarService.setAvatar(userId, imageUrl);
            return ResultMsg.success(imageUrl);
        } catch (IllegalArgumentException e) {
            return new ResultMsg<>(400, "上传失败",e.getMessage());
        } catch (Exception e) {
            log.error("上传失败", e);
            return ResultMsg.error("上传失败");
        }
    }

    @GetMapping("/github-config")
    public String testConfig() {
        return String.format("Username: %s, Token: %s...",
                username,
                token.substring(0, 10) + "***");
    }
}
