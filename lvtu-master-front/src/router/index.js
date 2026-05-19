import { createRouter, createWebHistory } from 'vue-router'
import AdminLogin from '../views/auth/AdminLogin.vue'
import AdminRegister from '../views/auth/AdminRegister.vue'
import LawManage from '../views/law/LawManage.vue'
import LawCategory from '../views/law/LawCategory.vue'
import LawDocument from '../views/law/LawDocument.vue'
import LawArticle from '../views/law/LawArticle.vue'
import AdminLawBrowse from '../views/law/AdminLawBrowse.vue'
import UserManagement from '../views/admin/UserManagement.vue'
import AuthAudit from '../views/admin/AuthAudit.vue'
import OrderQuery from '../views/admin/OrderQuery.vue'
import SettlementManage from '../views/admin/SettlementManage.vue'
import EvaluationManage from '../views/admin/EvaluationManage.vue'

import { useAuthStore } from '../store/auth'

const getDefaultRouteByUser = (user) => {
  if (user?.userType === 4) return '/auth-audit'
  return '/law-management'
}

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/admin' },
    { path: '/admin', redirect: () => getDefaultRouteByUser(useAuthStore().user) },
    { path: '/admin/login', component: AdminLogin, name: 'AdminLogin' },
    { path: '/admin/register', component: AdminRegister, name: 'AdminRegister' },
    { path: '/law-management', component: LawManage, name: 'LawManage', meta: { requiresAdmin: true, allowedUserTypes: [3] } },
    { path: '/law-browse', component: AdminLawBrowse, name: 'AdminLawBrowse', meta: { requiresAdmin: true, allowedUserTypes: [3] } },
    { path: '/users', component: UserManagement, name: 'UserManagement', meta: { requiresAdmin: true, allowedUserTypes: [3] } },
    { path: '/auth-audit', component: AuthAudit, name: 'AuthAudit', meta: { requiresAdmin: true, allowedUserTypes: [4] } },
    { path: '/orders', component: OrderQuery, name: 'OrderQuery', meta: { requiresAdmin: true, allowedUserTypes: [3] } },
    { path: '/settlements', component: SettlementManage, name: 'SettlementManage', meta: { requiresAdmin: true, allowedUserTypes: [3] } },
    { path: '/evaluations', component: EvaluationManage, name: 'EvaluationManage', meta: { requiresAdmin: true, allowedUserTypes: [3] } },
    { path: '/categories', component: LawCategory, name: 'LawCategory', meta: { requiresAdmin: true, allowedUserTypes: [3] } },
    { path: '/documents', component: LawDocument, name: 'LawDocument', meta: { requiresAdmin: true, allowedUserTypes: [3] } },
    { path: '/articles', component: LawArticle, name: 'LawArticle', meta: { requiresAdmin: true, allowedUserTypes: [3] } },
  ]
})

router.beforeEach((to) => {
  const authStore = useAuthStore()
  const isLoggedIn = authStore.isAuthenticated
  const user = authStore.user

  if (to.meta.requiresAdmin) {
    if (!isLoggedIn) return { path: '/admin/login', query: { redirect: to.fullPath } }
    const allowedUserTypes = to.meta.allowedUserTypes || []
    if (!allowedUserTypes.includes(user?.userType)) {
      return getDefaultRouteByUser(user)
    }
  }
})

export default router
