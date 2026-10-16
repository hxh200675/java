package com.fenglin.springboottest.controller;

import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    /**
     * 获取数字图形验证码
     * GET /api/auth/captcha
     * 返回：{ "code": 200, "data": { "captchaKey": "...", "image": "base64 png" } }
     */
    @GetMapping("/captcha")
    public Result<CaptchaService.CaptchaVO> captcha() {
        return Result.success("ok", captchaService.generate());
    }
}
