package com.yixi.yipan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yixi.yipan.dto.AdminDTO;
import com.yixi.yipan.entity.Admin;

import java.util.Map;

public interface AdminService extends IService<Admin> {
    AdminDTO addAdmin(Map<String, String> adminInfo);

    AdminDTO login(Map<String, String> loginInfo);
}
