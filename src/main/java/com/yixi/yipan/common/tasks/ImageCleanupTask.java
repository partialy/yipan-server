package com.yixi.yipan.common.tasks;

import com.yixi.yipan.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ImageCleanupTask {

    @Autowired
    private ImageService imageService;

    /**
     * 每天凌晨2点执行清理
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldImages() {
        log.info("开始清理过期头像");
        try {
            // 清理30天前的旧头像
            imageService.cleanupOldImages(30);
            log.info("清理完成");
        } catch (Exception e) {
            log.error("清理失败", e);
        }
    }
}
