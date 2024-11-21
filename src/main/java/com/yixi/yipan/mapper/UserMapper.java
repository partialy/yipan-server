package com.yixi.yipan.mapper;

import com.yixi.yipan.dto.UserDTO;
import com.yixi.yipan.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    int updateUserStatus(String id, @Param("status") Integer status);

    /**
     * 检查用户名是否存在
     */
    int checkUsernameExists(String username);

    /**
     * 检查邮箱是否存在
     */
    int checkEmailExists(String email);

    /**
     * 获取活跃用户数
     */
    int countActiveUser();

    /**
     * 获取存储总用量
     */
    Long getTotalStorageUsed();

    /**
     * 获取每日活跃用户数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 返回格式 [{count: 100, login_date: "2024-03-01"}, ...]
     */
    List<Map<String, Object>> getDailyActiveUsers(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 获取每月活跃用户数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 返回格式 [{count: 3000, month: 3, month_date: "2024-03"}, ...]
     */
    List<Map<String, Object>> getMonthlyActiveUsers(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
