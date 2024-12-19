package com.yixi.yipan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user_avatar")
public class UserAvatar {
    @TableId
    private String id;

    private String userId;

    @TableField("avatar_url")
    private String avatarUrl;

    private Date updateTime;
}
