<<<<<<< HEAD
// 导入Vue框架
import { createApp } from 'vue'
// 导入根组件
import App from './App.vue'
// 导入路由配置
import router from './router'

// 创建Vue应用实例
const app = createApp(App)
// 注册路由插件
app.use(router)
// 挂载应用到DOM
=======
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)
const pinia = createPinia()
app.use(pinia)
app.use(router)
app.use(ElementPlus)

import { useAuthStore } from './store/auth'
const authStore = useAuthStore(pinia)
authStore.initAuth()

>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
app.mount('#app')