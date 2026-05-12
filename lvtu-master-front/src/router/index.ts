import { createRouter, createWebHistory } from 'vue-router'
import AdminLogin from '../views/auth/AdminLogin.vue'
import AdminRegister from '../views/auth/AdminRegister.vue'
import LawCategory from '../views/law/LawCategory.vue'
import LawDocument from '../views/law/LawDocument.vue'
import LawArticle from '../views/law/LawArticle.vue'

import ApplicationAudit from '../views/auth/ApplicationAudit.vue'
import { useAuthStore } from '../store/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: AdminLogin, name: 'AdminLogin' },
    { path: '/register', component: AdminRegister, name: 'AdminRegister' },
    { path: '/categories', component: LawCategory, name: 'LawCategory', meta: { requiresAuth: true } },
    { path: '/documents', component: LawDocument, name: 'LawDocument', meta: { requiresAuth: true } },
    { path: '/articles', component: LawArticle, name: 'LawArticle', meta: { requiresAuth: true } },
    { path: '/application-audit', component: ApplicationAudit, name: 'ApplicationAudit', meta: { requiresAuth: true } },
    { path: '/', redirect: '/articles' }
  ]
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.meta.requiresAuth

  if (requiresAuth && !authStore.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router