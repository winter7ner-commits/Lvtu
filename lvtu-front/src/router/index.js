import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import LawyerList from '../views/lawyer/LawyerList.vue'
import LawyerDetail from '../views/lawyer/LawyerDetail.vue'
import Login from '../views/auth/Login.vue'
import Register from '../views/auth/Register.vue'
import ForgotPassword from '../views/auth/ForgotPassword.vue'
import AdminLogin from '../views/auth/AdminLogin.vue'
import AdminRegister from '../views/auth/AdminRegister.vue'

// import UserProfile from '../views/user/UserProfile.vue'
// import Settings from '../views/Settings.vue'

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

    // { path: '/user-profile', component: UserProfile, name: 'UserProfile', meta: { requiresAuth: true } },
    // { path: '/settings', component: Settings, name: 'Settings', meta: { requiresAuth: true } },
  ]
})

// 路由守卫
router.beforeEach((to) => {
  const authStore = useAuthStore()
  const isLoggedIn = authStore.isAuthenticated
  const user = authStore.user

  if (to.meta.requiresAdmin) {
    if (!isLoggedIn) {
      return '/admin/login' // 直接返回字符串
    } 
    if (user?.userType !== 3) {
      return '/' // 非管理员重定向到首页
    }
  }

  if (to.meta.requiresAuth && !isLoggedIn) {
    return '/login' // 未登录重定向到登录页
  }
})

export default router