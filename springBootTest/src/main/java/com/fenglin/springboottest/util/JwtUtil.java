package com.fenglin.springboottest.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具：生成与解析 token（HS256）
 * 说明：演示用固定密钥，生产环境应放到配置中心/环境变量，并定期轮换。
 */
@Component
public class JwtUtil {

    /** 至少 256 bit（32 字节）的密钥 */
    private static final String SECRET = "cinema-mumingguangying-jwt-secret-key-2026-very-long-1234567890";

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /** 有效期：2 小时 */
    private static final long EXPIRE_MS = 1000L * 60 * 60 * 2;

    /** 生成 token，subject 存放用户名 */
    public String generateToken(String subject) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + EXPIRE_MS);
        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    /** 解析 token 返回 subject；无效/过期返回 null */
    public String parseSubject(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
