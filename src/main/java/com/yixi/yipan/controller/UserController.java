package com.yixi.yipan.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixi.yipan.common.result.ResultMsg;
import com.yixi.yipan.common.utils.LogUtils;
import com.yixi.yipan.dto.UserDTO;
import com.yixi.yipan.entity.UserAvatar;
import com.yixi.yipan.param.user.UserQueryParam;
import com.yixi.yipan.param.user.UserCreateParam;
import com.yixi.yipan.param.user.UserUpdateParam;
import com.yixi.yipan.param.user.UserLoginParam;
import com.yixi.yipan.service.UserAvatarService;
import com.yixi.yipan.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.yixi.yipan.entity.User;

import java.util.Date;

/**
 * 用户管理接口
 *
 * @author yixi
 */
@Tag(name = "用户管理", description = "用户的增删改查接口")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserAvatarService userAvatarService;

    @Operation(summary = "分页查询用户列表", description = "支持多条件查询和排序")
    @Parameters({
            @Parameter(name = "current", description = "当前页码", example = "1"),
            @Parameter(name = "size", description = "每页大小", example = "10")
    })
    @GetMapping
    public ResponseEntity<IPage<UserDTO>> list(
            @Valid UserQueryParam param,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        Page<User> page = new Page<>(current, size);
        return ResponseEntity.ok(userService.queryByPage(page, param));
    }

    @Operation(summary = "获取用户详情")
    @ApiResponse(responseCode = "200", description = "成功获取用户信息")
    @ApiResponse(responseCode = "404", description = "用户不存在")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "创建用户", description = "创建新用户，用户名和邮箱不能重复")
    @ApiResponse(responseCode = "200", description = "用户创建成功")
    @ApiResponse(responseCode = "400", description = "请求参数错误")
    @PostMapping("/register")
    public ResultMsg<UserDTO> create(@Valid @RequestBody UserCreateParam param) {
        LogUtils.info("{},用户{}注册", new Date(),param.getUsername());
        return ResultMsg.success(userService.createUser(param));
    }


    @Operation(summary = "用户重置密码", description = "重置用户密码")
    @ApiResponse(responseCode = "200", description = "密码重置成功")
    @ApiResponse(responseCode = "400", description = "请求参数错误")
    @PutMapping("/reset-password")
    public ResultMsg<Boolean> resetPassword(@Valid @RequestBody UserUpdateParam param) {
        LogUtils.info("{},用户{}重置密码", new Date(),param.getUsername());
        return ResultMsg.success(userService.resetPassword(param));
    }

    @Operation(summary = "用户登录", description = "用户登录")
    @ApiResponse(responseCode = "200", description = "登录成功")
    @ApiResponse(responseCode = "400", description = "请求参数错误")
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody UserLoginParam param) {
        LogUtils.info("{},用户{}尝试登录", new Date(),param.getUsername());
        return ResponseEntity.ok(userService.login(param));
    }

    @Operation(summary = "更新用户信息", description = "支持部分字段更新")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @ApiResponse(responseCode = "404", description = "用户不存在")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String id,
            @Valid @RequestBody UserUpdateParam param) {
        return ResponseEntity.ok(userService.updateUser(id, param));
    }

    @Operation(summary = "删除用户")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ApiResponse(responseCode = "404", description = "用户不存在")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "禁用用户")
    @ApiResponse(responseCode = "200", description = "禁用成功")
    @ApiResponse(responseCode = "404", description = "用户不存在")
    @PutMapping("/{id}/disable")
    public ResponseEntity<Void> disable(@PathVariable String id) {
        userService.disableUser(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "启用用户")
    @ApiResponse(responseCode = "200", description = "启用成功")
    @ApiResponse(responseCode = "404", description = "用户不存在")
    @PutMapping("/{id}/enable")
    public ResponseEntity<Void> enable(@PathVariable String id) {
        userService.enableUser(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "设置用户头像")
    @ApiResponse(responseCode = "200", description = "设置成功")
    @ApiResponse(responseCode = "404", description = "用户不存在")
    @PutMapping("/{id}/setAvatar")
    public ResultMsg<String> setAvatar(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String id,
            @Parameter(description = "头像url", required = true)
            @RequestParam("avatarUrl") String avatarUrl) {
        return ResultMsg.success(userAvatarService.setAvatar(id, avatarUrl));
    }

    @Operation(summary = "获取用户头像")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @ApiResponse(responseCode = "404", description = "用户不存在")
    @GetMapping("/{id}/getAvatar")
    public ResultMsg<UserAvatar> getAvatar(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String id) {
        return ResultMsg.success(userAvatarService.getUserAvatar(id));
    }

    @Operation(summary = "更新用户头像")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @ApiResponse(responseCode = "404", description = "用户不存在")
    @PutMapping("/{id}/updateAvatar")
    public ResultMsg<Boolean> updateAvatar(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String id,
            @Parameter(description = "头像url", required = true)
            @RequestParam("avatarUrl") String avatarUrl) {
        return ResultMsg.success(userAvatarService.updateAvatar(id, avatarUrl));
    }
}