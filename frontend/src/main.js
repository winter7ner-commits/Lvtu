import { createApp } from 'vue'
import App from './App.vue'

// 创建Vue应用
const app = createApp(App)

// 模拟用户信息
const currentUser = {
  id: 1001,
  name: '测试用户',
  role: 'USER'
}

// 全局属性
app.config.globalProperties.$currentUser = currentUser

// 挂载应用
app.mount('#app')
