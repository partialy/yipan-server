package com.yixi.yipan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixi.yipan.entity.Share;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 文件分享表(Share)数据库访问层
 *
 * @author yixi
 * @since 2024-10-31
 */

@Mapper
public interface ShareMapper extends BaseMapper<Share> {
    /**
     * 插入分享记录
     *
     * @param share 分享信息
     * @return 影响行数
     */
    int insert(Share share);

    /**
     * 根据ID查询分享
     *
     * @param id 分享ID
     * @return 分享信息
     */
    Share queryById(String id);

    /**
     * 根据分享码查询分享
     *
     * @param shareCode 分享码
     * @return 分享信息
     */
    Share queryByShareCode(String shareCode);

    /**
     * 查询用户的分享列表
     *
     * @param userId 用户ID
     * @return 分享列表
     */
    List<Share> queryByUserId(String userId);

    /**
     * 增加分享下载次数
     *
     * @param id 分享ID
     * @return 影响行数
     */
    int incrementDownloadCount(String id);

    /**
     * 取消分享
     *
     * @param id 分享ID
     * @param userId 用户ID
     * @return 影响行数
     */
    int cancelShare(@Param("id") String id, @Param("userId") String userId);

    /**
     * 删除过期的分享记录
     *
     * @return 影响行数
     */
    int deleteExpiredShares();

    /**
     * 检查分享是否有效
     *
     * @param id 分享ID
     * @return 有效的分享数量
     */
    int checkShareValid(String id);
}