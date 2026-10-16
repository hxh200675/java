package com.fenglin.springboottest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fenglin.springboottest.entity.Movie;
import com.fenglin.springboottest.entity.MovieSession;

import java.util.List;

public interface MovieService extends IService<Movie> {

    /**
     * 首页影片列表（支持分类筛选、关键词搜索、排序）
     *
     * @param category 分类（"首页" 或 null 表示全部）
     * @param keyword  关键词（匹配片名/类型/分类/地区）
     * @param tab      排序：hot-最热（按评分），new-最新（按年份），其他-综合（按评分）
     * @return 影片列表
     */
    List<Movie> listMovies(String category, String keyword, String tab);

    /** 首页轮播：评分最高的前 3 部 */
    List<Movie> banners();

    /** 某影片的放映场次列表（按放映时间升序） */
    List<MovieSession> listSessions(Long movieId);
}
