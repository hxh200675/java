<script setup>
import { reactive, ref, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const form = reactive({ account: '', password: '', confirm: '' })
const showPwd = ref(false)
const errors = reactive({ account: '', password: '', confirm: '' })

const toast = reactive({ show: false, msg: '', type: 'info' })
let toastTimer = null

function showToast(msg, type = 'info') {
  toast.msg = msg
  toast.type = type
  toast.show = true
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => (toast.show = false), 2600)
}

function validate() {
  errors.account = ''
  errors.password = ''
  errors.confirm = ''
  let ok = true
  const acc = form.account.trim()
  if (!acc) {
    errors.account = '请输入账号'
    ok = false
  } else if (
    !/^1[3-9]\d{9}$/.test(acc) &&
    !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(acc)
  ) {
    errors.account = '请输入手机号或邮箱'
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
  return ok
}

function onSubmit() {
  if (!validate()) {
    showToast('信息有误，请检查', 'error')
    return
  }
  showToast('账号创建成功，请登录', 'success')
  // 真实项目在这里调用注册接口
  console.log('register payload:', { ...form })
  setTimeout(() => router.push('/login'), 900)
}

function onBack() {
  router.push('/login')
}

function onSocial(name) {
  showToast(`使用 ${name} 注册（演示）`, 'info')
}

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
        <span class="field__label">账号</span>
        <div class="field__box" :class="{ 'is-error': errors.account }">
          <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
            <path
              fill="currentColor"
              d="M12 12a5 5 0 1 0 0-10 5 5 0 0 0 0 10Zm0 2c-5 0-9 2.5-9 6v2h18v-2c0-3.5-4-6-9-6Z"
            />
          </svg>
          <input
            v-model="form.account"
            type="text"
            autocomplete="username"
            placeholder="手机号 / 邮箱"
          />
        </div>
        <em class="field__err">{{ errors.account }}</em>
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

      <button type="submit" class="enter">
        <svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
          <path fill="currentColor" d="M8 5v14l11-7z" />
        </svg>
        开通会员
      </button>
    </form>

    <div class="divider"><span>其他开通方式</span></div>

    <div class="socials">
      <button class="social" type="button" aria-label="微信注册" @click="onSocial('微信')">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path
            fill="currentColor"
            d="M9 4C5 4 2 6.7 2 10c0 1.9 1 3.5 2.7 4.6L4 17l2.6-1.3c.8.2 1.6.3 2.4.3h.6A5.6 5.6 0 0 1 9 13c0-3 3-5.3 6.6-5.3h.4C15.4 6 12.4 4 9 4Zm-2 4.2a1 1 0 1 1 0 2 1 1 0 0 1 0-2Zm4 0a1 1 0 1 1 0 2 1 1 0 0 1 0-2Zm9 2.8c0-2.6-2.6-4.7-5.8-4.7S10.4 9.4 10.4 12c0 2.6 2.6 4.7 5.8 4.7.7 0 1.4-.1 2-.3L20 17.5l-.5-1.8c1.4-.9 2.5-2.2 2.5-3.7Zm-7.7-.9a.8.8 0 1 1 0 1.6.8.8 0 0 1 0-1.6Zm3.8 0a.8.8 0 1 1 0 1.6.8.8 0 0 1 0-1.6Z"
          />
        </svg>
      </button>
      <button class="social" type="button" aria-label="Apple 注册" @click="onSocial('Apple')">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path
            fill="currentColor"
            d="M16 3c.1 1.2-.4 2.3-1.1 3.1-.8.9-2 1.5-3.1 1.4-.1-1.2.4-2.4 1.1-3.1C13.7 3.4 15 2.9 16 3Zm3.3 16.3c-.5 1.2-.8 1.7-1.4 2.8-.9 1.4-2.2 3.1-3.8 3.1-1.4 0-1.8-.9-3.7-.9s-2.4.9-3.7.9c-1.6 0-2.9-1.6-3.8-3C.6 18.7-.3 14.6 1.7 11.6c1-1.6 2.6-2.6 4.2-2.6 1.6 0 2.6 1 3.9 1 1.3 0 2-1 3.9-1 1.4 0 2.9.8 3.9 2.1-3.4 1.9-2.9 6.8 1.7 7.7Z"
          />
        </svg>
      </button>
      <button class="social" type="button" aria-label="Google 注册" @click="onSocial('Google')">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path
            fill="#EA4335"
            d="M12 10.2v3.9h5.5c-.2 1.4-1.7 4.1-5.5 4.1-3.3 0-6-2.7-6-6s2.7-6 6-6c1.9 0 3.1.8 3.8 1.5l2.6-2.5C16.7 3.4 14.6 2.5 12 2.5 6.9 2.5 2.8 6.6 2.8 11.7S6.9 20.9 12 20.9c5.3 0 8.8-3.7 8.8-9 0-.6-.1-1.1-.2-1.6H12Z"
          />
        </svg>
      </button>
    </div>

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
