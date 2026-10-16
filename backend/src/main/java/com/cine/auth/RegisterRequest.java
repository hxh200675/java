package com.cine.auth;

/**
 * 注册请求体。
 */
public class RegisterRequest {
    /** 手机号或邮箱 */
    private String account;
    /** 明文密码（后端需加密存储，禁止明文落库） */
    private String password;
    /** 6 位验证码 */
    private String code;
    /** 下发渠道：sms / email */
    private String channel;

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
}
