package com.fenglin.springboottest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fenglin.springboottest.entity.MovieSession;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MovieSessionMapper extends BaseMapper<MovieSession> {
}
