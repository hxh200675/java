package com.fenglin.springboottest.dto;

/**
 * 登录请求
 */
public class LoginRequest {

    /** 用户名 */
    private String username;

    /** 明文密码 */
    private String password;

    /** 验证码 key（前端从 /api/auth/captcha 获取） */
    private String captchaKey;

    /** 用户输入的验证码 */
    private String captchaCode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptchaKey() {
        return captchaKey;
    }

    public void setCaptchaKey(String captchaKey) {
        this.captchaKey = captchaKey;
    }

    public String getCaptchaCode() {
        return captchaCode;
    }

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }
}
