import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import LawyerList from '../views/lawyer/LawyerList.vue'
import LawyerDetail from '../views/lawyer/LawyerDetail.vue'
import LawArticleList from '../views/lawyer/LawArticleList.vue'
import AdminArticle from '../views/admin/AdminArticle.vue'
import Login from '../views/auth/Login.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Home, name: 'Home' },
    { path: '/lawyer-list', component: LawyerList, name: 'LawyerList' },
    { path: '/lawyer', component: LawyerDetail, name: 'LawyerDetail' },
    { path: '/law-article-list', component: LawArticleList, name: 'LawArticleList' },
    { path: '/login', component: Login, name: 'Login' },
    { path: '/admin-articles', component: AdminArticle, name: 'AdminArticle' }
  ]
})

router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
  const currentUser = localStorage.getItem('currentUser')
  const requiresAuth = to.path.startsWith('/admin-articles')

  if (requiresAuth && !isLoggedIn) {
    next('/login')
  } else if (requiresAuth && isLoggedIn && currentUser) {
    const user = JSON.parse(currentUser)
    if (user.userType !== 3) {
      next('/login')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router