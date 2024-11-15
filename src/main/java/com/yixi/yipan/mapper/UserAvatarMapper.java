package com.yixi.yipan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixi.yipan.entity.UserAvatar;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAvatarMapper extends BaseMapper<UserAvatar> {
    void deleteByUserId(String userId);

    UserAvatar selectByUserId(String userId);

    boolean existsByAvatarUrl(String avatarUrl);
}
