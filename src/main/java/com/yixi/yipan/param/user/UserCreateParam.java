package com.yixi.yipan.param.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户创建参数
 *
 * @author yixi
 */
@Data
@Schema(description = "用户创建参数")
public class UserCreateParam {
    
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20之间")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "用户名只能包含字母、数字、下划线和横线")
    @Schema(description = "用户名", example = "yixi_123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,20}$",
            message = "密码必须包含字母和数字")
    @Schema(description = "密码", example = "abc123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    @Schema(description = "邮箱", example = "example@yixi.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
}