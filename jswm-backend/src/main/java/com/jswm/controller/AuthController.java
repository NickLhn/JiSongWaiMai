package com.jswm.controller;

import com.jswm.common.Result;
import com.jswm.dto.request.LoginRequest;
import com.jswm.dto.request.RegisterRequest;
import com.jswm.dto.response.LoginResponse;
import com.jswm.entity.SysUser;
import com.jswm.service.UserService;
import com.jswm.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        SysUser user = userService.login(request.getUsername(), request.getPassword());
        String token = JwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setRole(user.getRole());
        return Result.success(response);
    }

    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterRequest request) {
        userService.register(request.getUsername(), request.getPassword(), request.getPhone(), request.getRealName());
        return Result.success("注册成功", null);
    }
}
