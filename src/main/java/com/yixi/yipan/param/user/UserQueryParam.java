package com.yixi.yipan.param.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户查询参数
 *
 * @author yixi
 */
@Data
@Schema(description = "用户查询参数")
public class UserQueryParam {
    
    /**
     * 用户名（模糊查询）
     */
    @Schema(description = "用户名（支持模糊查询）")
    private String username;
    
    /**
     * 邮箱（模糊查询）
     */
    @Schema(description = "邮箱（支持模糊查询）")
    private String email;
    
    /**
     * 用户状态（0:禁用，1:启用）
     */
    @Schema(description = "用户状态（0:禁用，1:启用）")
    private Integer status;
    
    /**
     * 开始时间
     */
    @Schema(description = "注册开始时间", example = "2024-01-01")
    private String startTime;
    
    /**
     * 结束时间
     */
    @Schema(description = "注册结束时间", example = "2024-12-31")
    private String endTime;
    
    /**
     * 排序字段
     */
    @Schema(description = "排序字段", example = "created_at")
    private String orderBy;
    
    /**
     * 排序方向（asc/desc）
     */
    @Schema(description = "排序方向", example = "desc", allowableValues = {"asc", "desc"})
    private String orderDirection;
}