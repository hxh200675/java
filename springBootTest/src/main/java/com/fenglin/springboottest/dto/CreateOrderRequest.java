package com.fenglin.springboottest.dto;

import java.util.List;

/**
 * 购票请求：选择场次 + 选座
 */
public class CreateOrderRequest {

    /** 场次 id */
    private Long sessionId;

    /** 选中的座位标签列表，如 ["3排5座", "3排6座"] */
    private List<String> seats;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public List<String> getSeats() {
        return seats;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }
}
