package com.cine.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 影院会员认证服务（Spring Boot）启动类。
 * 仅提供注册 + 验证码相关预留接口骨架，业务实现处均标注 TODO。
 */
@SpringBootApplication
public class CineAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(CineAuthApplication.class, args);
    }
}
