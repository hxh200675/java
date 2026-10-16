package com.cine.auth;

/**
 * 认证业务逻辑接口（预留）。
 */
public interface AuthService {

    /**
     * 生成并向用户下发注册验证码。
     *
     * @param account 手机号或邮箱
     * @param channel sms / email
     */
    void sendCode(String account, String channel);

    /**
     * 校验验证码并完成注册。
     *
     * @return 注册结果（用户标识、令牌等）
     */
    RegisterResult register(String account, String password, String code, String channel);
}
