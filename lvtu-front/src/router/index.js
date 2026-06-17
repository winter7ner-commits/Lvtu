import { createRouter, createWebHistory } from 'vue-router'

import { useAuthStore } from '../store/auth'

const Home = () => import('../views/Home.vue')
const SearchResults = () => import('../views/SearchResults.vue')
const LawyerList = () => import('../views/lawyer/LawyerList.vue')
const LawyerDetail = () => import('../views/lawyer/LawyerDetail.vue')
const LawArticleList = () => import('../views/lawyer/LawArticleList.vue')
const About = () => import('../views/About.vue')
const Login = () => import('../views/auth/Login.vue')
const Register = () => import('../views/auth/Register.vue')
const ForgotPassword = () => import('../views/auth/ForgotPassword.vue')
const ChangePassword = () => import('../views/auth/ChangePassword.vue')
const Settings = () => import('../views/Settings.vue')
const AuthCenter = () => import('../views/user/AuthCenter.vue')
const RealName = () => import('../views/user/RealName.vue')
const ApplicationForm = () => import('../views/user/ApplicationForm.vue')
const ApplicationStatus = () => import('../views/user/ApplicationStatus.vue')
const OrderCreate = () => import('../views/order/OrderCreate.vue')
const ClientOrderList = () => import('../views/order/ClientOrderList.vue')
const ClientOrderDetail = () => import('../views/order/ClientOrderDetail.vue')
const LawyerOrderHall = () => import('../views/lawyerOrder/LawyerOrderHall.vue')
const LawyerMyOrders = () => import('../views/lawyerOrder/LawyerMyOrders.vue')
const LawyerOrderDetail = () => import('../views/lawyerOrder/LawyerOrderDetail.vue')
const EvaluationDashboard = () => import('../views/evaluation/EvaluationDashboard.vue')
const NotificationList = () => import('../views/notification/NotificationList.vue')
const UserProfile = () => import('../views/user/UserProfile.vue')

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
    { path: '/laws', component: LawArticleList, name: 'Laws' },
    { path: '/law-article-list', redirect: (to) => ({ path: '/laws', query: to.query }) },
    { path: '/lawyer-list', component: LawyerList, name: 'LawyerList' },
    { path: '/lawyer/:id', component: LawyerDetail, name: 'LawyerDetail' },
    { path: '/login', component: Login, name: 'Login' },
    { path: '/register', component: Register, name: 'Register' },
    { path: '/forgot-password', component: ForgotPassword, name: 'ForgotPassword' },
    { path: '/change-password', component: ChangePassword, name: 'ChangePassword', meta: { requiresAuth: true } },
    { path: '/settings', component: Settings, name: 'Settings', meta: { requiresAuth: true } },
    { path: '/auth-center', component: AuthCenter, name: 'AuthCenter', meta: { requiresAuth: true } },
    { path: '/user/realname', component: RealName, name: 'RealName', meta: { requiresAuth: true } },
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
    { path: '/about', component: About, name: 'About' },
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
