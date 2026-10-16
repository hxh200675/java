<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import SceneBackground from './components/SceneBackground.vue'
import FloatingReels from './components/FloatingReels.vue'

// 登录/注册页保留影院氛围背景；首页是浅色门户，去掉氛围背景
const route = useRoute()
const isAuth = computed(
  () => route.name === 'login' || route.name === 'register',
)
</script>

<template>
  <SceneBackground v-if="isAuth" />
  <FloatingReels v-if="isAuth" />
  <main class="stage" :class="{ 'stage--home': route.name === 'home' }">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </main>
</template>

<style scoped>
.stage {
  position: relative;
  z-index: 3;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}
/* 首页整页铺满，不再垂直居中，支持长内容滚动 */
.stage--home {
  display: block;
  padding: 0;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}
.fade-enter-from {
  opacity: 0;
  transform: translateY(12px);
}
.fade-leave-to {
  opacity: 0;
  transform: translateY(-12px);
}
</style>
