package com.yixi.yipan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yixi.yipan.dto.ShareDTO;
import com.yixi.yipan.entity.Share;
import com.yixi.yipan.param.share.ShareCreateParam;

import java.util.Date;
import java.util.List;

/**
 * 文件分享服务接口
 *
 * @author yixi
 */
public interface ShareService extends IService<Share> {

    /**
     * 创建分享
     *
     * @param shareCreateParam 分享创建参数
     * @return 分享信息
     */
    List<ShareDTO> createShare(ShareCreateParam shareCreateParam);

    /**
     * 获取分享详情
     *
     * @param id 分享ID
     * @return 分享详情
     */
    ShareDTO getShareById(String id);
    /**
     * 通过分享码获取分享信息
     *
     * @param shareCode 分享码
     * @return 分享信息
     */
    ShareDTO getShareByCode(String shareCode);

    /**
     * 分页查询用户的分享列表
     *
     * @param userId 用户ID
     * @param page 分页参数
     * @return 分页结果
     */
    IPage<ShareDTO> queryUserShares(String userId, Page<Share> page);

    /**
     * 分页查询所有分享列表
     *
     * @param page 分页参数
     * @return 分页结果
     */
    IPage<ShareDTO> queryAllShares(Page<Share> page);

    /**
     * 取消分享
     *
     * @param id 分享ID
     */
    void cancelShare(String id);

    /**
     * 批量取消分享
     *
     * @param ids 分享ID列表
     */
    void batchCancelShares(List<String> ids);

    /**
     * 增加下载次数
     *
     * @param id 分享ID
     */
    void incrementDownloadCount(String id);

    /**
     * 检查分享是否有效
     *
     * @param shareCode 分享码
     * @return true-有效，false-无效
     */
    boolean isShareValid(String shareCode);

    /**
     * 清理过期分享
     */
    void cleanExpiredShares();

    /**
     * 获取分享列表
     */
    List<ShareDTO> getShareFiles(String userId);
}