<script setup>
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../../store/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const navItems = [
  { path: '/categories', label: '法律分类' },
  { path: '/documents', label: '法律文件' },
  { path: '/articles', label: '法律条文' }
]

const isActive = (path) => route.path === path

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

const goToHome = () => {
  window.location.href = 'http://localhost:5181'
}
</script>

<template>
  <header class="admin-header">
    <div class="header-left">
      <h1 class="logo">律途 - 管理员后台</h1>
    </div>
    <nav class="header-nav">
      <ul>
        <li v-for="item in navItems" :key="item.path">
          <router-link :to="item.path" :class="{ active: isActive(item.path) }">
            {{ item.label }}
          </router-link>
        </li>
      </ul>
    </nav>
    <div class="header-right">
      <button class="home-btn" @click="goToHome">返回主页</button>
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </div>
  </header>
</template>

<style scoped>
.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 70px;
  padding: 0 2rem;
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
  color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header-left .logo {
  font-size: 1.4rem;
  font-weight: bold;
  margin: 0;
}

.header-nav ul {
  list-style: none;
  display: flex;
  gap: 2rem;
  margin: 0;
  padding: 0;
}

.header-nav a {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background 0.3s;
}

.header-nav a:hover,
.header-nav a.active {
  background: rgba(255, 255, 255, 0.2);
}

.home-btn {
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  padding: 0.6rem 1.2rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
  margin-right: 0.5rem;
}

.home-btn:hover {
  background: rgba(255, 255, 255, 0.25);
}

.logout-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  padding: 0.6rem 1.2rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}
</style>