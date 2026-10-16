package com.fenglin.springboottest.dto;

/**
 * 注册请求参数，对应前端注册表单：
 * { username, password, confirm }
 */
public class RegisterRequest {

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 确认密码 */
    private String confirm;

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

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
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
