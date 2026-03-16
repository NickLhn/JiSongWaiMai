package com.jswm.service.impl;

import com.jswm.entity.SysUser;
import com.jswm.exception.BusinessException;
import com.jswm.mapper.SysUserMapper;
import com.jswm.service.UserService;
import cn.hutool.crypto.digest.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public SysUser login(String username, String password) {
        SysUser user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(1002, "用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(1003, "账号已被禁用");
        }
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new BusinessException(1002, "用户名或密码错误");
        }
        return user;
    }

    @Override
    public SysUser register(String username, String password, String phone, String realName, Integer role) {
        SysUser existUser = userMapper.selectByUsername(username);
        if (existUser != null) {
            throw new BusinessException(1001, "用户名已存在");
        }
        if (phone != null && !phone.isEmpty()) {
            SysUser existPhone = userMapper.selectByPhone(phone);
            if (existPhone != null) {
                throw new BusinessException(1004, "手机号已被注册");
            }
        }
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setPhone(phone);
        user.setRealName(realName);
        // 角色：0=学生，1=商家，2=管理员（仅后台创建）
        user.setRole(role != null ? role : 0);
        user.setStatus(1);
        userMapper.insert(user);
        return user;
    }

    @Override
    public SysUser getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public SysUser getUserInfo(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(1005, "用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public void updateUserInfo(Long id, SysUser user) {
        SysUser existUser = userMapper.selectById(id);
        if (existUser == null) {
            throw new BusinessException(1005, "用户不存在");
        }
        user.setId(id);
        userMapper.updateById(user);
    }
}
