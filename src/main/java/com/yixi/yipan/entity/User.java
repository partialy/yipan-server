package com.yixi.yipan.entity;

import java.util.Date;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * 用户表(User)实体类
 *
 * @author yixi
 * @since 2024-10-31 14:38:39
 */

 @Data
 @TableName("user")
public class User implements Serializable {
    private static final long serialVersionUID = 118316643775119174L;
/**
     * 用户UUID
     */
    private String id;
/**
     * 用户名
     */
    private String username;
/**
     * 密码
     */
    private String password;
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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getStorageUsed() {
        return storageUsed;
    }

    public void setStorageUsed(Long storageUsed) {
        this.storageUsed = storageUsed;
    }

    public Long getStorageLimit() {
        return storageLimit;
    }

    public void setStorageLimit(Long storageLimit) {
        this.storageLimit = storageLimit;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}

