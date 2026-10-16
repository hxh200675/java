package com.fenglin.springboottest.controller;

import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.entity.User;
import com.fenglin.springboottest.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class MeController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前登录用户信息（需携带有效 token）
     * token 中的账号由 JwtInterceptor 写入 request attribute
     */
    @GetMapping("/me")
    public Result<Map<String, Object>> me(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return Result.error("未登录");
        }
        User user = userService.lambdaQuery().eq(User::getUsername, username).one();
        if (user == null) {
            return Result.error("账号不存在");
        }
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("nickname", user.getNickname());
        info.put("avatar", user.getAvatar());
        info.put("status", user.getStatus());
        return Result.success("ok", info);
    }

    /**
     * 修改昵称（需登录）
     * PUT /api/user/profile  body: { "nickname": "新昵称" }
     */
    @PutMapping("/profile")
    public Result<Map<String, Object>> updateProfile(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return Result.error("未登录或登录已过期");
        }
        return userService.updateNickname(username, body.get("nickname"));
    }

    /**
     * 上传头像（需登录，multipart/form-data，字段名 file）
     * POST /api/user/avatar
     */
    @PostMapping("/avatar")
    public Result<Map<String, Object>> uploadAvatar(@RequestParam("file") MultipartFile file,
                                                     HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return Result.error("未登录或登录已过期");
        }
        return userService.updateAvatar(username, file);
    }
}
