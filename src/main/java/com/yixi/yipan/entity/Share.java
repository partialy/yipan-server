package com.yixi.yipan.entity;

import java.io.Serial;
import java.util.Date;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 文件分享表(Share)实体类
 *
 * @author yixi
 * @since 2024-10-31 14:38:39
 */

 @Setter
 @Getter
 @Data
 @TableName("share")
public class Share implements Serializable {
    @Serial
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
     * 提取码
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
     * 分享链接
     */
    private String link;
}

