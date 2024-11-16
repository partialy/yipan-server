package com.yixi.yipan.controller;

import com.yixi.yipan.common.result.ResultMsg;
import com.yixi.yipan.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Resource
    private ImageService imageService;

    @Operation(summary = "删除不需要的用户头像")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ApiResponse(responseCode = "404", description = "头像不存在")
    @DeleteMapping("/deleteAvatar")
    public ResultMsg<Integer> deleteAvatar(
            @RequestParam("userId") String userId
    ) {
        return ResultMsg.success(imageService.cleanupUnusedImages(userId));
    }
}
