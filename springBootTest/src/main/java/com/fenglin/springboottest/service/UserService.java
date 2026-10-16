package com.fenglin.springboottest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.dto.LoginRequest;
import com.fenglin.springboottest.dto.RegisterRequest;
import com.fenglin.springboottest.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {

    /**
     * 注册用户
     *
     * @param req 注册请求（account / password / confirm）
     * @return 统一响应
     */
    Result<Map<String, Object>> register(RegisterRequest req);

    /**
     * 登录
     *
     * @param req 登录请求（account / password）
     * @return 成功时 data 为不含密码的用户信息
     */
    Result<Map<String, Object>> login(LoginRequest req);

    /**
     * 修改昵称
     *
     * @param username 当前登录用户名
     * @param nickname 新昵称
     * @return 统一响应，data 为更新后的用户信息
     */
    Result<Map<String, Object>> updateNickname(String username, String nickname);

    /**
     * 上传/更新头像
     *
     * @param username 当前登录用户名
     * @param file     头像文件
     * @return 统一响应，data 含新的 avatar 地址
     */
    Result<Map<String, Object>> updateAvatar(String username, org.springframework.web.multipart.MultipartFile file);
}
