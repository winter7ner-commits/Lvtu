<<<<<<< HEAD
// 导入Vue Router的核心函数
import { createRouter, createWebHistory } from 'vue-router'
// 导入页面组件
import Home from '../views/Home.vue'
import LawyerList from '../views/lawyer/LawyerList.vue'
import LawyerDetail from '../views/lawyer/LawyerDetail.vue'
import LawArticleList from '../views/lawyer/LawArticleList.vue'

// 创建路由实例：使用HTML5历史模式
const router = createRouter({
  history: createWebHistory(),
  // 定义路由规则
  routes: [
    // 首页
    { path: '/', component: Home, name: 'Home' },
    // 律师列表页
    { path: '/lawyer-list', component: LawyerList, name: 'LawyerList' },
    // 律师详情页
    { path: '/lawyer', component: LawyerDetail, name: 'LawyerDetail' },
    // 法律法规列表页
    { path: '/law-article-list', component: LawArticleList, name: 'LawArticleList' }
  ]
})

// 导出路由实例供main.js使用
=======
import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import LawyerList from '../views/lawyer/LawyerList.vue'
import LawyerDetail from '../views/lawyer/LawyerDetail.vue'
import Login from '../views/auth/Login.vue'
import Register from '../views/auth/Register.vue'
import ForgotPassword from '../views/auth/ForgotPassword.vue'
import AdminLogin from '../views/auth/AdminLogin.vue'
import AdminRegister from '../views/auth/AdminRegister.vue'
import { useAuthStore } from '../store/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Home, name: 'Home' },
    { path: '/lawyer-list', component: LawyerList, name: 'LawyerList' },
    { path: '/lawyer', component: LawyerDetail, name: 'LawyerDetail' },
    { path: '/login', component: Login, name: 'Login' },
    { path: '/register', component: Register, name: 'Register' },
    { path: '/forgot-password', component: ForgotPassword, name: 'ForgotPassword' },
    { path: '/admin/login', component: AdminLogin, name: 'AdminLogin' },
    { path: '/admin/register', component: AdminRegister, name: 'AdminRegister', meta: { requiresAdmin: true } },
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const isLoggedIn = authStore.isAuthenticated
  const user = authStore.user

  if (to.meta.requiresAdmin) {
    if (!isLoggedIn) {
      next('/admin/login')
    } else if (user?.userType !== 3) {
      next('/')
    } else {
      next()
    }
  } else {
    next()
  }
})

>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
export default router