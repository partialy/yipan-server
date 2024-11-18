package com.yixi.yipan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yixi.yipan.dto.UserDTO;
import com.yixi.yipan.entity.User;
import com.yixi.yipan.param.user.UserCreateParam;
import com.yixi.yipan.param.user.UserLoginParam;
import com.yixi.yipan.param.user.UserQueryParam;
import com.yixi.yipan.param.user.UserUpdateParam;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author yixi
 */
public interface UserService extends IService<User> {

    /**
     * 分页查询用户列表
     *
     * @param page  分页参数
     * @param param 查询参数
     * @return 分页结果
     */
    IPage<UserDTO> queryByPage(Page<User> page, UserQueryParam param);

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    UserDTO getUserById(String id);

    /**
     * 创建用户
     *
     * @param param 创建参数
     * @return 用户信息
     */
    UserDTO createUser(UserCreateParam param);

    /**
     * 用户登录
     *
     * @param param 登录参数
     * @return 登录结果
     */
    UserDTO login(UserLoginParam param);

    /**
     * 重置密码
     *
     * @param param 更新参数
     *              return boolean 值，true-更新成功，false-更新失败
     */
    boolean resetPassword(UserUpdateParam param);

    /**
     * 更新用户信息
     *
     * @param id    用户ID
     * @param param 更新参数
     * @return 更新后的用户信息
     */
    UserDTO updateUser(String id, UserUpdateParam param);

    /**
     * 禁用用户
     *
     * @param id 用户ID
     */
    void disableUser(String id);

    /**
     * 启用用户
     *
     * @param id 用户ID
     */
    void enableUser(String id);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteUser(String id);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return true-存在，false-不存在
     */
    boolean checkUsernameExists(String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return true-存在，false-不存在
     */
    boolean checkEmailExists(String email);

    /**
     * 更新用户存储空间使用量
     *
     * @param userId 用户ID
     * @param size   变化的大小（正数增加，负数减少）
     */
    void updateStorageUsed(String userId, Long size);


    /*
      管理员端接口
     */
    int deleteUsers(List<String> userIds);

    boolean updateUserStatus(String userId, Integer status);

    int updateUsersStatus(List<String> userIds, Integer status);
}