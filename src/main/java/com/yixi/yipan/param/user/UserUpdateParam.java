package com.yixi.yipan.param.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户信息更新参数
 *
 * @author yixi
 */
@Data
@Schema(description = "用户信息更新参数")
public class UserUpdateParam {
    
    /**
     * 用户名
     */
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20之间")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "用户名只能包含字母、数字、下划线和横线")
    @Schema(description = "用户名", example = "yixi_123")
    private String username;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    @Schema(description = "邮箱", example = "example@yixi.com")
    private String email;

    /**
     * 密码
     */
    @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$", 
            message = "密码必须包含字母和数字")
    @Schema(description = "密码（必须包含字母和数字）", example = "abc123")
    private String password;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1")
    private Integer status;
}