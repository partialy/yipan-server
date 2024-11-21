package com.yixi.yipan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
/**
 * 管理员表
 */
@Data
@TableName("admin")
public class Admin implements Serializable {
    /**
     * 管理员ID
     */
    private String id;
    /**
     * 管理员用户名
     */
    private String username;
    /**
     * 管理员密码
     */
    private String password;
    /**
     * 创建时间
     */
    private Date creatAt;
    /**
     * 更新时间
     */
    private Date updateAt;
    /**
     * 状态
     */
    private int status;
}
