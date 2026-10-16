package com.cine.auth;

/**
 * 注册返回数据（与前端 data 字段对齐）。
 */
public class RegisterResult {
    private String userId;
    private String token;

    public RegisterResult() {}

    public RegisterResult(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
