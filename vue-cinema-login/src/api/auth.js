/**
 * 认证相关 API 封装
 * 后端：springBootTest（/api 由 vite 代理到 http://localhost:8081）
 * 统一响应：{ code: 200 成功 | 其他失败, message, data }
 */

const TOKEN_KEY = 'cinema_token'
const USER_KEY = 'cinema_user'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function getUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  } catch {
    return null
  }
}

export function setAuth(token, user) {
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(USER_KEY, JSON.stringify(user || null))
}

export function clearAuth() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}

/** 统一请求：自动携带 token，返回 { code, message, data } */
export async function request(url, options = {}) {
  const headers = { ...(options.headers || {}) }
  // FormData 由浏览器自动设置 Content-Type（含 boundary），不能手动指定 JSON 头
  if (!(options.body instanceof FormData)) {
    headers['Content-Type'] = 'application/json'
  }
  const token = getToken()
  if (token) headers.Authorization = `Bearer ${token}`

  let resp
  try {
    resp = await fetch(url, { ...options, headers })
  } catch {
    return { code: -1, message: '无法连接服务器，请确认后端已启动', data: null }
  }

  const body = await resp.json().catch(() => null)
  if (!body) {
    return { code: resp.status, message: `请求失败（HTTP ${resp.status}）`, data: null }
  }
  return body
}

/** 获取图形验证码：返回 { captchaKey, image(base64) } */
export async function fetchCaptcha() {
  const res = await request('/api/auth/captcha')
  if (res.code !== 200) throw new Error(res.message || '获取验证码失败')
  return res.data
}

/** 注册：{ username, password, confirm, captchaKey, captchaCode } */
export function register(payload) {
  return request('/api/auth/register', { method: 'POST', body: JSON.stringify(payload) })
}

/** 登录：{ username, password, captchaKey, captchaCode } -> data: { token, user } */
export function login(payload) {
  return request('/api/auth/login', { method: 'POST', body: JSON.stringify(payload) })
}

/** 当前登录用户信息（需 token） */
export function fetchMe() {
  return request('/api/user/me')
}

/** 修改昵称（需登录）：PUT /api/user/profile，body { nickname } */
export function updateProfile(nickname) {
  return request('/api/user/profile', {
    method: 'PUT',
    body: JSON.stringify({ nickname }),
  })
}

/** 上传头像（需登录）：POST /api/user/avatar，multipart/form-data 字段名 file */
export function uploadAvatar(file) {
  const fd = new FormData()
  fd.append('file', file)
  return request('/api/user/avatar', { method: 'POST', body: fd })
}

/** 退出登录（JWT 无状态，前端清除 token 即可） */
export function logout() {
  return request('/api/auth/logout', { method: 'POST' })
}
