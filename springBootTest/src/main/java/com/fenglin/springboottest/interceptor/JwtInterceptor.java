package com.fenglin.springboottest.interceptor;

import com.fenglin.springboottest.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * JWT 鉴权拦截器：
 * - 拦截所有 /api/**（登录、注册在 WebMvcConfig 中放行）
 * - 要求请求头携带 Authorization: Bearer <token>
 * - 校验通过后将用户名写入 request attribute，供 Controller 使用
 * - 校验失败返回 401 JSON
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 双保险：登录/注册直接放行
        String uri = request.getRequestURI();
        if ("/api/auth/login".equals(uri) || "/api/auth/register".equals(uri)) {
            return true;
        }

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            writeUnauthorized(response);
            return false;
        }

        String token = auth.substring(7).trim();
        String username = jwtUtil.parseSubject(token);
        if (username == null) {
            writeUnauthorized(response);
            return false;
        }

        request.setAttribute("username", username);
        return true;
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"未登录或登录已过期\",\"data\":null}");
    }
}
