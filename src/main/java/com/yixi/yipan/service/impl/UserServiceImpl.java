package com.yixi.yipan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixi.yipan.common.utils.LogUtils;
import com.yixi.yipan.dto.UserDTO;
import com.yixi.yipan.entity.User;
import com.yixi.yipan.exception.BusinessException;
import com.yixi.yipan.mapper.UserMapper;
import com.yixi.yipan.param.user.UserCreateParam;
import com.yixi.yipan.param.user.UserQueryParam;
import com.yixi.yipan.param.user.UserUpdateParam;
import com.yixi.yipan.service.UserService;
import com.yixi.yipan.param.user.UserLoginParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author yixi
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private UserMapper userMapper;

    @Override
    public Page<UserDTO> queryByPage(Page<User> page, UserQueryParam param) {
        System.out.println(page);
        System.out.println(param);
        System.out.println("_____________________________________________________________");
        // 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .like(StringUtils.hasText(param.getUsername()), User::getUsername, param.getUsername())
                .like(StringUtils.hasText(param.getEmail()), User::getEmail, param.getEmail())
                .eq(param.getStatus() != null, User::getStatus, param.getStatus())
                .ge(param.getStartTime() != null, User::getCreatedAt, param.getStartTime())
                .le(param.getEndTime() != null, User::getCreatedAt, param.getEndTime())
                .orderByDesc(User::getCreatedAt);

        Page<User> userPage = baseMapper.selectPage(page, wrapper);

        // 创建新的Page对象并复制所有分页信息
        Page<UserDTO> dtoPage = new Page<>();
        dtoPage.setCurrent(userPage.getCurrent());
        dtoPage.setSize(userPage.getSize());
        dtoPage.setTotal(userPage.getTotal());
        // 转换记录
        List<UserDTO> dtoList = userPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    /*
     * 获取用户信息
     */
    @Override
    public UserDTO getUserById(String id) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToDTO(user);
    }

    /**
     * 创建用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO createUser(UserCreateParam param) {
        if (checkUsernameExists(param.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        if (checkEmailExists(param.getEmail())) {
            throw new BusinessException("邮箱已存在");
        }
        User user = new User();
        BeanUtils.copyProperties(param, user);
        user.setPassword(passwordEncoder.encode(param.getPassword()));
        user.setId(UUID.randomUUID().toString());
        user.setStorageUsed(0L);
        user.setStorageLimit(5585058816L);
        user.setCreatedAt(new Date());
        user.setStatus(0);
        save(user);
        return convertToDTO(user);
    }

    /**
     * 用户登录
     */
    @Override
    public UserDTO login(UserLoginParam param) {
        // 根据用户名查询用户
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, param.getUsername()));
        UserDTO userDTO = convertToDTO(user);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 验证密码
        if (!passwordEncoder.matches(param.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        LogUtils.info("{}用户{}登录成功", new Date(), user.getUsername());
        return userDTO;
    }

    /**
     * 更新用户信息
     */
    @Override
    public boolean resetPassword(UserUpdateParam param) {
        User userinfo = userMapper.queryByEmail(param.getEmail());
        if (userinfo == null) {
            throw new BusinessException("用户不存在");
        }
        if (userinfo.getStatus() != 0) {
            throw new BusinessException("用户状态不正常");
        }
        userinfo.setPassword(passwordEncoder.encode(param.getPassword()));
        userinfo.setUpdatedAt(new Date());
        userMapper.updateById(userinfo);
        return true;
    }

    /**
     * 更新用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO updateUser(String id, UserUpdateParam param) {
        User user = this.getById(id);
        user.setPassword(passwordEncoder.encode(param.getPassword()));
        user.setUsername(param.getUsername());
        user.setEmail(param.getEmail());
        user.setUpdatedAt(new Date());
        updateById(user);
        return convertToDTO(user);
    }

    /**
     * 禁用用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableUser(String id) {
        User user = this.getById(id);
        user.setStatus(1);
        updateById(user);
    }

    /**
     * 启用用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableUser(String id) {
        User user = this.getById(id);
        user.setStatus(0);
        updateById(user);
    }

    /**
     * 删除用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(String id) {
        removeById(id);
    }

    /**
     * 检查用户名是否存在
     */
    @Override
    public boolean checkUsernameExists(String username) {
        return count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)) > 0;
    }

    /**
     * 检查邮箱是否存在
     */
    @Override
    public boolean checkEmailExists(String email) {
        return count(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email)) > 0;
    }

    /**
     * 更新存储空间使用量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStorageUsed(String userId, Long size) {
        User user = this.getById(userId);
        user.setStorageUsed(user.getStorageUsed() + size);
        updateById(user);
    }

    // 管理员端接口实现

    @Override
    public int deleteUsers(List<String> userIds) {
        try {
            for (String userId : userIds) {
                this.removeById(userId);
            }
        } catch (Exception e) {
            log.error("删除用户失败", e);
            throw new BusinessException(e.getMessage());
        }
        return userIds.size();
    }

    @Override
    public boolean updateUserStatus(String userId, Integer status) {
        try {
            userMapper.updateUserStatus(userId, status);
            return true;
        } catch (Exception e) {
            log.error("更新用户状态失败", e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public int updateUsersStatus(List<String> userIds, Integer status) {
        for (String userId : userIds) {
            this.updateUserStatus(userId, status);
        }
        return userIds.size();
    }

    /**
     * 将用户实体转换为DTO
     */
    private UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        // 不要复制密码等敏感信息
        return dto;
    }

    /**
     * 检查存储空间是否足够
     */
    private boolean checkStorageSpace(String userId, Long size) {
        User user = this.getById(userId);
        return user.getStorageUsed() + size <= user.getStorageLimit();
    }

    /**
     * 检查用户状态是否正常
     */
    private void checkUserStatus(User user) {
        if (user.getStatus() != 0) {
            throw new BusinessException("用户状态不正常");
        }
    }

}