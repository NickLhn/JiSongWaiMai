package com.jswm.mapper;

import com.jswm.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper {
    SysUser selectById(Long id);
    SysUser selectByUsername(String username);
    SysUser selectByPhone(String phone);
    int insert(SysUser user);
    int updateById(SysUser user);
    int deleteById(Long id);
}
