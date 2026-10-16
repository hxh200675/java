package com.fenglin.springboottest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fenglin.springboottest.entity.Movie;
import com.fenglin.springboottest.entity.MovieSession;
import com.fenglin.springboottest.mapper.MovieMapper;
import com.fenglin.springboottest.mapper.MovieSessionMapper;
import com.fenglin.springboottest.service.MovieService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl extends ServiceImpl<MovieMapper, Movie> implements MovieService {

    private final MovieSessionMapper sessionMapper;

    public MovieServiceImpl(MovieSessionMapper sessionMapper) {
        this.sessionMapper = sessionMapper;
    }

    @Override
    public List<Movie> listMovies(String category, String keyword, String tab) {
        LambdaQueryWrapper<Movie> q = new LambdaQueryWrapper<>();

        if (category != null && !category.isBlank() && !"首页".equals(category)) {
            q.eq(Movie::getCategory, category);
        }
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            q.and(w -> w.like(Movie::getTitle, kw)
                    .or().like(Movie::getGenre, kw)
                    .or().like(Movie::getCategory, kw)
                    .or().like(Movie::getRegion, kw));
        }

        if ("new".equals(tab)) {
            q.orderByDesc(Movie::getYear);
        } else {
            // hot 和默认都按评分降序（评分高的排前面）
            q.orderByDesc(Movie::getScore);
        }
        return this.list(q);
    }

    @Override
    public List<Movie> banners() {
        LambdaQueryWrapper<Movie> q = new LambdaQueryWrapper<>();
        q.orderByDesc(Movie::getScore).last("limit 3");
        return this.list(q);
    }

    @Override
    public List<MovieSession> listSessions(Long movieId) {
        LambdaQueryWrapper<MovieSession> q = new LambdaQueryWrapper<>();
        q.eq(MovieSession::getMovieId, movieId).orderByAsc(MovieSession::getShowTime);
        return sessionMapper.selectList(q);
    }
}
