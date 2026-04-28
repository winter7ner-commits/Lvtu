import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import LawyerList from '../views/lawyer/LawyerList.vue'
import LawyerDetail from '../views/lawyer/LawyerDetail.vue'
//import UserProfile from '../views/user/UserProfile.vue'
//import Login from '../views/auth/Login.vue'
//import Register from '../views/auth/Register.vue'
//import Settings from '../views/Settings.vue'

import TheHeader from '../components/layout/TheHeader.vue'
import TheFooter from '../components/layout/TheFooter.vue'
//import TheSidebar from '../components/layout/TheSidebar.vue'


const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Home, name: 'Home' },
    { path: '/lawyer-list', component: LawyerList, name: 'LawyerList' },
    { path: '/lawyer', component: LawyerDetail, name: 'LawyerDetail' },
    //{ path: '/user-profile', component: UserProfile, name: 'UserProfile' },
    //{ path: '/login', component: Login, name: 'Login' },
    //{ path: '/register', component: Register, name: 'Register' },
    //{ path: '/settings', component: Settings, name: 'Settings' }
  ]
})

export default router