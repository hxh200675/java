package com.fenglin.springboottest.controller;

import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.dto.LoginRequest;
import com.fenglin.springboottest.dto.RegisterRequest;
import com.fenglin.springboottest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 注册接口
     * 前端 POST /api/auth/register
     * body: { "username": "用户名", "password": "密码", "confirm": "确认密码" }
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody RegisterRequest req) {
        return userService.register(req);
    }

    /**
     * 登录接口
     * 前端 POST /api/auth/login
     * body: { "username": "用户名", "password": "密码" }
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest req) {
        return userService.login(req);
    }

    /**
     * 退出登录（JWT 无状态，前端丢弃 token 即可；此接口用于约定动作）
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success("已退出登录");
    }
}
