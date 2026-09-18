<script setup>
import { ref, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const movies = [
  { title: '星际回响', tag: '科幻', score: 9.1, hue: 280 },
  { title: '雾港旧梦', tag: '悬疑', score: 8.4, hue: 200 },
  { title: '盛夏告白', tag: '爱情', score: 8.8, hue: 340 },
  { title: '荒野猎手', tag: '动作', score: 8.0, hue: 30 },
  { title: '深海之歌', tag: '动画', score: 9.3, hue: 190 },
  { title: '霓虹迷城', tag: '犯罪', score: 7.9, hue: 250 },
]

const toast = ref({ show: false, msg: '' })
let toastTimer = null

function showToast(msg) {
  toast.value = { show: true, msg }
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => (toast.value = { show: false, msg: '' }), 2200)
}

function onPick(m) {
  showToast(`已选《${m.title}》，前往选座（演示）`)
}

function onLogout() {
  showToast('已退出登录')
  setTimeout(() => router.push('/login'), 700)
}

onBeforeUnmount(() => clearTimeout(toastTimer))
</script>

<template>
  <div class="home">
    <header class="home__bar">
      <div class="home__brand">
        <svg viewBox="0 0 64 64" width="30" height="30" aria-hidden="true">
          <rect x="6" y="10" width="52" height="16" rx="3" fill="#f5c542" />
          <path
            d="M6 13l52 10M16 10l-6 16M30 11l-6 16M44 12l-6 16M58 13l-6 16"
            stroke="#1a1320"
            stroke-width="2"
          />
          <rect x="30" y="26" width="4" height="30" rx="2" fill="#f5c542" />
        </svg>
        <span>幕幕光影</span>
      </div>
      <button class="home__logout" type="button" @click="onLogout">退出登录</button>
    </header>

    <section class="home__hero">
      <h2>欢迎回到你的私人影院</h2>
      <p>今夜片单已备好，挑一部开始放映吧。</p>
    </section>

    <section class="home__section">
      <h3>正在热映</h3>
      <div class="grid">
        <article
          v-for="m in movies"
          :key="m.title"
          class="card"
          :style="{ '--hue': m.hue }"
          @click="onPick(m)"
        >
          <div class="card__poster">
            <span class="card__score">{{ m.score }}</span>
            <span class="card__tag">{{ m.tag }}</span>
          </div>
          <div class="card__meta">
            <h4>{{ m.title }}</h4>
            <span class="card__cta">选座购票</span>
          </div>
        </article>
      </div>
    </section>

    <transition name="toast">
      <div v-if="toast.show" class="toast" role="status" aria-live="polite">
        {{ toast.msg }}
      </div>
    </transition>
  </div>
</template>

<style scoped>
.home {
  width: min(960px, 100%);
  margin: 0 auto;
  padding: 26px 24px 60px;
}

.home__bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.home__brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  letter-spacing: 3px;
  font-weight: 600;
  background: linear-gradient(90deg, var(--gold-soft), var(--gold));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}
.home__logout {
  padding: 9px 18px;
  border-radius: 999px;
  border: 1px solid var(--line);
  background: rgba(255, 255, 255, 0.05);
  color: var(--ink);
  cursor: pointer;
  font-size: 13px;
  transition: border-color 0.2s, background 0.2s;
}
.home__logout:hover {
  border-color: var(--gold);
  background: rgba(245, 197, 66, 0.12);
}

.home__hero {
  margin: 40px 0 30px;
}
.home__hero h2 {
  margin: 0;
  font-size: 30px;
  letter-spacing: 2px;
}
.home__hero p {
  margin: 10px 0 0;
  color: var(--muted);
}

.home__section h3 {
  margin: 0 0 18px;
  font-size: 18px;
  letter-spacing: 1px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 18px;
}
.card {
  border-radius: 16px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  cursor: pointer;
  transition: transform 0.18s, border-color 0.2s, box-shadow 0.2s;
}
.card:hover {
  transform: translateY(-4px);
  border-color: var(--gold);
  box-shadow: 0 16px 34px rgba(0, 0, 0, 0.45);
}
.card__poster {
  position: relative;
  height: 200px;
  background: linear-gradient(
    150deg,
    hsl(var(--hue), 60%, 32%),
    hsl(calc(var(--hue) + 40), 55%, 18%)
  );
}
.card__poster::after {
  content: "";
  position: absolute;
  inset: 0;
  background: radial-gradient(
    80% 60% at 30% 20%,
    rgba(255, 255, 255, 0.18),
    transparent 60%
  );
}
.card__score {
  position: absolute;
  top: 10px;
  left: 10px;
  font-size: 18px;
  font-weight: 700;
  color: var(--gold-soft);
  text-shadow: 0 2px 6px rgba(0, 0, 0, 0.5);
}
.card__tag {
  position: absolute;
  bottom: 10px;
  right: 10px;
  padding: 3px 10px;
  font-size: 12px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
}
.card__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
}
.card__meta h4 {
  margin: 0;
  font-size: 15px;
}
.card__cta {
  font-size: 12px;
  color: var(--gold-soft);
}

.toast {
  position: fixed;
  left: 50%;
  bottom: 42px;
  transform: translateX(-50%);
  padding: 12px 22px;
  border-radius: 999px;
  font-size: 14px;
  color: #fff;
  background: rgba(20, 14, 30, 0.92);
  border: 1px solid var(--line);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.5);
  z-index: 20;
}
.toast-enter-active,
.toast-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}
.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(12px);
}
</style>
