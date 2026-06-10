import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import SearchResults from '../views/SearchResults.vue'
import LawyerList from '../views/lawyer/LawyerList.vue'
import LawyerDetail from '../views/lawyer/LawyerDetail.vue'
import LawArticleList from '../views/lawyer/LawArticleList.vue'
import Login from '../views/auth/Login.vue'
import Register from '../views/auth/Register.vue'
import ForgotPassword from '../views/auth/ForgotPassword.vue'
import ChangePassword from '../views/auth/ChangePassword.vue'
import Settings from '../views/Settings.vue'
import AuthCenter from '../views/user/AuthCenter.vue'
import ApplicationForm from '../views/user/ApplicationForm.vue'
import ApplicationStatus from '../views/user/ApplicationStatus.vue'
import OrderCreate from '../views/order/OrderCreate.vue'
import ClientOrderList from '../views/order/ClientOrderList.vue'
import ClientOrderDetail from '../views/order/ClientOrderDetail.vue'
import LawyerOrderHall from '../views/lawyerOrder/LawyerOrderHall.vue'
import LawyerMyOrders from '../views/lawyerOrder/LawyerMyOrders.vue'
import LawyerOrderDetail from '../views/lawyerOrder/LawyerOrderDetail.vue'
import EvaluationDashboard from '../views/evaluation/EvaluationDashboard.vue'
import NotificationList from '../views/notification/NotificationList.vue'
// import AdminLogin from '../views/auth/AdminLogin.vue'
// import AdminRegister from '../views/auth/AdminRegister.vue'

import UserProfile from '../views/user/UserProfile.vue'
// import Settings from '../views/Settings.vue'

import { useAuthStore } from '../store/auth'


const router = createRouter({
  history: createWebHistory(),
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }

    return { left: 0, top: 0 }
  },
  routes: [
    { path: '/', component: Home, name: 'Home' },
    { path: '/search', component: SearchResults, name: 'SearchResults' },
    { path: '/law-article-list', component: LawArticleList, name: 'LawArticleList' },
    { path: '/lawyer-list', component: LawyerList, name: 'LawyerList' },
    { path: '/lawyer/:id', component: LawyerDetail, name: 'LawyerDetail' },
    { path: '/login', component: Login, name: 'Login' },
    { path: '/register', component: Register, name: 'Register' },
    { path: '/forgot-password', component: ForgotPassword, name: 'ForgotPassword' },
    { path: '/change-password', component: ChangePassword, name: 'ChangePassword' },
    { path: '/settings', component: Settings, name: 'Settings', meta: { requiresAuth: true } },
    { path: '/auth-center', component: AuthCenter, name: 'AuthCenter', meta: { requiresAuth: true } },
    { path: '/apply', component: ApplicationForm, name: 'ApplicationForm', meta: { requiresAuth: true } },
    { path: '/application-status', component: ApplicationStatus, name: 'ApplicationStatus', meta: { requiresAuth: true } },
    { path: '/order-create', component: OrderCreate, name: 'OrderCreate', meta: { requiresAuth: true } },
    { path: '/orders', component: ClientOrderList, name: 'ClientOrderList', meta: { requiresAuth: true } },
    { path: '/orders/:orderId', component: ClientOrderDetail, name: 'ClientOrderDetail', meta: { requiresAuth: true } },
    { path: '/lawyer/orders/available', component: LawyerOrderHall, name: 'LawyerOrderHall', meta: { requiresAuth: true } },
    { path: '/lawyer/orders/my', component: LawyerMyOrders, name: 'LawyerMyOrders', meta: { requiresAuth: true } },
    { path: '/lawyer/orders/:orderId', component: LawyerOrderDetail, name: 'LawyerOrderDetail', meta: { requiresAuth: true } },
    { path: '/evaluations', component: EvaluationDashboard, name: 'EvaluationDashboard' },
    { path: '/notifications', component: NotificationList, name: 'NotificationList', meta: { requiresAuth: true } },
    // { path: '/admin/login', component: AdminLogin, name: 'AdminLogin' },
    // { path: '/admin/register', component: AdminRegister, name: 'AdminRegister', meta: { requiresAdmin: true } },

    { path: '/user-profile', component: UserProfile, name: 'UserProfile', meta: { requiresAuth: true } },
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
    return {
      path: '/login',
      query: { redirect: to.fullPath }
    }
  }

  const isLawyerAccount = Number(user?.authStatus) === 2
  const userOnlyRoutes = ['OrderCreate', 'ClientOrderList', 'ClientOrderDetail']
  if (isLawyerAccount && userOnlyRoutes.includes(to.name)) {
    return '/lawyer/orders/my'
  }

  if (isLoggedIn && !isLawyerAccount && to.path.startsWith('/lawyer/orders')) {
    return '/'
  }
})

export default router
