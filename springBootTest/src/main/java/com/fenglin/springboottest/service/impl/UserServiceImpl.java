package com.fenglin.springboottest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.dto.LoginRequest;
import com.fenglin.springboottest.dto.RegisterRequest;
import com.fenglin.springboottest.entity.User;
import com.fenglin.springboottest.mapper.UserMapper;
import com.fenglin.springboottest.service.CaptchaService;
import com.fenglin.springboottest.service.UserService;
import com.fenglin.springboottest.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /** 头像上传目录（相对于后端工作目录） */
    private static final String UPLOAD_DIR = "uploads";
    /** 允许的图片扩展名 */
    private static final Set<String> ALLOWED_EXT = Set.of("png", "jpg", "jpeg", "gif", "webp", "bmp");

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CaptchaService captchaService;

    @Override
    public Result<Map<String, Object>> register(RegisterRequest req) {
        String username = req.getUsername() == null ? "" : req.getUsername().trim();
        String password = req.getPassword();
        String confirm = req.getConfirm();
        String captchaKey = req.getCaptchaKey();
        String captchaCode = req.getCaptchaCode();

        // 0. 验证码校验（防刷，优先于其他校验）
        if (!captchaService.validate(captchaKey, captchaCode)) {
            return Result.error("验证码错误或已过期，请刷新重试");
        }

        // 1. 基础校验
        if (username.isEmpty()) {
            return Result.error("请输入用户名");
        }
        if (!isValidUsername(username)) {
            return Result.error("用户名格式不正确（4-20 位字母、数字或下划线，以字母开头）");
        }
        if (password == null || password.length() < 6) {
            return Result.error("密码至少 6 位");
        }
        if (confirm == null || !confirm.equals(password)) {
            return Result.error("两次输入的密码不一致");
        }

        // 2. 用户名唯一性校验
        long count = this.lambdaQuery().eq(User::getUsername, username).count();
        if (count > 0) {
            return Result.error("该用户名已注册，请直接登录");
        }

        // 3. 持久化（密码 BCrypt 加密）
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(1);
        // 昵称默认取用户名，便于前端展示，可后续修改
        user.setNickname(username);
        this.save(user);

        // 注册成功后直接签发 token，实现「注册即登录」（返回结构与 login 一致）
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("token", jwtUtil.generateToken(user.getUsername()));
        data.put("user", buildUserInfo(user));
        return Result.success("注册成功", data);
    }

    @Override
    public Result<Map<String, Object>> login(LoginRequest req) {
        String username = req.getUsername() == null ? "" : req.getUsername().trim();
        String password = req.getPassword();
        String captchaKey = req.getCaptchaKey();
        String captchaCode = req.getCaptchaCode();

        // 0. 验证码校验（防刷，优先于其他校验）
        if (!captchaService.validate(captchaKey, captchaCode)) {
            return Result.error("验证码错误或已过期，请刷新重试");
        }

        if (username.isEmpty()) {
            return Result.error("请输入用户名");
        }

        // 1. 用户名是否存在
        User user = this.lambdaQuery().eq(User::getUsername, username).one();
        if (user == null) {
            return Result.error("账号不存在");
        }

        // 2. 账号状态（1-正常 0-禁用）
        if (user.getStatus() != null && user.getStatus() == 0) {
            return Result.error("账号已被禁用，请联系管理员");
        }

        // 3. 密码校验（BCrypt 比对）
        if (password == null || !passwordEncoder.matches(password, user.getPassword())) {
            return Result.error("密码错误");
        }

        // 4. 生成 token，并返回不含密码的用户信息
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("token", jwtUtil.generateToken(user.getUsername()));
        data.put("user", buildUserInfo(user));
        return Result.success("登录成功", data);
    }

    @Override
    public Result<Map<String, Object>> updateNickname(String username, String nickname) {
        String nick = nickname == null ? "" : nickname.trim();
        if (nick.isEmpty()) {
            return Result.error("昵称不能为空");
        }
        if (nick.length() > 20) {
            return Result.error("昵称不能超过 20 个字符");
        }
        User user = this.lambdaQuery().eq(User::getUsername, username).one();
        if (user == null) {
            return Result.error("账号不存在");
        }
        user.setNickname(nick);
        this.updateById(user);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("user", buildUserInfo(user));
        return Result.success("昵称修改成功", data);
    }

    @Override
    public Result<Map<String, Object>> updateAvatar(String username, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("请选择要上传的图片");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只支持上传图片文件");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.error("图片不能超过 5MB");
        }

        User user = this.lambdaQuery().eq(User::getUsername, username).one();
        if (user == null) {
            return Result.error("账号不存在");
        }

        // 扩展名（白名单）
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            ext = original.substring(dot + 1).toLowerCase();
        }
        if (!ALLOWED_EXT.contains(ext)) {
            ext = "png";
        }

        try {
            Path dir = Paths.get(UPLOAD_DIR).toAbsolutePath();
            Files.createDirectories(dir);
            String filename = "avatar_" + user.getId() + "_" + System.currentTimeMillis() + "." + ext;
            Path target = dir.resolve(filename);
            file.transferTo(target.toFile());

            String avatarUrl = "/uploads/" + filename;
            user.setAvatar(avatarUrl);
            this.updateById(user);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("avatar", avatarUrl);
            data.put("user", buildUserInfo(user));
            return Result.success("头像上传成功", data);
        } catch (IOException e) {
            return Result.error("头像保存失败：" + e.getMessage());
        }
    }

    /** 统一构造返回给前端的用户信息（不含密码） */
    private Map<String, Object> buildUserInfo(User user) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("nickname", user.getNickname());
        info.put("avatar", user.getAvatar());
        info.put("status", user.getStatus());
        return info;
    }

    /** 校验用户名为 4-20 位字母/数字/下划线，且以字母开头 */
    private boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z][a-zA-Z0-9_]{3,19}$");
    }
}
