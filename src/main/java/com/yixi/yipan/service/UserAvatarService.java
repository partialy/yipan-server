package com.yixi.yipan.service;

import com.yixi.yipan.entity.UserAvatar;

public interface UserAvatarService {
    /**
     * 设置用户头像
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     * @return 头像URL
     */
    String setAvatar(String userId, String avatarUrl);

    /**
     * 更新用户头像
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     * @return 是否更新成功
     */
    boolean updateAvatar(String userId, String avatarUrl);

    /**
     * 获取用户头像
     * @param userId 用户ID
     * @return 用户头像信息
     */
    UserAvatar getUserAvatar(String userId);
}