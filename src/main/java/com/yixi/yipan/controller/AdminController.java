package com.yixi.yipan.controller;

import com.yixi.yipan.common.result.ResultMsg;
import com.yixi.yipan.dto.UserDTO;
import com.yixi.yipan.param.user.UserCreateParam;
import com.yixi.yipan.param.user.UserUpdateParam;
import com.yixi.yipan.service.ImageService;
import com.yixi.yipan.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员接口", description = "管理员相关的 CRUD 操作")
public class AdminController {
    @Resource
    private ImageService imageService;

    @Operation(summary = "删除不需要的用户头像")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ApiResponse(responseCode = "404", description = "头像不存在")
    @DeleteMapping("/deleteAvatar")
    public ResultMsg<Integer> deleteAvatar(
            @RequestParam("userId") String userId
    ) {
        return ResultMsg.success(imageService.cleanupUnusedImages(userId));
    }

    @Resource
    private UserService userService;

    @Operation(summary = "创建新用户")
    @ApiResponse(responseCode = "200", description = "创建成功")
    @ApiResponse(responseCode = "400", description = "请求参数错误")
    @PostMapping("/createUser")
    public ResultMsg<UserDTO> createUser(@Valid @RequestBody UserCreateParam request) {
        return ResultMsg.success(userService.createUser(request));
    }

    @Operation(summary = "更新用户信息")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @ApiResponse(responseCode = "404", description = "用户不存在")
    @PutMapping("/user/{userId}")
    public ResultMsg<UserDTO> updateUser(
            @PathVariable("userId") String userId,
            @Valid @RequestBody UserUpdateParam request
    ) {
        return ResultMsg.success(userService.updateUser(userId, request));
    }

    @Operation(summary = "删除用户")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ApiResponse(responseCode = "404", description = "用户不存在")
    @DeleteMapping("/user/{userId}")
    public ResultMsg<Boolean> deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return ResultMsg.success(true);
    }

    @Operation(summary = "批量删除用户")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @DeleteMapping("/users")
    public ResultMsg<Integer> deleteUsers(@RequestParam("userIds") List<String> userIds) {
        return ResultMsg.success(userService.deleteUsers(userIds));
    }

    @Operation(summary = "启用/禁用用户")
    @ApiResponse(responseCode = "200", description = "操作成功")
    @ApiResponse(responseCode = "404", description = "用户不存在")
    @PutMapping("/user/{userId}/updateStatus")
    public ResultMsg<Boolean> updateUserStatus(
            @PathVariable("userId") String userId,
            @RequestParam("status") Integer status
    ) {
        return ResultMsg.success(userService.updateUserStatus(userId, status));
    }

    @Operation(summary = "批量启用用户")
    @ApiResponse(responseCode = "200", description = "操作成功")
    @PutMapping("/users/enable")
    public ResultMsg<Integer> enableUsers(@RequestParam("userIds") List<String> userIds) {
        return ResultMsg.success(userService.updateUsersStatus(userIds, 0));
    }

    @Operation(summary = "批量禁用用户")
    @ApiResponse(responseCode = "200", description = "操作成功")
    @PutMapping("/users/disable")
    public ResultMsg<Integer> disableUsers(@RequestParam("userIds") List<String> userIds) {
        return ResultMsg.success(userService.updateUsersStatus(userIds, 1));
    }
}
