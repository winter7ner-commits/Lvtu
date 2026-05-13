import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import Home from '../views/Home.vue'
import LawyerList from '../views/lawyer/LawyerList.vue'
import LawyerDetail from '../views/lawyer/LawyerDetail.vue'
import OrderCreate from '../views/order/OrderCreate.vue'
import ClientOrderList from '../views/order/ClientOrderList.vue'
import UserProfile from '../views/user/UserProfile.vue'
import Login from '../views/auth/Login.vue'
import Register from '../views/auth/Register.vue'
import Settings from '../views/Settings.vue'
import About from '../views/About.vue'
import Order from '../views/order/ClientOrderList.vue'

import TheHeader from '../components/layout/TheHeader.vue'
import TheFooter from '../components/layout/TheFooter.vue'
//import TheSidebar from '../components/layout/TheSidebar.vue'


const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Home, name: 'Home' },
    { path: '/lawyer-list', component: LawyerList, name: 'LawyerList' },
    { path: '/lawyer/:id', component: LawyerDetail, name: 'LawyerDetail' },
    { path: '/order-create', component: OrderCreate, name: 'OrderCreate', meta: { requiresAuth: true } },
    { path: '/order-list', component: ClientOrderList, name: 'ClientOrderList', meta: { requiresAuth: true } },
    { path: '/user-profile', component: UserProfile, name: 'UserProfile', meta: { requiresAuth: true } },
    { path: '/login', component: Login, name: 'Login' },
    { path: '/signup', component: Register, name: 'Signup' },
    { path: '/register', component: Register, name: 'Register' },
    { path: '/settings', component: Settings, name: 'Settings', meta: { requiresAuth: true } },
    { path: '/about', component: About, name: 'About' },
    { path: '/orders', component: Order, name: 'Orders', meta: { requiresAuth: true } }
  ]
})

// 全局导航守卫 - 检查登录状态
router.beforeEach((to, from, next) => {
  // 检查路由是否需要登录
  if (to.meta.requiresAuth) {
    // 从本地存储检查登录状态
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
    
    if (!isLoggedIn) {
      // 未登录，显示提示并跳转到登录页
      ElMessage.warning('请先登录')
      next('/login')
    } else {
      // 已登录，正常导航
      next()
    }
  } else {
    // 不需要登录的页面，正常导航
    next()
  }
})

export default router
