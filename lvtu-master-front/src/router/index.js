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
import ArticleFeedback from '../views/admin/ArticleFeedback.vue'
import InterventionManage from '../views/admin/InterventionManage.vue'
import SystemSettings from '../views/admin/SystemSettings.vue'

import { useAuthStore } from '../store/auth'
import { canAccessAdminRoute, firstAdminPath } from '../utils/adminPermissions'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: () => firstAdminPath(JSON.parse(localStorage.getItem('adminUser') || 'null')) },
    { path: '/admin', redirect: () => firstAdminPath(JSON.parse(localStorage.getItem('adminUser') || 'null')) },
    { path: '/admin/login', component: AdminLogin, name: 'AdminLogin' },
    { path: '/admin/register', redirect: '/roles' },
    { path: '/roles', component: AdminRegister, name: 'RoleManagement', meta: { requiresAdmin: true, roles: ['SUPER_ADMIN'] } },
    { path: '/settings', component: SystemSettings, name: 'SystemSettings', meta: { requiresAdmin: true, roles: ['SUPER_ADMIN'] } },
    { path: '/law-management', component: LawManage, name: 'LawManage', meta: { requiresAdmin: true, roles: ['SUPER_ADMIN', 'OPERATOR'] } },
    { path: '/law-browse', component: AdminLawBrowse, name: 'AdminLawBrowse', meta: { requiresAdmin: true, roles: ['SUPER_ADMIN', 'OPERATOR'] } },
    { path: '/users', component: UserManagement, name: 'UserManagement', meta: { requiresAdmin: true, roles: ['SUPER_ADMIN', 'OPERATOR'] } },
    { path: '/auth-audit', component: AuthAudit, name: 'AuthAudit', meta: { requiresAdmin: true, roles: ['SUPER_ADMIN', 'CERT_AUDITOR'] } },
    { path: '/orders', component: OrderQuery, name: 'OrderQuery', meta: { requiresAdmin: true, roles: ['SUPER_ADMIN', 'OPERATOR', 'CUSTOMER_SERVICE'] } },
    { path: '/interventions', component: InterventionManage, name: 'InterventionManage', meta: { requiresAdmin: true, roles: ['SUPER_ADMIN', 'CUSTOMER_SERVICE'] } },
    { path: '/settlements', component: SettlementManage, name: 'SettlementManage', meta: { requiresAdmin: true, roles: ['SUPER_ADMIN'] } },
    { path: '/evaluations', component: EvaluationManage, name: 'EvaluationManage', meta: { requiresAdmin: true, roles: ['SUPER_ADMIN', 'OPERATOR'] } },
    { path: '/article-feedback', component: ArticleFeedback, name: 'ArticleFeedback', meta: { requiresAdmin: true, roles: ['SUPER_ADMIN', 'OPERATOR'] } },
    { path: '/categories', component: LawCategory, name: 'LawCategory', meta: { requiresAdmin: true, roles: ['SUPER_ADMIN', 'OPERATOR'] } },
    { path: '/documents', component: LawDocument, name: 'LawDocument', meta: { requiresAdmin: true, roles: ['SUPER_ADMIN', 'OPERATOR'] } },
    { path: '/articles', component: LawArticle, name: 'LawArticle', meta: { requiresAdmin: true, roles: ['SUPER_ADMIN', 'OPERATOR'] } },
  ]
})

router.beforeEach((to) => {
  const authStore = useAuthStore()
  const isLoggedIn = authStore.isAuthenticated
  const user = authStore.user

  if (to.meta.requiresAdmin) {
    if (!isLoggedIn) return { path: '/admin/login', query: { redirect: to.fullPath } }
    if (Number(user?.userType) !== 3) return { path: '/admin/login', query: { redirect: to.fullPath } }
    if (!canAccessAdminRoute(user, to.meta.roles || [])) return { path: firstAdminPath(user) }
  }
})

export default router
