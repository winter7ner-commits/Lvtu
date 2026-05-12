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
import { useAuthStore } from '../store/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/law-management' },
    { path: '/admin', redirect: '/law-management' },
    { path: '/admin/login', component: AdminLogin, name: 'AdminLogin' },
    { path: '/admin/register', component: AdminRegister, name: 'AdminRegister' },
    { path: '/law-management', component: LawManage, name: 'LawManage', meta: { requiresAdmin: true } },
    { path: '/law-browse', component: AdminLawBrowse, name: 'AdminLawBrowse', meta: { requiresAdmin: true } },
    { path: '/users', component: UserManagement, name: 'UserManagement', meta: { requiresAdmin: true } },
    { path: '/auth-audit', component: AuthAudit, name: 'AuthAudit', meta: { requiresAdmin: true } },
    { path: '/orders', component: OrderQuery, name: 'OrderQuery', meta: { requiresAdmin: true } },
    { path: '/categories', component: LawCategory, name: 'LawCategory', meta: { requiresAdmin: true } },
    { path: '/documents', component: LawDocument, name: 'LawDocument', meta: { requiresAdmin: true } },
    { path: '/articles', component: LawArticle, name: 'LawArticle', meta: { requiresAdmin: true } },
  ]
})

router.beforeEach((to) => {
  const authStore = useAuthStore()
  const isLoggedIn = authStore.isAuthenticated
  const user = authStore.user

  if (to.meta.requiresAdmin) {
    if (!isLoggedIn) return { path: '/admin/login', query: { redirect: to.fullPath } }
    if (user?.userType !== 3) return { path: '/admin/login', query: { redirect: to.fullPath } }
  }
})

export default router
