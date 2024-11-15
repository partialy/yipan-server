package com.yixi.yipan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixi.yipan.entity.UserAvatar;
import com.yixi.yipan.mapper.UserAvatarMapper;
import com.yixi.yipan.service.UserAvatarService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.UUID;

@Slf4j
@Service
public class UserAvatarServiceImpl extends ServiceImpl<UserAvatarMapper, UserAvatar> implements UserAvatarService {

    @Resource
    private UserAvatarMapper avatarMapper;

    @Override
    public String setAvatar(String userId, String avatarUrl) {
        if(avatarMapper.selectByUserId(userId)!= null){
            return String.valueOf(updateAvatar(userId, avatarUrl));
        }
        UserAvatar userAvatar = new UserAvatar();
        userAvatar.setId(UUID.randomUUID().toString());
        userAvatar.setUserId(userId);
        userAvatar.setAvatarUrl(avatarUrl);
        userAvatar.setUpdateTime(new Date(System.currentTimeMillis()));
        avatarMapper.insert(userAvatar);
        return avatarUrl;
    }

    @Override
    public boolean updateAvatar(String userId, String avatarUrl) {
        UserAvatar userAvatar = avatarMapper.selectByUserId(userId);
        userAvatar.setAvatarUrl(avatarUrl);
        userAvatar.setUpdateTime(new Date(System.currentTimeMillis()));
        avatarMapper.updateById(userAvatar);
        return true;
    }

    @Override
    public UserAvatar getUserAvatar(String userId) {

        System.out.println(avatarMapper.selectByUserId(userId));
        return avatarMapper.selectByUserId(userId);
    }


}
