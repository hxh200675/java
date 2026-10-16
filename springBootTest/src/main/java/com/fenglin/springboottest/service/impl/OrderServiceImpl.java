package com.fenglin.springboottest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.dto.CreateOrderRequest;
import com.fenglin.springboottest.entity.Movie;
import com.fenglin.springboottest.entity.MovieSession;
import com.fenglin.springboottest.entity.Order;
import com.fenglin.springboottest.mapper.MovieMapper;
import com.fenglin.springboottest.mapper.MovieSessionMapper;
import com.fenglin.springboottest.mapper.OrderMapper;
import com.fenglin.springboottest.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final MovieSessionMapper sessionMapper;
    private final MovieMapper movieMapper;

    public OrderServiceImpl(MovieSessionMapper sessionMapper, MovieMapper movieMapper) {
        this.sessionMapper = sessionMapper;
        this.movieMapper = movieMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> createOrder(String username, CreateOrderRequest req) {
        Long sessionId = req.getSessionId();
        List<String> seats = req.getSeats();

        if (sessionId == null) {
            return Result.error("请选择观影场次");
        }
        if (seats == null || seats.isEmpty()) {
            return Result.error("请至少选择一个座位");
        }

        MovieSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            return Result.error("场次不存在或已下架");
        }
        if (session.getAvailableSeats() == null || session.getAvailableSeats() <= 0) {
            return Result.error("该场次已售罄");
        }

        int count = seats.size();
        if (count > session.getAvailableSeats()) {
            return Result.error("余票不足，仅剩 " + session.getAvailableSeats() + " 张");
        }

        Movie movie = movieMapper.selectById(session.getMovieId());
        BigDecimal amount = session.getPrice().multiply(BigDecimal.valueOf(count));
        String orderNo = genOrderNo();

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUsername(username);
        order.setMovieId(session.getMovieId());
        order.setMovieTitle(movie != null ? movie.getTitle() : "未知影片");
        order.setSessionId(sessionId);
        order.setCinema(session.getCinema());
        order.setHall(session.getHall());
        order.setShowTime(session.getShowTime());
        order.setSeats(String.join(",", seats));
        order.setSeatCount(count);
        order.setAmount(amount);
        order.setStatus(1);
        this.save(order);

        // 扣减剩余座位
        session.setAvailableSeats(session.getAvailableSeats() - count);
        sessionMapper.updateById(session);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("orderNo", orderNo);
        data.put("movieTitle", order.getMovieTitle());
        data.put("cinema", session.getCinema());
        data.put("hall", session.getHall());
        data.put("showTime", session.getShowTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        data.put("seats", seats);
        data.put("seatCount", count);
        data.put("amount", amount);
        return Result.success("购票成功", data);
    }

    @Override
    public List<Order> myOrders(String username) {
        LambdaQueryWrapper<Order> q = new LambdaQueryWrapper<>();
        q.eq(Order::getUsername, username).orderByDesc(Order::getCreateTime);
        return this.list(q);
    }

    /** 订单号：时间戳 + 4 位随机，保证唯一 */
    private String genOrderNo() {
        long ts = System.currentTimeMillis();
        int rnd = (int) (Math.random() * 9000) + 1000;
        return "M" + ts + rnd;
    }
}
