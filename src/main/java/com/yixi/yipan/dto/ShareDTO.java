package com.yixi.yipan.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 文件分享信息传输对象
 *
 * @author yixi
 * @since 2024-10-31
 */
@Getter
@Setter
@Data
public class ShareDTO {
    /**
     * 分享ID
     */
    private String id;

    /**
     * 分享用户id
     */
    private String userId;

    /**
     * 分享用户名
     */
    private String userName;

    /**
     * 分享码，用于访问分享内容
     */
    private String shareCode;
    /**
     * 访问码，用于访问分享内容
     */
    private String accessCode;
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
    private List<FileDTO> fileDTOList;

    /**
     * 链接
     */
    private String link;
}