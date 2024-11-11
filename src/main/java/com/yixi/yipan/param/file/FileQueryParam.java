package com.yixi.yipan.param.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "文件查询参数")
public class FileQueryParam {

    @Schema(description = "用户ID")
    private String userId;
    
    @Schema(description = "文件名（支持模糊查询）")
    private String fileName;
    
    @Schema(description = "父文件夹ID")
    private String parentId;
    
    @Schema(description = "文件类型")
    private String fileType;
    
    @Schema(description = "是否是文件夹")
    private Boolean isFolder;
    
    @Schema(description = "创建开始时间")
    private String startTime;
    
    @Schema(description = "创建结束时间")
    private String endTime;
}
