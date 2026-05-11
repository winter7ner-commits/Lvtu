import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import LawyerList from '../views/lawyer/LawyerList.vue'
import LawyerDetail from '../views/lawyer/LawyerDetail.vue'
import Login from '../views/auth/Login.vue'
import Register from '../views/auth/Register.vue'
import ForgotPassword from '../views/auth/ForgotPassword.vue'
import AdminLogin from '../views/auth/AdminLogin.vue'
import AdminRegister from '../views/auth/AdminRegister.vue'
import AuthCenter from '../views/user/AuthCenter.vue'
import ApplicationForm from '../views/user/ApplicationForm.vue'
import ApplicationStatus from '../views/user/ApplicationStatus.vue'
import ApplicationAudit from '../views/admin/ApplicationAudit.vue'
import Settings from '../views/Settings.vue'
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
    { path: '/auth-center', component: AuthCenter, name: 'AuthCenter' },
    { path: '/apply', component: ApplicationForm, name: 'ApplicationForm' },
    { path: '/application-status', component: ApplicationStatus, name: 'ApplicationStatus' },
    { path: '/application-audit', component: ApplicationAudit, name: 'ApplicationAudit', meta: { requiresAdmin: true } },
    { path: '/settings', component: Settings, name: 'Settings' }
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

export default router