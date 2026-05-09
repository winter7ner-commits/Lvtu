// 导入Vue Router的核心函数
import { createRouter, createWebHistory } from 'vue-router'
// 导入页面组件
import Home from '../views/Home.vue'
import LawyerList from '../views/lawyer/LawyerList.vue'
import LawyerDetail from '../views/lawyer/LawyerDetail.vue'
import LawArticleList from '../views/lawyer/LawArticleList.vue'

// 创建路由实例：使用HTML5历史模式
const router = createRouter({
  history: createWebHistory(),
  // 定义路由规则
  routes: [
    // 首页
    { path: '/', component: Home, name: 'Home' },
    // 律师列表页
    { path: '/lawyer-list', component: LawyerList, name: 'LawyerList' },
    // 律师详情页
    { path: '/lawyer', component: LawyerDetail, name: 'LawyerDetail' },
    // 法律法规列表页
    { path: '/law-article-list', component: LawArticleList, name: 'LawArticleList' }
  ]
})

// 导出路由实例供main.js使用
export default router