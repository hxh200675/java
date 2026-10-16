package com.cine.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口（预留，与前端 vue-cinema-login 注册页对齐）。
 *
 * 路由前缀 /api 由前端 vite 代理（dev）或网关统一添加。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 发送注册验证码。
     * POST /api/auth/send-code
     */
    @PostMapping("/send-code")
    public Result<Void> sendCode(@RequestBody SendCodeRequest req) {
        authService.sendCode(req.getAccount(), req.getChannel());
        return Result.ok();
    }

    /**
     * 提交注册。
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public Result<RegisterResult> register(@RequestBody RegisterRequest req) {
        RegisterResult result = authService.register(
                req.getAccount(), req.getPassword(), req.getCode(), req.getChannel());
        return Result.ok(result);
    }
}
