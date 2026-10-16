import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    open: false,
    proxy: {
      // 将 /api 代理到本地 Spring Boot 后端，避免跨域
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      },
      // 将上传的静态资源（头像等）也代理到后端
      '/uploads': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  }
})
