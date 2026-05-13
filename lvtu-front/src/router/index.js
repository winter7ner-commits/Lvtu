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
import LawyerOrderHall from '../views/lawyerOrder/LawyerOrderHall.vue'
import LawyerMyOrders from '../views/lawyerOrder/LawyerMyOrders.vue'
import LawyerOrderDetail from '../views/lawyerOrder/LawyerOrderDetail.vue'

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
    { path: '/lawyer/orders/available', component: LawyerOrderHall, name: 'LawyerOrderHall' },
    { path: '/lawyer/orders/my', component: LawyerMyOrders, name: 'LawyerMyOrders' },
    { path: '/lawyer/orders/:orderId', component: LawyerOrderDetail, name: 'LawyerOrderDetail' },
  ]
})

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
