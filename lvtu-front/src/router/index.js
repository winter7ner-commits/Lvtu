import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import LawyerList from '../views/lawyer/LawyerList.vue'
import LawyerDetail from '../views/lawyer/LawyerDetail.vue'
import LawArticleList from '../views/lawyer/LawArticleList.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Home, name: 'Home' },
    { path: '/lawyer-list', component: LawyerList, name: 'LawyerList' },
    { path: '/lawyer', component: LawyerDetail, name: 'LawyerDetail' },
    { path: '/law-article-list', component: LawArticleList, name: 'LawArticleList' }
  ]
})

export default router
