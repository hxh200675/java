package com.fenglin.springboottest.controller;

import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.entity.Movie;
import com.fenglin.springboottest.entity.MovieSession;
import com.fenglin.springboottest.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movie")
public class MovieController {

    @Autowired
    private MovieService movieService;

    /**
     * 首页影片列表（支持分类、搜索、排序）
     * GET /api/movie/list?category=&keyword=&tab=hot|new|all
     */
    @GetMapping("/list")
    public Result<List<Movie>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "all") String tab) {
        return Result.success("ok", movieService.listMovies(category, keyword, tab));
    }

    /**
     * 首页轮播（评分 Top3）
     * GET /api/movie/banners
     */
    @GetMapping("/banners")
    public Result<List<Movie>> banners() {
        return Result.success("ok", movieService.banners());
    }

    /**
     * 影片详情
     * GET /api/movie/{id}
     */
    @GetMapping("/{id}")
    public Result<Movie> detail(@PathVariable Long id) {
        return Result.success("ok", movieService.getById(id));
    }

    /**
     * 某影片的放映场次
     * GET /api/movie/{id}/sessions
     */
    @GetMapping("/{id}/sessions")
    public Result<List<MovieSession>> sessions(@PathVariable Long id) {
        return Result.success("ok", movieService.listSessions(id));
    }
}
