package com.cine.auth;

import org.springframework.stereotype.Service;

/**
 * 认证业务实现（桩）。
 *
 * 以下 TODO 为接入真实后端必须补齐的部分：
 *  - 接口限流 / 防刷（同一账号 N 秒内只能发一次）
 *  - 验证码生成后存入 Redis（带 TTL，如 5 分钟），禁止只放内存
 *  - 通过短信网关 / 邮件服务真实下发验证码
 *  - 注册时校验验证码、校验账号唯一性、密码用 BCrypt 加密存储
 *  - 返回 JWT 或 Session Token
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public void sendCode(String account, String channel) {
        // TODO: 1) 校验账号格式；2) 生成 6 位随机码；
        //       3) 存入 Redis（key=verify:code:{account}，TTL=300s）；
        //       4) 通过短信/邮件渠道下发。
        throw new UnsupportedOperationException("sendCode 未实现：请接入 Redis + 短信/邮件服务");
    }

    @Override
    public RegisterResult register(String account, String password, String code, String channel) {
        // TODO: 1) 比对 Redis 中的验证码（错误次数限制）；2) 校验账号是否已存在；
        //       3) 密码 BCrypt 加密落库；4) 生成 Token。
        throw new UnsupportedOperationException("register 未实现：请接入用户表 + 密码加密 + Token 签发");
    }
}
