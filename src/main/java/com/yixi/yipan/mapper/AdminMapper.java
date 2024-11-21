package com.yixi.yipan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixi.yipan.dto.AdminDTO;
import com.yixi.yipan.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 管理员表 Mapper 接口
 * </p>
 *
 * @author yixi
 * @since 2024-11-21
 */

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    Admin selectByUsername(String username);
}
