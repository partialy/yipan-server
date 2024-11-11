package com.yixi.yipan.param.file;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "文件更新参数")
public class FileUpdateParam {
    
    @Size(max = 255, message = "文件名不能超过255个字符")
    @Schema(description = "新文件名")
    private String filename;

    @Schema(description = "还原文件")
    private boolean restore;
}