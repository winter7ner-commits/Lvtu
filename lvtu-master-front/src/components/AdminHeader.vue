<template>
  <header class="header">
    <div class="header-container">
      <router-link to="/law-management" class="logo">
        <img src="/icons/logo.png" alt="LVTU" class="logo-img" />
        <span class="logo-text">律途</span>
      </router-link>

      <nav class="nav-menu">
        <router-link to="/law-management" class="nav-item">法条管理</router-link>
        <router-link to="/users" class="nav-item">用户管理</router-link>
        <router-link to="/auth-audit" class="nav-item">认证审核</router-link>
        <router-link to="/orders" class="nav-item">订单查询</router-link>
        <router-link to="/settlements" class="nav-item">结算管理</router-link>
      </nav>

      <div class="header-right">
        <button class="search-btn" type="button">🔍</button>
        <template v-if="!isLoggedIn">
          <router-link to="/admin/login" class="login-btn">登录</router-link>
          <router-link to="/admin/register" class="signup-btn">注册</router-link>
        </template>
        <div v-else class="user-menu-dropdown">
          <button class="user-menu-btn" type="button">
            <span class="avatar-text">admin</span>
            <span>{{ userName }}</span>
            <i class="dropdown-icon">▼</i>
          </button>
          <div class="dropdown-menu user-dropdown">
            <a href="#" class="dropdown-item logout" @click="handleLogout">退出登录</a>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'

const router = useRouter()
const authStore = useAuthStore()

const isLoggedIn = computed(() => authStore.isAuthenticated)
const userName = computed(() => authStore.user?.username || 'admin')

const handleLogout = (event) => {
  event.preventDefault()
  authStore.logout()
  router.push('/admin/login')
}
</script>

<style scoped>
.header {
  background: linear-gradient(135deg, #1e40af 0%, #2563eb 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.16);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-container {
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  flex-shrink: 0;
}

.logo-img {
  width: 44px;
  height: 44px;
  border-radius: 50%;
}

.logo-text {
  color: #fff;
  font-size: 25px;
  font-weight: 800;
  letter-spacing: 2px;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  margin-left: 44px;
}

.nav-item {
  color: rgba(255, 255, 255, 0.88);
  text-decoration: none;
  font-size: 15px;
  font-weight: 700;
  padding: 10px 18px;
  border-radius: 4px;
}

.nav-item:hover,
.nav-item.router-link-active {
  color: #fff;
  background: rgba(255, 255, 255, 0.2);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-shrink: 0;
}

.search-btn {
  border: 0;
  background: transparent;
  color: #fff;
  font-size: 19px;
  cursor: pointer;
}

.login-btn,
.signup-btn {
  min-width: 76px;
  text-align: center;
  padding: 10px 18px;
  border-radius: 4px;
  font-size: 15px;
  font-weight: 700;
  text-decoration: none;
}

.login-btn {
  color: #fff;
  border: 2px solid rgba(255, 255, 255, 0.86);
}

.signup-btn {
  color: #1e40af;
  background: #fff;
}

.user-menu-dropdown {
  position: relative;
}

.user-menu-btn {
  display: flex;
  align-items: center;
  gap: 9px;
  border: 0;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  padding: 7px 14px 7px 8px;
  font-weight: 700;
  cursor: pointer;
}

.avatar-text {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.25);
  font-size: 11px;
}

.dropdown-icon {
  font-size: 12px;
}

.dropdown-menu {
  display: none;
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 140px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.16);
  padding: 8px 0;
}

.user-menu-dropdown:hover .dropdown-menu {
  display: block;
}

.dropdown-item {
  display: block;
  padding: 10px 16px;
  color: #333;
  text-decoration: none;
}

.logout {
  color: #ef4444;
}

@media (max-width: 900px) {
  .nav-menu {
    gap: 0;
    margin-left: 18px;
  }

  .nav-item {
    padding: 8px 10px;
    font-size: 13px;
  }
}
</style>
