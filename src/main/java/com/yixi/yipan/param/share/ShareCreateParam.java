package com.yixi.yipan.param.share;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "分享创建参数")
public class ShareCreateParam {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @NotNull(message = "文件ID不能为空")
    @Schema(description = "文件ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fileId;
    
    @Schema(description = "分享密码")
    private String password;
    
    @Schema(description = "过期时间")
    private Date expireTime;
    
    @Schema(description = "最大下载次数")
    private Integer maxDownloads;
    
    @Schema(description = "分享说明")
    private String description;
}