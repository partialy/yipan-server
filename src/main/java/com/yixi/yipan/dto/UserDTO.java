package com.yixi.yipan.dto;

import java.util.Date;
import lombok.Data;

/**
 * 用户信息传输对象
 *
 * @author yixi
 * @since 2024-10-31
 */
@Data
public class UserDTO {
    /**
     * 用户UUID
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 已用空间
     */
    private Long storageUsed;

    /**
     * 总空间
     */
    private Long storageLimit;

    /**
     * 注册时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 0:禁用，1:启用
     */
    private Integer status;
}