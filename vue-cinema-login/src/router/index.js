import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import HomeView from '../views/HomeView.vue'
import { getToken } from '../api/auth'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'login', component: LoginView },
  { path: '/register', name: 'register', component: RegisterView },
  { path: '/home', name: 'home', component: HomeView, meta: { requiresAuth: true } },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫：/home 需要登录；已登录访问登录/注册页则直接去首页
router.beforeEach((to) => {
  const loggedIn = !!getToken()
  if (to.meta.requiresAuth && !loggedIn) {
    return { name: 'login' }
  }
  if ((to.name === 'login' || to.name === 'register') && loggedIn) {
    return { name: 'home' }
  }
  return true
})

export default router
