package com.fenglin.springboottest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.dto.CreateOrderRequest;
import com.fenglin.springboottest.entity.Order;

import java.util.List;
import java.util.Map;

public interface OrderService extends IService<Order> {

    /**
     * 购票下单
     *
     * @param username 当前登录用户名（来自 JWT 拦截器写入 request 的属性）
     * @param req      场次 id + 选座列表
     * @return 统一响应，data 为订单信息
     */
    Result<Map<String, Object>> createOrder(String username, CreateOrderRequest req);

    /** 当前用户的订单列表（按下单时间倒序） */
    List<Order> myOrders(String username);
}
