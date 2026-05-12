import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import LawyerList from '../views/lawyer/LawyerList.vue'
import LawyerDetail from '../views/lawyer/LawyerDetail.vue'
import LawArticleList from '../views/lawyer/LawArticleList.vue'
import Login from '../views/auth/Login.vue'
import Register from '../views/auth/Register.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Home, name: 'Home' },
    { path: '/lawyer-list', component: LawyerList, name: 'LawyerList' },
    { path: '/lawyer', component: LawyerDetail, name: 'LawyerDetail' },
    { path: '/law-article-list', component: LawArticleList, name: 'LawArticleList' },
    { path: '/login', component: Login, name: 'Login' },
    { path: '/register', component: Register, name: 'Register' }
  ]
})

export default router