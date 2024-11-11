package com.yixi.yipan.mapper;

import com.yixi.yipan.dto.UserDTO;
import com.yixi.yipan.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author yixi
 * @since 2024-10-31
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据ID查询用户
     */
    User queryById(String id);

    /**
     * 根据用户名查询用户
     */
    UserDTO queryByUsername(String username);

    /**
     * 根据邮箱查询用户
     */
    User queryByEmail(String email);

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 更新用户信息
     */
    int update(User user);

    /**
     * 更新用户存储空间使用量
     */
    int updateStorageUsed(@Param("id") String id, @Param("size") Long size);

    /**
     * 禁用用户
     */
    int disable(String id);

    /**
     * 检查用户名是否存在
     */
    int checkUsernameExists(String username);

    /**
     * 检查邮箱是否存在
     */
    int checkEmailExists(String email);

}
