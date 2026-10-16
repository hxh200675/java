<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRouter } from 'vue-router'
import { fetchMe, logout, clearAuth, getUser, updateProfile, uploadAvatar } from '../api/auth'
import { fetchMovies, fetchBanners, fetchSessions, createOrder, fetchMyOrders } from '../api/movie'

const router = useRouter()

const user = ref(getUser())

// ===================== 顶部导航（分类筛选） =====================
const categories = ['首页', '电影', '电视剧', '综艺', '动漫', '纪录片']
const activeCat = ref('首页')

// ===================== 搜索 =====================
const keyword = ref('')

// ===================== 排序 Tab =====================
const tabs = [
  { key: 'all', label: '综合' },
  { key: 'hot', label: '最热' },
  { key: 'new', label: '最新' },
]
const activeTab = ref('all')

// ===================== 数据（后端驱动） =====================
const banners = ref([])
const movies = ref([])
const loading = ref(false)

// ===================== 横幅轮播 =====================
const current = ref(0)
let bannerTimer = null

// ===================== 海报图片（按 id 映射 src/assets/posters/movie-N.png） =====================
const posters = import.meta.glob('../assets/posters/movie-*.png', { eager: true, import: 'default' })
const posterMap = {}
for (const path in posters) {
  const mm = path.match(/movie-(\d+)\.png/)
  if (mm) posterMap[mm[1]] = posters[path]
}

// ===================== 数据加载 =====================
async function loadBanners() {
  try {
    const list = await fetchBanners()
    banners.value = list.map((m) => ({
      id: m.id,
      title: m.title,
      sub: `${m.genre} · ${m.region} · ${m.year}`,
      cta: '立即观影',
      hue: (m.id * 53) % 360,
    }))
  } catch (e) {
    banners.value = []
  }
}

async function loadMovies() {
  loading.value = true
  try {
    movies.value = await fetchMovies({
      category: activeCat.value,
      keyword: keyword.value,
      tab: activeTab.value,
    })
  } catch (e) {
    showToast(e.message || '加载影片失败')
    movies.value = []
  } finally {
    loading.value = false
  }
}

// ===================== 交互 =====================
function pickCategory(cat) {
  activeCat.value = cat
  loadMovies()
}
function pickTab(key) {
  activeTab.value = key
  loadMovies()
}
function onSearch() {
  loadMovies()
}
function onBanner(b) {
  showToast(`《${b.title}》${b.cta}（演示）`)
}

// ===================== 购票弹窗 =====================
const showBuy = ref(false)
const buyMovie = ref(null)
const sessions = ref([])
const selectedSession = ref(null)
const seatRows = ref([])
const buyLoading = ref(false)

async function onPick(m) {
  buyMovie.value = m
  showBuy.value = true
  sessions.value = []
  selectedSession.value = null
  seatRows.value = []
  try {
    sessions.value = await fetchSessions(m.id)
  } catch (e) {
    showToast(e.message)
  }
}

function selectSession(s) {
  selectedSession.value = s
  buildSeats(s)
}

function buildSeats(session) {
  const total = session.totalSeats || 0
  const avail = session.availableSeats || 0
  const occupiedCount = Math.max(0, total - avail)
  const cols = Math.min(12, total)
  const rows = Math.ceil(total / cols)
  const arr = []
  let idx = 0
  for (let r = 0; r < rows; r++) {
    const rowArr = []
    for (let c = 0; c < cols && idx < total; c++, idx++) {
      rowArr.push({
        row: r + 1,
        col: c + 1,
        label: `${r + 1}排${c + 1}座`,
        occupied: idx < occupiedCount,
        selected: false,
      })
    }
    arr.push(rowArr)
  }
  seatRows.value = arr
}

function toggleSeat(seat) {
  if (seat.occupied) return
  seat.selected = !seat.selected
}

const selectedSeatLabels = computed(() =>
  seatRows.value.flat().filter((s) => s.selected).map((s) => s.label),
)
const totalPrice = computed(() => {
  const price = selectedSession.value ? Number(selectedSession.value.price) : 0
  return (price * selectedSeatLabels.value.length).toFixed(2)
})

async function confirmBuy() {
  if (!selectedSession.value) {
    showToast('请选择观影场次')
    return
  }
  const seats = selectedSeatLabels.value
  if (seats.length === 0) {
    showToast('请至少选择一个座位')
    return
  }
  buyLoading.value = true
  try {
    const res = await createOrder({ sessionId: selectedSession.value.id, seats })
    if (res.code === 200) {
      showToast(`购票成功！订单号 ${res.data.orderNo}`)
      showBuy.value = false
      if (buyMovie.value) {
        sessions.value = await fetchSessions(buyMovie.value.id)
      }
    } else if (res.code === 401) {
      showToast('请先登录')
      setTimeout(() => router.push('/login'), 700)
    } else {
      showToast(res.message || '购票失败')
    }
  } catch (e) {
    showToast('网络异常，购票失败')
  } finally {
    buyLoading.value = false
  }
}

// ===================== 我的订单弹窗 =====================
const showOrders = ref(false)
const orders = ref([])
async function openOrders() {
  try {
    orders.value = await fetchMyOrders()
    showOrders.value = true
  } catch (e) {
    if (e.message && e.message.includes('401')) {
      showToast('请先登录')
      setTimeout(() => router.push('/login'), 700)
    } else {
      showToast(e.message)
    }
  }
}

// ===================== 退出登录 =====================
async function onLogout() {
  try {
    await logout()
  } finally {
    clearAuth()
  }
  showToast('已退出登录')
  setTimeout(() => router.push('/login'), 700)
}

// ===================== 个人中心（昵称修改 + 头像上传） =====================
const showMe = ref(false)
const meNickname = ref('')
const meLoading = ref(false)
const avatarLoading = ref(false)
const avatarInput = ref(null)

function openMe() {
  meNickname.value = user.value?.nickname || ''
  showMe.value = true
}

function mergeUser(info) {
  const merged = { ...(user.value || {}), ...(info || {}) }
  user.value = merged
  setAuth(getToken(), merged)
}

async function saveNickname() {
  const nick = (meNickname.value || '').trim()
  if (!nick) {
    showToast('昵称不能为空')
    return
  }
  meLoading.value = true
  try {
    const res = await updateProfile(nick)
    if (res.code === 200) {
      mergeUser(res.data?.user || res.data)
      showToast('昵称已更新')
    } else if (res.code === 401) {
      showToast('请先登录')
      setTimeout(() => router.push('/login'), 700)
    } else {
      showToast(res.message || '昵称修改失败')
    }
  } catch (e) {
    showToast('网络异常，修改失败')
  } finally {
    meLoading.value = false
  }
}

async function onAvatarChange(e) {
  const file = e.target.files && e.target.files[0]
  if (!file) return
  avatarLoading.value = true
  try {
    const res = await uploadAvatar(file)
    if (res.code === 200) {
      mergeUser(res.data?.user || res.data)
      showToast('头像已更新')
    } else if (res.code === 401) {
      showToast('请先登录')
      setTimeout(() => router.push('/login'), 700)
    } else {
      showToast(res.message || '头像上传失败')
    }
  } catch (err) {
    showToast('网络异常，上传失败')
  } finally {
    avatarLoading.value = false
    e.target.value = '' // 允许重复选择同一文件
  }
}

// ===================== Toast =====================
const toast = ref({ show: false, msg: '' })
let toastTimer = null
function showToast(msg) {
  toast.value = { show: true, msg }
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => (toast.value = { show: false, msg: '' }), 2200)
}

// ===================== 关键词搜索（防抖） =====================
let kwTimer = null
watch(keyword, () => {
  clearTimeout(kwTimer)
  kwTimer = setTimeout(loadMovies, 400)
})

onMounted(() => {
  bannerTimer = setInterval(() => {
    if (banners.value.length) current.value = (current.value + 1) % banners.value.length
  }, 4000)
  fetchMe()
    .then((res) => {
      if (res.code === 200) user.value = res.data
      else if (res.code === 401) {
        clearAuth()
        router.push('/login')
      }
    })
    .catch(() => {})
  loadBanners()
  loadMovies()
})

onBeforeUnmount(() => {
  clearInterval(bannerTimer)
  clearTimeout(toastTimer)
  clearTimeout(kwTimer)
})
</script>

<template>
  <div class="home">
    <!-- 顶部导航栏 -->
    <header class="home-header">
      <div class="home-header__inner">
        <div class="brand">
          <span class="brand__mark">幕</span>
          <span class="brand__name">幕幕影院</span>
        </div>

        <nav class="nav">
          <span
            v-for="cat in categories"
            :key="cat"
            class="nav__item"
            :class="{ 'is-active': activeCat === cat }"
            @click="pickCategory(cat)"
            >{{ cat }}</span
          >
        </nav>

        <div class="header-right">
          <div class="search">
            <input
              v-model="keyword"
              type="text"
              placeholder="搜索影片、类型、分类、地区"
              @keyup.enter="onSearch"
            />
            <button type="button" aria-label="搜索" @click="onSearch">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path
                  fill="currentColor"
                  d="M10 2a8 8 0 1 0 5.3 14l5.4 5.4 1.4-1.4-5.4-5.4A8 8 0 0 0 10 2Zm0 2a6 6 0 1 1 0 12 6 6 0 0 1 0-12Z"
                />
              </svg>
            </button>
          </div>

          <button class="orders-btn" type="button" @click="openOrders">我的订单</button>
          <button class="me-btn" type="button" @click="openMe">个人中心</button>

          <div class="user">
            <button class="avatar" type="button" :title="user?.nickname || '个人中心'" @click="openMe">
              <img v-if="user?.avatar" class="avatar__img" :src="user.avatar" :alt="user?.nickname" />
              <span v-else>{{ (user?.nickname || '观')[0] }}</span>
            </button>
            <span class="user__name">{{ user?.nickname || '观众' }}</span>
            <button class="logout" type="button" @click="onLogout">退出</button>
          </div>
        </div>
      </div>
    </header>

    <main class="home-main">
      <!-- 横幅轮播 -->
      <section v-if="banners.length" class="banner">
        <div
          class="banner__bg"
          :style="{
            background: `linear-gradient(135deg, hsl(${banners[current].hue},62%,42%), hsl(${(banners[current].hue + 40) % 360},58%,22%))`,
          }"
        ></div>
        <img class="banner__img" :src="posterMap[banners[current].id]" :alt="banners[current].title" />
        <div class="banner__overlay"></div>
        <div class="banner__inner">
          <h2 class="banner__title">{{ banners[current].title }}</h2>
          <p class="banner__sub">{{ banners[current].sub }}</p>
          <button class="banner__cta" type="button" @click="onBanner(banners[current])">
            {{ banners[current].cta }}
          </button>
        </div>
        <div class="banner__dots">
          <span
            v-for="(b, i) in banners"
            :key="b.id"
            class="dot"
            :class="{ 'is-active': current === i }"
            @click="current = i"
          ></span>
        </div>
      </section>

      <!-- 区块标题 + 排序 Tab -->
      <div class="sec-head">
        <h3 class="sec-title">
          {{ activeCat === '首页' ? '为你推荐' : activeCat }}
        </h3>
        <div class="tabs">
          <span
            v-for="t in tabs"
            :key="t.key"
            class="tab"
            :class="{ 'is-active': activeTab === t.key }"
            @click="pickTab(t.key)"
            >{{ t.label }}</span
          >
        </div>
      </div>

      <!-- 影片网格 -->
      <div v-if="movies.length" class="grid">
        <article
          v-for="m in movies"
          :key="m.id"
          class="card"
          :style="{ '--hue': (m.id * 53) % 360 }"
          @click="onPick(m)"
        >
          <div class="card__poster">
            <img class="card__poster-img" :src="posterMap[m.id]" :alt="m.title" loading="lazy" />
            <span class="card__score">{{ Number(m.score).toFixed(1) }}</span>
            <span class="card__cat">{{ m.category }}</span>
          </div>
          <div class="card__body">
            <h4 class="card__title">{{ m.title }}</h4>
            <div class="card__meta">
              <span>{{ m.genre }}</span>
              <span>·</span>
              <span>{{ m.year }}</span>
            </div>
            <button class="buy-btn" type="button" @click.stop="onPick(m)">选座购票</button>
          </div>
        </article>
      </div>
      <div v-else-if="loading" class="empty">影片加载中…</div>
      <div v-else class="empty">没有找到匹配的影片，换个关键词试试～</div>
    </main>

    <footer class="home-footer">幕幕影院 · 演示项目（前端页面已对接后端首页/搜索/购票接口）</footer>

    <!-- 购票弹窗 -->
    <transition name="fade">
      <div v-if="showBuy" class="modal-mask" @click.self="showBuy = false">
        <div class="modal">
          <button class="modal__close" type="button" @click="showBuy = false">×</button>
          <h3 class="modal__title">选座购票 · 《{{ buyMovie?.title }}》</h3>

          <div v-if="!sessions.length" class="modal__empty">该影片暂未排片</div>

          <template v-else>
            <div class="sess-list">
              <div
                v-for="s in sessions"
                :key="s.id"
                class="sess"
                :class="{ 'is-active': selectedSession && selectedSession.id === s.id }"
                @click="selectSession(s)"
              >
                <div class="sess__info">
                  <div class="sess__cinema">{{ s.cinema }} · {{ s.hall }}</div>
                  <div class="sess__time">{{ (s.showTime || '').replace('T', ' ') }}</div>
                </div>
                <div class="sess__right">
                  <div class="sess__price">¥{{ Number(s.price).toFixed(2) }}</div>
                  <div class="sess__seat" :class="{ 'is-soldout': s.availableSeats <= 0 }">
                    {{ s.availableSeats > 0 ? '余' + s.availableSeats + '张' : '已售罄' }}
                  </div>
                </div>
              </div>
            </div>

            <div v-if="selectedSession" class="seats">
              <div class="seats__head">屏幕方向 ↓</div>
              <div class="seat-grid">
                <div class="seat-row" v-for="(row, ri) in seatRows" :key="ri">
                  <button
                    v-for="seat in row"
                    :key="seat.label"
                    type="button"
                    class="seat"
                    :class="{ occupied: seat.occupied, selected: seat.selected }"
                    :disabled="seat.occupied"
                    @click="toggleSeat(seat)"
                  >
                    {{ seat.col }}
                  </button>
                </div>
              </div>
              <div class="seats__bar">
                <span>已选 {{ selectedSeatLabels.length }} 座：{{ selectedSeatLabels.join('、') }}</span>
                <span class="seats__total">合计 ¥{{ totalPrice }}</span>
              </div>
            </div>

            <button
              class="confirm-btn"
              type="button"
              :disabled="buyLoading || !selectedSession || selectedSeatLabels.length === 0"
              @click="confirmBuy"
            >
              {{ buyLoading ? '提交中…' : '确认购票' }}
            </button>
          </template>
        </div>
      </div>
    </transition>

    <!-- 我的订单弹窗 -->
    <transition name="fade">
      <div v-if="showOrders" class="modal-mask" @click.self="showOrders = false">
        <div class="modal">
          <button class="modal__close" type="button" @click="showOrders = false">×</button>
          <h3 class="modal__title">我的订单</h3>
          <div v-if="!orders.length" class="modal__empty">还没有订单，快去购票吧～</div>
          <div v-else class="order-list">
            <div v-for="o in orders" :key="o.id" class="order">
              <div class="order__main">
                <div class="order__title">《{{ o.movieTitle }}》</div>
                <div class="order__sub">{{ o.cinema }} · {{ o.hall }}</div>
                <div class="order__sub">{{ (o.showTime || '').replace('T', ' ') }}</div>
              </div>
              <div class="order__side">
                <div class="order__seats">{{ o.seats }}</div>
                <div class="order__amount">¥{{ Number(o.amount).toFixed(2) }}</div>
                <div class="order__no">{{ o.orderNo }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- 个人中心弹窗：昵称修改 + 头像上传 -->
    <transition name="fade">
      <div v-if="showMe" class="modal-mask" @click.self="showMe = false">
        <div class="modal">
          <button class="modal__close" type="button" @click="showMe = false">×</button>
          <h3 class="modal__title">个人中心</h3>

          <div class="me">
            <div class="me__avatar">
              <img v-if="user?.avatar" :src="user.avatar" :alt="user?.nickname" />
              <span v-else>{{ (user?.nickname || '观')[0] }}</span>
            </div>
            <button class="me__upload" type="button" :disabled="avatarLoading" @click="avatarInput?.click()">
              {{ avatarLoading ? '上传中…' : '更换头像' }}
            </button>
            <input ref="avatarInput" class="me__file" type="file" accept="image/*" @change="onAvatarChange" />

            <div class="me__field">
              <label>用户名</label>
              <div class="me__readonly">{{ user?.username || '-' }}</div>
            </div>

            <div class="me__field">
              <label>昵称</label>
              <div class="me__row">
                <input
                  v-model="meNickname"
                  type="text"
                  maxlength="20"
                  placeholder="请输入昵称（≤20 字）"
                />
                <button class="me__save" type="button" :disabled="meLoading" @click="saveNickname">
                  {{ meLoading ? '保存中…' : '保存' }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <transition name="toast">
      <div v-if="toast.show" class="toast" role="status" aria-live="polite">
        {{ toast.msg }}
      </div>
    </transition>
  </div>
</template>

<style scoped>
.home {
  background: #f4f5f7;
  min-height: 100vh;
  color: #1f2329;
}

/* ===================== 顶部导航 ===================== */
.home-header {
  position: sticky;
  top: 0;
  z-index: 50;
  background: #fff;
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.06), 0 6px 20px rgba(0, 0, 0, 0.05);
}
.home-header__inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 60px;
  display: flex;
  align-items: center;
  gap: 28px;
  padding: 0 20px;
}
.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 800;
  font-size: 20px;
}
.brand__mark {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  background: linear-gradient(135deg, #2b8cff, #1f6fff);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
}
.brand__name {
  background: linear-gradient(90deg, #2b8cff, #1f6fff);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}
.nav {
  display: flex;
  align-items: center;
  gap: 22px;
}
.nav__item {
  font-size: 15px;
  color: #4b5563;
  cursor: pointer;
  padding: 6px 2px;
  position: relative;
  transition: color 0.2s;
}
.nav__item:hover {
  color: #1f2329;
}
.nav__item.is-active {
  color: #1f6fff;
  font-weight: 600;
}
.nav__item.is-active::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: -2px;
  height: 3px;
  border-radius: 3px;
  background: #1f6fff;
}

.header-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 16px;
}
.search {
  display: flex;
  align-items: center;
  background: #f1f2f4;
  border: 1px solid transparent;
  border-radius: 999px;
  padding: 6px 8px 6px 14px;
  width: 240px;
  transition: 0.2s;
}
.search:focus-within {
  background: #fff;
  border-color: #2b8cff;
  box-shadow: 0 0 0 3px rgba(43, 140, 255, 0.15);
}
.search input {
  border: 0;
  background: transparent;
  outline: none;
  flex: 1;
  font-size: 14px;
  color: #1f2329;
}
.search button {
  border: 0;
  background: #2b8cff;
  color: #fff;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: none;
}
.orders-btn {
  border: 1px solid #e3e6eb;
  background: #fff;
  color: #4b5563;
  font-size: 13px;
  padding: 7px 12px;
  border-radius: 999px;
  cursor: pointer;
  transition: 0.2s;
}
.orders-btn:hover {
  border-color: #2b8cff;
  color: #1f6fff;
}
.user {
  display: flex;
  align-items: center;
  gap: 8px;
}
.avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  border: 0;
  padding: 0;
  background: linear-gradient(135deg, #ffb347, #ff7a45);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  cursor: pointer;
  overflow: hidden;
  flex: none;
}
.avatar__img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.me-btn {
  border: 1px solid #e3e6eb;
  background: #fff;
  color: #4b5563;
  font-size: 13px;
  padding: 7px 12px;
  border-radius: 999px;
  cursor: pointer;
  transition: 0.2s;
}
.me-btn:hover {
  border-color: #2b8cff;
  color: #1f6fff;
}
.user__name {
  font-size: 14px;
  color: #1f2329;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.logout {
  font-size: 13px;
  color: #8a94a6;
  background: none;
  border: 0;
  cursor: pointer;
}
.logout:hover {
  color: #ff5a5a;
}

/* ===================== 主体 ===================== */
.home-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 22px 20px 60px;
}

/* 横幅 */
.banner {
  position: relative;
  height: 300px;
  border-radius: 14px;
  overflow: hidden;
  margin-bottom: 26px;
  color: #fff;
}
.banner__bg {
  position: absolute;
  inset: 0;
  transition: background 0.6s ease;
}
.banner__img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 1;
  opacity: 0.85;
  transition: opacity 0.6s ease;
}
.banner__overlay {
  position: absolute;
  inset: 0;
  z-index: 1;
  background: linear-gradient(
    90deg,
    rgba(0, 0, 0, 0.62),
    rgba(0, 0, 0, 0.2) 60%,
    rgba(0, 0, 0, 0.08)
  );
}
.banner__inner {
  position: relative;
  z-index: 2;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 32px 36px;
}
.banner__title {
  font-size: 34px;
  font-weight: 800;
  margin: 0;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.4);
}
.banner__sub {
  margin: 10px 0 18px;
  font-size: 15px;
  opacity: 0.92;
}
.banner__cta {
  align-self: flex-start;
  border: 0;
  background: #fff;
  color: #1f2329;
  font-weight: 600;
  padding: 10px 22px;
  border-radius: 999px;
  cursor: pointer;
  transition: transform 0.15s;
}
.banner__cta:hover {
  transform: scale(1.04);
}
.banner__dots {
  position: absolute;
  z-index: 3;
  right: 24px;
  bottom: 18px;
  display: flex;
  gap: 8px;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: 0.2s;
}
.dot.is-active {
  background: #fff;
  width: 20px;
  border-radius: 4px;
}

/* 区块头 + Tab */
.sec-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 6px 0 18px;
}
.sec-title {
  font-size: 20px;
  font-weight: 700;
  color: #1f2329;
  margin: 0;
}
.tabs {
  display: flex;
  gap: 6px;
}
.tab {
  font-size: 14px;
  color: #6b7280;
  padding: 6px 14px;
  border-radius: 999px;
  cursor: pointer;
  background: #f1f2f4;
  transition: 0.2s;
}
.tab.is-active {
  color: #fff;
  background: #2b8cff;
}

/* 网格 + 卡片 */
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 20px;
}
.card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  transition: transform 0.18s, box-shadow 0.2s;
}
.card:hover {
  transform: translateY(-6px);
  box-shadow: 0 14px 30px rgba(0, 0, 0, 0.12);
}
.card__poster {
  position: relative;
  height: 240px;
  background: linear-gradient(
    150deg,
    hsl(var(--hue), 60%, 32%),
    hsl(calc(var(--hue) + 40), 55%, 18%)
  );
}
.card__poster-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 0;
}
.card__poster::after {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 1;
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
  z-index: 2;
  font-size: 16px;
  font-weight: 800;
  color: #ff8a00;
  background: rgba(0, 0, 0, 0.35);
  padding: 2px 8px;
  border-radius: 6px;
}
.card__cat {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 2;
  font-size: 12px;
  color: #fff;
  background: rgba(0, 0, 0, 0.35);
  padding: 2px 8px;
  border-radius: 6px;
}
.card__body {
  padding: 12px 14px 14px;
}
.card__title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2329;
  margin: 0 0 6px;
}
.card__meta {
  font-size: 12px;
  color: #9aa1ad;
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
}
.buy-btn {
  width: 100%;
  border: 0;
  background: linear-gradient(90deg, #2b8cff, #1f6fff);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  padding: 8px 0;
  border-radius: 8px;
  cursor: pointer;
  transition: opacity 0.2s;
}
.buy-btn:hover {
  opacity: 0.9;
}

.empty {
  text-align: center;
  color: #9aa1ad;
  padding: 60px 0;
}

.home-footer {
  text-align: center;
  color: #b0b6c0;
  font-size: 13px;
  padding: 30px 0 40px;
}

/* ===================== 弹窗 ===================== */
.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 100;
  background: rgba(10, 12, 20, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}
.modal {
  position: relative;
  width: 100%;
  max-width: 520px;
  max-height: 86vh;
  overflow-y: auto;
  background: #fff;
  border-radius: 16px;
  padding: 24px 24px 28px;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.35);
}
.modal__close {
  position: absolute;
  top: 14px;
  right: 16px;
  border: 0;
  background: #f1f2f4;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  font-size: 18px;
  line-height: 1;
  cursor: pointer;
  color: #4b5563;
}
.modal__title {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 16px;
}
.modal__empty {
  text-align: center;
  color: #9aa1ad;
  padding: 30px 0;
}

/* 场次列表 */
.sess-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.sess {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border: 1px solid #eef0f3;
  border-radius: 10px;
  padding: 12px 14px;
  cursor: pointer;
  transition: 0.15s;
}
.sess:hover {
  border-color: #c9dcff;
}
.sess.is-active {
  border-color: #2b8cff;
  background: #f3f8ff;
}
.sess__cinema {
  font-size: 14px;
  font-weight: 600;
  color: #1f2329;
}
.sess__time {
  font-size: 13px;
  color: #6b7280;
  margin-top: 4px;
}
.sess__right {
  text-align: right;
}
.sess__price {
  font-size: 15px;
  font-weight: 700;
  color: #ff5a3c;
}
.sess__seat {
  font-size: 12px;
  color: #2b8cff;
  margin-top: 4px;
}
.sess__seat.is-soldout {
  color: #b0b6c0;
}

/* 选座 */
.seats {
  margin-top: 18px;
  border-top: 1px dashed #e6e8ec;
  padding-top: 16px;
}
.seats__head {
  text-align: center;
  font-size: 12px;
  color: #9aa1ad;
  background: #f4f5f7;
  border-radius: 6px;
  padding: 4px 0;
  margin-bottom: 12px;
}
.seat-grid {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;
}
.seat-row {
  display: flex;
  gap: 8px;
}
.seat {
  width: 26px;
  height: 26px;
  border: 0;
  border-radius: 6px;
  background: #e3e6eb;
  color: #6b7280;
  font-size: 11px;
  cursor: pointer;
  transition: 0.12s;
}
.seat:hover:not(:disabled) {
  background: #c9dcff;
}
.seat.selected {
  background: #2b8cff;
  color: #fff;
}
.seat.occupied {
  background: #f3c0c0;
  color: #fff;
  cursor: not-allowed;
}
.seats__bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 14px;
  font-size: 13px;
  color: #4b5563;
}
.seats__total {
  font-weight: 700;
  color: #ff5a3c;
}
.confirm-btn {
  width: 100%;
  margin-top: 18px;
  border: 0;
  background: linear-gradient(90deg, #2b8cff, #1f6fff);
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  padding: 12px 0;
  border-radius: 10px;
  cursor: pointer;
  transition: opacity 0.2s;
}
.confirm-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 订单列表 */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.order {
  display: flex;
  justify-content: space-between;
  border: 1px solid #eef0f3;
  border-radius: 10px;
  padding: 14px;
}
.order__title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2329;
}
.order__sub {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}
.order__side {
  text-align: right;
}
.order__seats {
  font-size: 12px;
  color: #4b5563;
}
.order__amount {
  font-size: 15px;
  font-weight: 700;
  color: #ff5a3c;
  margin-top: 4px;
}
.order__no {
  font-size: 11px;
  color: #b0b6c0;
  margin-top: 4px;
}

/* 个人中心 */
.me {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.me__avatar {
  width: 84px;
  height: 84px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ffb347, #ff7a45);
  color: #fff;
  font-size: 30px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  box-shadow: 0 6px 18px rgba(255, 122, 69, 0.3);
}
.me__avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.me__upload {
  margin-top: 12px;
  border: 1px solid #2b8cff;
  background: #fff;
  color: #1f6fff;
  font-size: 13px;
  padding: 6px 16px;
  border-radius: 999px;
  cursor: pointer;
  transition: 0.2s;
}
.me__upload:hover:not(:disabled) {
  background: #f3f8ff;
}
.me__upload:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.me__file {
  display: none;
}
.me__field {
  width: 100%;
  margin-top: 18px;
}
.me__field > label {
  display: block;
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 6px;
}
.me__readonly {
  font-size: 14px;
  color: #1f2329;
  background: #f4f5f7;
  border-radius: 8px;
  padding: 9px 12px;
}
.me__row {
  display: flex;
  gap: 10px;
}
.me__row input {
  flex: 1;
  border: 1px solid #e3e6eb;
  border-radius: 8px;
  padding: 9px 12px;
  font-size: 14px;
  outline: none;
  color: #1f2329;
  transition: 0.2s;
}
.me__row input:focus {
  border-color: #2b8cff;
  box-shadow: 0 0 0 3px rgba(43, 140, 255, 0.15);
}
.me__save {
  border: 0;
  background: linear-gradient(90deg, #2b8cff, #1f6fff);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  padding: 0 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: opacity 0.2s;
}
.me__save:hover:not(:disabled) {
  opacity: 0.9;
}
.me__save:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Toast */
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
  border: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.4);
  z-index: 80;
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

/* 弹窗淡入 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
