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
import com.yixi.yipan.param.share.ShareCreateParam;
import com.yixi.yipan.service.FileService;
import com.yixi.yipan.service.ShareService;
import com.yixi.yipan.service.UserService;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.ArrayList;
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
    
    @Value("${server.frontDomain}")
    private String domain;

    @Resource
    private FileService fileService;

    @Resource
    private UserService userService;
    @Autowired
    private ShareMapper shareMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ShareDTO> createShare(ShareCreateParam param) {
        String shareCode = generateShareCode(12);
        List<ShareDTO> shareDTOList = new ArrayList<>();
        // 创建分享
        for (String fileId : param.getFileIds()) {
            Share share = new Share();
            share.setUserId(param.getUserId());
            share.setExpireTime(param.getExpireTime());
            share.setFileId(fileId);
            // 分享码
            if (param.getIsNeedCode()==1) {
                if (param.getAccessCode() == null) {
                    share.setAccessCode(generateShareCode(4));
                } else {
                    share.setAccessCode(param.getAccessCode());
                }
            } else {
                share.setAccessCode(null);
            }
            share.setShareCode(shareCode);
            // 下载次数
            share.setDownloadCount(0);
            // 创建时间
            share.setCreatedAt(new Date());
            // 状态
            share.setStatus(0);
            // 生成分享链接
            share.setLink(domain + "/share/" + share.getShareCode());
            baseMapper.insert(share);
            shareDTOList.add(convertToDTO(share,  null));
        }
        // 转换为DTO
        return shareDTOList;
    }

    @Override
    public ShareDTO getShareById(String id) {
        // TODO: 实现获取分享详情逻辑
        return null;
    }

    @Override
    public ShareDTO getShareByCode(String shareCode) {
        // 查询分享信息
        List<Share> share = baseMapper.queryByShareCode(shareCode);
        List<FileDTO> fileDTOList = fileService.getFilesByIds(share.stream().map(Share::getFileId).toList());
        // 转换为DTO
        return convertToDTO(share.get(0), fileDTOList);
    }

    @Override
    public IPage<ShareDTO> queryUserShares(String userId, Page<Share> page) {
        return null;
    }

    @Override
    public IPage<ShareDTO> queryAllShares(Page<Share> page) {
        return null;
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

    @Override
    public List<ShareDTO> getShareFiles(String userId) {
        List<String> codeList = shareMapper.queryShareCodeByUserId(userId);
        List<ShareDTO> shareDTOList = new ArrayList<>();
        for (String code : codeList) {
            shareDTOList.add(this.getShareByCode(code));
        }
        return shareDTOList;
    }

    /**
     * 生成分享码
     */
    private String generateShareCode(int length) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, length);
    }

    /**
     * 转换为DTO
     */
    private ShareDTO convertToDTO(Share share, List<FileDTO> fileDTOList) {
        if (share == null) {
            return null;
        }
        ShareDTO dto = new ShareDTO();
        BeanUtils.copyProperties(share, dto);
        dto.setUserName(userService.getUserById(share.getUserId()).getUsername());
        dto.setFileDTOList(fileDTOList);
        return dto;
    }

    /**
     * 检查分享权限
     */
    private void checkShareAccess(Share share) {
        // TODO: 实现分享权限检查逻辑
    }
}