import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { useAuthStore } from './store/auth'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)

// 在其他插件之前立即恢复登录状态
const authStore = useAuthStore()
authStore.restoreFromStorage()

app
  .use(router)
  .use(ElementPlus)   
  .mount('#app')