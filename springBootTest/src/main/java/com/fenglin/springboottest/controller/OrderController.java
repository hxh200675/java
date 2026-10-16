package com.fenglin.springboottest.controller;

import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.dto.CreateOrderRequest;
import com.fenglin.springboottest.entity.Order;
import com.fenglin.springboottest.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 购票下单（需登录，JWT 拦截器已将 username 写入 request）
     * POST /api/order/create
     */
    @PostMapping("/create")
    public Result<?> create(@RequestBody CreateOrderRequest req, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return Result.error("未登录或登录已过期");
        }
        return orderService.createOrder(username, req);
    }

    /**
     * 我的订单（需登录）
     * GET /api/order/my
     */
    @GetMapping("/my")
    public Result<List<Order>> my(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return Result.error("未登录或登录已过期");
        }
        return Result.success("ok", orderService.myOrders(username));
    }
}
