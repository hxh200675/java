/**
 * 影片 / 购票 相关 API 封装
 * 后端：springBootTest（/api 由 vite 代理到 http://localhost:8081）
 */

import { request } from './auth'

/** 首页影片列表（分类 / 搜索 / 排序） */
export async function fetchMovies(params = {}) {
  const qs = new URLSearchParams()
  if (params.category) qs.set('category', params.category)
  if (params.keyword) qs.set('keyword', params.keyword)
  if (params.tab) qs.set('tab', params.tab)
  const res = await request(`/api/movie/list?${qs.toString()}`)
  if (res.code !== 200) throw new Error(res.message || '加载影片失败')
  return res.data || []
}

/** 首页轮播（评分 Top3） */
export async function fetchBanners() {
  const res = await request('/api/movie/banners')
  if (res.code !== 200) throw new Error(res.message || '加载轮播失败')
  return res.data || []
}

/** 某影片的放映场次 */
export async function fetchSessions(movieId) {
  const res = await request(`/api/movie/${movieId}/sessions`)
  if (res.code !== 200) throw new Error(res.message || '加载场次失败')
  return res.data || []
}

/** 购票下单（需登录） */
export async function createOrder(payload) {
  return request('/api/order/create', { method: 'POST', body: JSON.stringify(payload) })
}

/** 我的订单（需登录） */
export async function fetchMyOrders() {
  const res = await request('/api/order/my')
  if (res.code !== 200) throw new Error(res.message || '加载订单失败')
  return res.data || []
}
