package com.yixi.yipan.param.share;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "分享查询参数")
public class ShareQueryParam {
    
    @Schema(description = "文件名")
    private String filename;
    
    @Schema(description = "创建者ID")
    private String creatorId;
    
    @Schema(description = "是否过期")
    private Boolean expired;
    
    @Schema(description = "开始时间")
    private Date startTime;
    
    @Schema(description = "结束时间")
    private Date endTime;
}
