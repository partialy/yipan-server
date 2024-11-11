package com.yixi.yipan.param.file;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "文件夹创建参数")
public class FileFoldCreateParam {
    
    @NotBlank(message = "文件夹名称不能为空")
    @Size(max = 255, message = "文件夹名称不能超过255个字符")
    @Schema(description = "文件夹名称", required = true)
    private String fileName;
    
    @Schema(description = "父文件夹ID")
    private String parentId;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "路径")
    private String path;
}
