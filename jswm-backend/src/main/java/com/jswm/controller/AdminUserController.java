package com.jswm.controller;

import com.jswm.common.Result;
import com.jswm.entity.SysUser;
import com.jswm.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/admin/users")
public class AdminUserController {

    @Autowired
    private SysUserMapper userMapper;

    @GetMapping
    public Result<Map<String, Object>> getUserList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        // 获取所有用户
        List<SysUser> allUsers = userMapper.selectAll();

        // 过滤
        List<SysUser> filteredUsers = allUsers.stream()
                .filter(u -> {
                    // 关键词搜索
                    if (keyword != null && !keyword.isEmpty()) {
                        boolean matchName = u.getRealName() != null && u.getRealName().contains(keyword);
                        boolean matchPhone = u.getPhone() != null && u.getPhone().contains(keyword);
                        boolean matchUsername = u.getUsername() != null && u.getUsername().contains(keyword);
                        if (!matchName && !matchPhone && !matchUsername) {
                            return false;
                        }
                    }
                    // 角色筛选
                    if (role != null && !role.equals(u.getRole())) {
                        return false;
                    }
                    // 状态筛选
                    if (status != null && !status.equals(u.getStatus())) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        // 分页
        int total = filteredUsers.size();
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<SysUser> pageData = start < total ? filteredUsers.subList(start, end) : List.of();

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageData);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);

        return Result.success(result);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userMapper.deleteById(id);
        return Result.success();
    }
}
