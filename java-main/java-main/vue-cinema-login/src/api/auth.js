// 认证相关后端接口封装（注册 / 验证码）
//
// 接口契约（与 Java 后端约定）：
//   POST /api/auth/send-code  { account, channel }            -> { code:0, message, data:null }
//   POST /api/auth/register   { account, password, code, channel } -> { code:0, message, data:{ userId, token } }
//
// 切换真实后端：
//   1. 由环境变量注入地址：VITE_API_BASE（默认 /api）
//   2. 关闭演示降级：VITE_API_DEMO=false（默认开启，便于后端未就绪时前端自测）
//   例如 .env.local 写入：VITE_API_BASE=/api\nVITE_API_DEMO=false

const API_BASE = import.meta.env.VITE_API_BASE || '/api'
// 演示模式默认开启：后端未就绪时前端自动模拟接口返回，保证流程可走通
const DEMO_MODE = import.meta.env.VITE_API_DEMO !== 'false'

const delay = (ms) => new Promise((r) => setTimeout(r, ms))

// 统一请求封装：兼容 Spring Boot 通用返回体 { code, message, data }
async function request(path, body) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  })
  const data = await res.json().catch(() => ({}))
  if (!res.ok || (data && data.code !== undefined && data.code !== 0)) {
    throw new Error((data && data.message) || `请求失败 (${res.status})`)
  }
  return data
}

// 演示模式下记录本次生成的验证码，用于本地校验（后端接入后此逻辑失效）
let demoCode = ''

/**
 * 发送注册验证码
 * @param {string} account 手机号或邮箱
 * @param {'sms'|'email'} channel 下发渠道（由账号格式推断）
 */
export async function sendRegisterCode(account, channel) {
  if (DEMO_MODE) {
    demoCode = String(Math.floor(100000 + Math.random() * 900000))
    // 真实环境验证码只通过短信/邮件下发，此处仅便于演示
    console.info(`[demo] 验证码已生成（仅演示）：${demoCode} -> ${account}`)
    await delay(400)
    return { code: 0, message: 'ok', data: null, demo: true, devCode: demoCode }
  }
  return request('/auth/send-code', { account, channel })
}

/**
 * 提交注册
 * @param {{account:string, password:string, code:string, channel:'sms'|'email'}} payload
 */
export async function registerAccount(payload) {
  if (DEMO_MODE) {
    await delay(500)
    if (String(payload.code) !== demoCode) {
      throw new Error('验证码错误（演示码见控制台）')
    }
    demoCode = ''
    return { code: 0, message: 'ok', data: { userId: 'u' + Date.now(), token: '' }, demo: true }
  }
  return request('/auth/register', payload)
}
