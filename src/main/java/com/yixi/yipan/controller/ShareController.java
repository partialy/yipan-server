package com.yixi.yipan.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixi.yipan.dto.ShareDTO;
import com.yixi.yipan.param.share.ShareCreateParam;
import com.yixi.yipan.param.share.ShareQueryParam;
import com.yixi.yipan.service.ShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件分享接口
 *
 * @author yixi
 */
@Tag(name = "文件分享", description = "文件分享相关接口")
@RestController
@RequestMapping("/api/share")
public class ShareController {
    
    @Resource
    private ShareService shareService;

    /**
     * 分页查询分享列表
     *
     * @param param 查询参数
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页查询分享列表")
    @GetMapping
    public ResponseEntity<IPage<ShareDTO>> list(
            @Valid ShareQueryParam param,
            @Parameter(description = "当前页码")
            @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "10") long size) {
        return ResponseEntity.ok(shareService.queryUserShares(param.getCreatorId(), new Page<>(current, size)));
    }

    /**
     * 获取分享详情
     *
     * @param id 分享ID
     * @return 分享详情
     */
    @Operation(summary = "获取分享详情")
    @GetMapping("/{id}")
    public ResponseEntity<ShareDTO> getById(
            @Parameter(description = "分享ID", required = true)
            @PathVariable String id) {
        return ResponseEntity.ok(shareService.getShareById(id));
    }

    /**
     * 通过分享码获取分享信息
     *
     * @param shareCode 分享码
     * @return 分享信息
     */
    @Operation(summary = "通过分享码获取分享信息")
    @GetMapping("/code/{shareCode}")
    public ResponseEntity<ShareDTO> getByCode(
            @Parameter(description = "分享码", required = true)
            @PathVariable String shareCode) {
        return ResponseEntity.ok(shareService.getShareByCode(shareCode));
    }

    /**
     * 创建分享
     *
     * @param param 创建参数
     * @return 创建的分享信息
     */
    @Operation(summary = "创建分享")
    @PostMapping
    public ResponseEntity<ShareDTO> create(@Valid @RequestBody ShareCreateParam param) {
        return ResponseEntity.ok(shareService.createShare(param.getUserId(),param.getFileId(), param.getExpireTime()));
    }

    /**
     * 取消分享
     *
     * @param id 分享ID
     * @return 操作结果
     */
    @Operation(summary = "取消分享")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(
            @Parameter(description = "分享ID", required = true)
            @PathVariable String id) {
        shareService.cancelShare(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量取消分享
     *
     * @param ids 分享ID列表
     * @return 操作结果
     */
    @Operation(summary = "批量取消分享")
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchCancel(
            @Parameter(description = "分享ID列表", required = true)
            @RequestBody List<String> ids) {
        shareService.batchCancelShares(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * 增加下载次数
     *
     * @param id 分享ID
     * @return 操作结果
     */
    @Operation(summary = "增加下载次数")
    @PostMapping("/{id}/downloads")
    public ResponseEntity<Void> incrementDownloads(
            @Parameter(description = "分享ID", required = true)
            @PathVariable String id) {
        shareService.incrementDownloadCount(id);
        return ResponseEntity.ok().build();
    }
}