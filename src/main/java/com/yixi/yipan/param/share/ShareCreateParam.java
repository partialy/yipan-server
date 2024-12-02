package com.yixi.yipan.param.share;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Schema(description = "分享创建参数")
public class ShareCreateParam {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @NotNull(message = "文件ID不能为空")
    @Schema(description = "文件ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> fileIds;

    @NotNull(message = "是否需要分享码不能为空")
    @Schema(description = "是否需要提取码", requiredMode = Schema.RequiredMode.REQUIRED)
    private int isNeedCode;

    @Schema(description = "提取码")
    private String accessCode;

    @Schema(description = "过期时间")
    private Date expireTime;
}