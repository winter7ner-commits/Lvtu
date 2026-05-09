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
app.mount('#app')