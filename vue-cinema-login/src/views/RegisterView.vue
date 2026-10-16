<script setup>
import { reactive, ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { fetchCaptcha, register, setAuth } from '../api/auth'

const router = useRouter()

const form = reactive({ username: '', password: '', confirm: '', captchaCode: '' })
const showPwd = ref(false)
const submitting = ref(false)
const errors = reactive({ username: '', password: '', confirm: '', captchaCode: '' })

const captcha = reactive({ key: '', image: '' })
let captchaSeq = 0

const toast = reactive({ show: false, msg: '', type: 'info' })
let toastTimer = null

function showToast(msg, type = 'info') {
  toast.msg = msg
  toast.type = type
  toast.show = true
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => (toast.show = false), 2600)
}

async function refreshCaptcha() {
  const seq = ++captchaSeq
  form.captchaCode = ''
  try {
    const data = await fetchCaptcha()
    if (seq !== captchaSeq) return // 防止并发刷新时旧响应覆盖新图
    captcha.key = data.captchaKey
    captcha.image = `data:image/png;base64,${data.image}`
  } catch (e) {
    showToast(e.message || '验证码加载失败，请点击图片重试', 'error')
  }
}

function validate() {
  errors.username = ''
  errors.password = ''
  errors.confirm = ''
  errors.captchaCode = ''
  let ok = true
  const name = form.username.trim()
  if (!name) {
    errors.username = '请输入用户名'
    ok = false
  } else if (!/^[a-zA-Z][a-zA-Z0-9_]{3,19}$/.test(name)) {
    errors.username = '用户名需 4-20 位字母、数字或下划线，且以字母开头'
    ok = false
  }
  if (!form.password) {
    errors.password = '请设置密码'
    ok = false
  } else if (form.password.length < 6) {
    errors.password = '密码至少 6 位'
    ok = false
  }
  if (!form.confirm) {
    errors.confirm = '请再次输入密码'
    ok = false
  } else if (form.confirm !== form.password) {
    errors.confirm = '两次密码不一致'
    ok = false
  }
  if (!form.captchaCode.trim()) {
    errors.captchaCode = '请输入验证码'
    ok = false
  }
  return ok
}

async function onSubmit() {
  if (submitting.value) return
  if (!validate()) {
    showToast('信息有误，请检查', 'error')
    return
  }
  submitting.value = true
  try {
    const res = await register({
      username: form.username.trim(),
      password: form.password,
      confirm: form.confirm,
      captchaKey: captcha.key,
      captchaCode: form.captchaCode.trim(),
    })
    if (res.code !== 200) {
      showToast(res.message || '注册失败', 'error')
      refreshCaptcha() // 验证码一次性使用，失败后换新
      return
    }
    // 注册成功后端直接返回 token，前端自动登录并跳转首页
    const { token, user } = res.data || {}
    if (token && user) {
      setAuth(token, user)
      showToast('注册成功，已自动登录', 'success')
      setTimeout(() => router.push('/'), 1000)
    } else {
      showToast('账号创建成功，请登录', 'success')
      setTimeout(() => router.push('/login'), 900)
    }
  } finally {
    submitting.value = false
  }
}

function onBack() {
  router.push('/login')
}

onMounted(refreshCaptcha)
onBeforeUnmount(() => clearTimeout(toastTimer))
</script>

<template>
  <section class="ticket" role="form" aria-label="注册">
    <span class="notch notch--t"></span>
    <span class="notch notch--b"></span>

    <div class="ticket__brand">
      <svg class="clapper" viewBox="0 0 64 64" width="46" height="46" aria-hidden="true">
        <g class="clapper__top">
          <rect x="6" y="10" width="52" height="16" rx="3" fill="#f5c542" />
          <path
            d="M6 13l52 10M16 10l-6 16M30 11l-6 16M44 12l-6 16M58 13l-6 16"
            stroke="#1a1320"
            stroke-width="2"
          />
        </g>
        <rect class="clapper__stick" x="30" y="26" width="4" height="30" rx="2" fill="#f5c542" />
      </svg>
      <div class="brand__text">
        <h1>幕幕光影</h1>
        <p>CINÉ · 购票即开通会员</p>
      </div>
    </div>

    <p class="ticket__hint">新观众请先取票，开启你的私人影院。</p>

    <form novalidate @submit.prevent="onSubmit">
      <label class="field">
        <span class="field__label">用户名</span>
        <div class="field__box" :class="{ 'is-error': errors.username }">
          <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
            <path
              fill="currentColor"
              d="M12 12a5 5 0 1 0 0-10 5 5 0 0 0 0 10Zm0 2c-5 0-9 2.5-9 6v2h18v-2c0-3.5-4-6-9-6Z"
            />
          </svg>
          <input
            v-model="form.username"
            type="text"
            autocomplete="username"
            placeholder="用户名（4-20 位字母、数字或下划线）"
          />
        </div>
        <em class="field__err">{{ errors.username }}</em>
      </label>

      <label class="field">
        <span class="field__label">密码</span>
        <div class="field__box" :class="{ 'is-error': errors.password }">
          <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
            <path
              fill="currentColor"
              d="M17 9V7a5 5 0 0 0-10 0v2H5v13h14V9h-2Zm-8 0V7a3 3 0 0 1 6 0v2H9Zm3 4a2 2 0 0 1 1 3.7V19h-2v-2.3A2 2 0 0 1 12 13Z"
            />
          </svg>
          <input
            v-model="form.password"
            :type="showPwd ? 'text' : 'password'"
            autocomplete="new-password"
            placeholder="设置密码（至少 6 位）"
          />
          <button
            type="button"
            class="eye"
            :class="{ 'eye--on': showPwd }"
            aria-label="显示密码"
            @click="showPwd = !showPwd"
          >
            <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
              <path
                fill="currentColor"
                d="M12 5c5 0 9 4.5 10 7-1 2.5-5 7-10 7S3 14.5 2 12c1-2.5 5-7 10-7Zm0 3a4 4 0 1 0 0 8 4 4 0 0 0 0-8Z"
              />
            </svg>
          </button>
        </div>
        <em class="field__err">{{ errors.password }}</em>
      </label>

      <label class="field">
        <span class="field__label">确认密码</span>
        <div class="field__box" :class="{ 'is-error': errors.confirm }">
          <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
            <path
              fill="currentColor"
              d="M17 9V7a5 5 0 0 0-10 0v2H5v13h14V9h-2Zm-8 0V7a3 3 0 0 1 6 0v2H9Zm3 4a2 2 0 0 1 1 3.7V19h-2v-2.3A2 2 0 0 1 12 13Z"
            />
          </svg>
          <input
            v-model="form.confirm"
            :type="showPwd ? 'text' : 'password'"
            autocomplete="new-password"
            placeholder="请再次输入密码"
          />
        </div>
        <em class="field__err">{{ errors.confirm }}</em>
      </label>

      <label class="field">
        <span class="field__label">验证码</span>
        <div class="captcha-row">
          <div class="field__box captcha-row__input" :class="{ 'is-error': errors.captchaCode }">
            <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
              <path
                fill="currentColor"
                d="M12 1 3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4Zm-1 15-4-4 1.4-1.4L11 13.17l5.6-5.59L18 9l-7 7Z"
              />
            </svg>
            <input
              v-model="form.captchaCode"
              type="text"
              inputmode="numeric"
              maxlength="4"
              autocomplete="off"
              placeholder="图中 4 位数字"
            />
          </div>
          <button
            type="button"
            class="captcha-img"
            title="看不清？点击刷新"
            aria-label="刷新验证码"
            @click="refreshCaptcha"
          >
            <img v-if="captcha.image" :src="captcha.image" alt="验证码" />
            <span v-else>加载中…</span>
          </button>
        </div>
        <em class="field__err">{{ errors.captchaCode }}</em>
      </label>

      <button type="submit" class="enter" :disabled="submitting">
        <svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
          <path fill="currentColor" d="M8 5v14l11-7z" />
        </svg>
        {{ submitting ? '开通中…' : '开通会员' }}
      </button>
    </form>

    <p class="signup">已有账号？<a href="#" @click.prevent="onBack">返回登录</a></p>

    <transition name="toast">
      <div
        v-if="toast.show"
        class="toast"
        :class="`toast--${toast.type}`"
        role="status"
        aria-live="polite"
      >
        {{ toast.msg }}
      </div>
    </transition>
  </section>
</template>
