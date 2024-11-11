package com.yixi.yipan.entity;

import java.util.Date;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 文件分享表(Share)实体类
 *
 * @author yixi
 * @since 2024-10-31 14:38:39
 */

 @Data
 @TableName("share")
public class Share implements Serializable {
    private static final long serialVersionUID = 527769116758023712L;
/**
     * 分享ID
     */
    private String id;
/**
     * 分享用户ID
     */
    private String userId;
/**
     * 被分享的文件ID
     */
    private String fileId;
/**
     * 分享码，用于访问分享内容
     */
    private String shareCode;
/**
     * 分享链接过期时间
     */
    private Date expireTime;
/**
     * 分享内容被下载次数
     */
    private Integer downloadCount;
/**
     * 分享创建时间
     */
    private Date createdAt;
/**
     * 分享状态：0-已取消，1-正常
     */
    private Integer status;


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

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getShareCode() {
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}

