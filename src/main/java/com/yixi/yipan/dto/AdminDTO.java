package com.yixi.yipan.dto;

import lombok.Data;

import java.util.Date;

/**
 * 管理员DTO
 * @author yixi
 * @since 2024-11-21
 */
@Data
public class AdminDTO {
    /**
     * 管理员ID
     */
    private String id;
    /**
     * 管理员用户名
     */
    private String username;
    /**
     * 创建时间
     */
    private Date createAt;
    /**
     * 更新时间
     */
    private Date updateAt;
    /**
     * 状态
     */
    private int status;
}
