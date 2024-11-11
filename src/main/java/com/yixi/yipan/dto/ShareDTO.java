package com.yixi.yipan.dto;

import java.util.Date;
import lombok.Data;

/**
 * 文件分享信息传输对象
 *
 * @author yixi
 * @since 2024-10-31
 */
@Data
public class ShareDTO {
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
    private Long fileId;

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

    /**
     * 分享的文件信息
     */
    private FileDTO file;

    /**
     * 分享用户信息
     */
    private UserDTO user;
}