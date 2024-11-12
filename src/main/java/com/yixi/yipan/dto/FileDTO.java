package com.yixi.yipan.dto;

import java.util.Date;
import lombok.Data;

/**
 * 文件信息传输对象
 *
 * @author yixi
 * @since 2024-10-31
 */
@Data
public class FileDTO {
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
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小(bytes)
     */
    private Long fileSize;

    /**
     * 文件类型/MIME类型
     */
    private String fileType;

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
     * 删除时间
     */
    private Date deletedAt;
}