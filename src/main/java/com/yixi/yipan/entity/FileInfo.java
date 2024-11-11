package com.yixi.yipan.entity;

import java.util.Date;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 文件信息表(FileInfo)实体类
 *
 * @author yixi
 * @since 2024-10-31 14:38:38
 */

 @Data
 @TableName("file")
public class FileInfo implements Serializable {
    private static final long serialVersionUID = 914032288557626877L;
/**
     * 文件ID
     */
    private String id;
/**
     * 用户ID
     */
    private String userId;
/**
     * 父文件夹ID，根目录为null
     */
    private String parentId;
/**
     * 文件名称
     */
    private String fileName;
/**
     * 文件大小(bytes)
     */
    private Long fileSize;
/**
     * 文件类型/MIME类型
     */
    private String fileType;
/**
     * 文件实际存储路径
     */
    private String filePath;
/**
     * 文件MD5值，用于秒传功能
     */
    private String md5;
/**
     * 是否是文件夹：0-文件，1-文件夹
     */
    private Integer isFolder;
/**
     * 创建时间
     */
    private Date createdAt;
/**
     * 更新时间
     */
    private Date updatedAt;
/**
     * 删除时间（软删除）
     */
    private Date deletedAt;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Integer getIsFolder() {
        return isFolder;
    }

    public void setIsFolder(Integer isFolder) {
        this.isFolder = isFolder;
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

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

}

