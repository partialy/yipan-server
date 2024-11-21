package com.yixi.yipan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixi.yipan.common.result.ResultMsg;
import com.yixi.yipan.dto.UserDTO;
import com.yixi.yipan.entity.FileInfo;
import com.yixi.yipan.mapper.UserMapper;
import com.yixi.yipan.param.user.UserCreateParam;
import com.yixi.yipan.param.user.UserUpdateParam;
import com.yixi.yipan.service.FileService;
import com.yixi.yipan.service.ImageService;
import com.yixi.yipan.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员接口", description = "管理员相关的 CRUD 操作")
public class AdminController {
    @Resource
    private ImageService imageService;

    @Resource
    private FileService fileService;

    @Resource
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Operation(summary = "删除不需要的用户头像")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ApiResponse(responseCode = "404", description = "头像不存在")
    @DeleteMapping("/deleteAvatar")
    public ResultMsg<Integer> deleteAvatar(
            @RequestParam("userId") String userId
    ) {
        return ResultMsg.success(imageService.cleanupUnusedImages(userId));
    }

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

    @Operation(summary = "编辑用户")
    @ApiResponse(responseCode = "200", description = "编辑成功")
    @PutMapping("/user/{userId}/edit")
    public ResultMsg<UserDTO> editUser(
            @PathVariable("userId") String userId,
            @Valid @RequestBody UserUpdateParam request
    ) {
        return ResultMsg.success(userService.editUser(userId, request));
    }

    @Operation(summary = "获取仪表盘数据")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/dashboard/statics")
    public ResultMsg<Object> getDashboardData() {
        Map<String, Object> result = new HashMap<>();
        long userCount = userService.count();
        long activeUserCount = userService.countActiveUser();
        long fileCount = fileService.count(new QueryWrapper<FileInfo>()
                .isNull("deleted_at")
                .eq("is_folder",0));
        long totalStorageUsed = userMapper.getTotalStorageUsed();
        result.put("userCount", userCount);
        result.put("activeUserCount", activeUserCount);
        result.put("fileCount", fileCount);
        result.put("totalStorageUsed", totalStorageUsed);
        return new ResultMsg(200, "获取成功", result);
    }

    @Operation(summary = "获取仪表盘图表数据")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/dashboard/user-trend")
    public ResultMsg<List<Integer>> getUserTrend(@RequestParam("range") String range) {
        if (range == null) {
            range = "week";
        }
        return new ResultMsg(200, "获取成功", userService.getUserTrend(range));
    }

    @Operation(summary = "获取仪表盘文件分布数据")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/dashboard/file-types")
    public ResultMsg<List<Map<String, Object>>> getFileTypeVO() {
        return new ResultMsg(200, "获取成功", fileService.getFileTypeVO());
    }
}
