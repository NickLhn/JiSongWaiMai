package com.jswm.controller;

import com.jswm.common.Result;
import com.jswm.entity.SysUser;
import com.jswm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/info")
    public Result<SysUser> getUserInfo(@RequestHeader("Authorization") String token) {
        Long userId = com.jswm.utils.JwtUtils.getUserId(token);
        SysUser user = userService.getUserInfo(userId);
        return Result.success(user);
    }

    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestHeader("Authorization") String token, @RequestBody SysUser user) {
        Long userId = com.jswm.utils.JwtUtils.getUserId(token);
        userService.updateUserInfo(userId, user);
        return Result.success("更新成功", null);
    }
}
