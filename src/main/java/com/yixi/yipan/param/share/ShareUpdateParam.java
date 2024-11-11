package com.yixi.yipan.param.share;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "分享更新参数")
public class ShareUpdateParam {
    
    @Schema(description = "分享密码")
    private String password;
    
    @Schema(description = "过期时间")
    private Date expireTime;
    
    @Schema(description = "最大下载次数")
    private Integer maxDownloads;
    
    @Schema(description = "分享说明")
    private String description;
}
