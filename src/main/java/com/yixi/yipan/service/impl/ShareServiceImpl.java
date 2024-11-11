package com.yixi.yipan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixi.yipan.dto.FileDTO;
import com.yixi.yipan.dto.ShareDTO;
import com.yixi.yipan.dto.UserDTO;
import com.yixi.yipan.entity.Share;
import com.yixi.yipan.mapper.ShareMapper;
import com.yixi.yipan.service.FileService;
import com.yixi.yipan.service.ShareService;
import com.yixi.yipan.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 文件分享服务实现类
 *
 * @author yixi
 */
@Slf4j
@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share> implements ShareService {

    @Resource
    private FileService fileService;

    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShareDTO createShare(String userId, String fileId, Date expireTime) {
        // 创建分享
        Share share = new Share();
        share.setUserId(userId);
        share.setFileId(fileId);
        share.setExpireTime(expireTime);
        // 分享码
        share.setShareCode(generateShareCode());
        // 下载次数
        share.setDownloadCount(0);
        // 创建时间
        share.setCreatedAt(new Date());
        // 状态
        share.setStatus(1);
        baseMapper.insert(share);
        return convertToDTO(share, null, null);
    }

    @Override
    public ShareDTO getShareById(String id) {
        // TODO: 实现获取分享详情逻辑
        return null;
    }

    @Override
    public ShareDTO getShareByCode(String shareCode) {
        // TODO: 实现通过分享码获取分享信息逻辑
        return null;
    }

    @Override
    public IPage<ShareDTO> queryUserShares(String userId, Page<Share> page) {
        // 查询分享信息
        Page<Share> sharePage = new Page<>(page.getCurrent(), page.getSize());

        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Share::getUserId, userId);
        // 查询分享信息
        sharePage = baseMapper.selectPage(sharePage, wrapper);
        // 转换为DTO
        Page<ShareDTO> dtoPage = new Page<>(sharePage.getCurrent(), sharePage.getSize(), sharePage.getRecords().size());
        dtoPage.setRecords(sharePage.getRecords().stream()
            .map(share -> convertToDTO(share, fileService.getFileById(share.getFileId()), userService.getUserById(share.getUserId())))
            .collect(Collectors.toList()));
        return dtoPage;
    }

    @Override
    public IPage<ShareDTO> queryAllShares(Page<Share> page) {
        // 查询分享信息
        Page<Share> sharePage = baseMapper.selectPage(page, null);
        // 转换为DTO
        Page<ShareDTO> dtoPage = new Page<>(sharePage.getCurrent(), sharePage.getSize(), sharePage.getRecords().size());
        dtoPage.setRecords(sharePage.getRecords().stream()
            .map(share -> convertToDTO(share, fileService.getFileById(share.getFileId()), userService.getUserById(share.getUserId())))
                .collect(Collectors.toList()));
        return dtoPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelShare(String id) {
        // TODO: 实现取消分享逻辑
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCancelShares(List<String> ids) {
        // TODO: 实现批量取消分享逻辑
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementDownloadCount(String id) {
        // TODO: 实现增加下载次数逻辑
    }

    @Override
    public boolean isShareValid(String shareCode) {
        // TODO: 实现检查分享是否有效逻辑
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanExpiredShares() {
        // TODO: 实现清理过期分享逻辑
    }

    /**
     * 生成分享码
     */
    private String generateShareCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    /**
     * 转换为DTO
     */
    private ShareDTO convertToDTO(Share share, FileDTO file, UserDTO user) {
        if (share == null) {
            return null;
        }
        ShareDTO dto = new ShareDTO();
        BeanUtils.copyProperties(share, dto);
        dto.setFile(file);
        dto.setUser(user);
        return dto;
    }

    /**
     * 检查分享权限
     */
    private void checkShareAccess(Share share) {
        // TODO: 实现分享权限检查逻辑
    }
}