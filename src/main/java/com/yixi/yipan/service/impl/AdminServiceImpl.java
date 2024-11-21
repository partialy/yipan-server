package com.yixi.yipan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixi.yipan.dto.AdminDTO;
import com.yixi.yipan.entity.Admin;
import com.yixi.yipan.exception.BusinessException;
import com.yixi.yipan.mapper.AdminMapper;
import com.yixi.yipan.service.AdminService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AdminDTO addAdmin(Map<String, String> adminInfo) {
        Admin admin = new Admin();
        admin.setId(UUID.randomUUID().toString());
        admin.setUsername(adminInfo.get("username"));
        admin.setPassword(passwordEncoder.encode(adminInfo.get("password")));
        admin.setCreatAt(new Date());
        admin.setUpdateAt(new Date());
        admin.setStatus(0);
        adminMapper.insert(admin);
        return this.convertAdminDTO(admin);
    }

    @Override
    public AdminDTO login(Map<String, String> loginInfo) {
        Admin adminInfo = adminMapper.selectByUsername(loginInfo.get("username"));
        if (adminInfo == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(loginInfo.get("password"), adminInfo.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        adminInfo.setUpdateAt(new Date());
        baseMapper.updateById(adminInfo);
        return this.convertAdminDTO(adminInfo);
    }

    public AdminDTO convertAdminDTO(Admin admin) {
        AdminDTO dto = new AdminDTO();
        dto.setId(admin.getId());
        dto.setUsername(admin.getUsername());
        dto.setCreateAt(admin.getCreatAt());
        dto.setUpdateAt(admin.getUpdateAt());
        dto.setStatus(admin.getStatus());
        return dto;
    }
}
