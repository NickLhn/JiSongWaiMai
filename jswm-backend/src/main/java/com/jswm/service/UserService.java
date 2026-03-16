package com.jswm.service;

import com.jswm.entity.SysUser;

public interface UserService {
    SysUser login(String username, String password);
    SysUser register(String username, String password, String phone, String realName, Integer role);
    SysUser getUserById(Long id);
    SysUser getUserInfo(Long id);
    void updateUserInfo(Long id, SysUser user);
}
