<template>
  <header class="header">
    <div class="header-container">
      <!-- Logo -->
      <div class="logo">
        <img src="/public/icons/logo.png" alt="LVTU" class="logo-img" />
        <span class="logo-text">律途</span>
      </div>

      <!-- Navigation Menu -->
      <nav class="nav-menu">
        <router-link to="/" class="nav-item">首页</router-link>
        
        <!-- Laws & Regulations Dropdown -->
        <div class="nav-item-dropdown">
          <button class="nav-item dropdown-toggle">
            法律法规
            <i class="dropdown-icon">▼</i>
          </button>
          <div class="dropdown-menu">
            <a href="#" class="dropdown-item">第一编 总则</a>
            <a href="#" class="dropdown-item">第二编 物权</a>
            <a href="#" class="dropdown-item">第三编 合同</a>
            <a href="#" class="dropdown-item">第四编 人格权</a>
            <a href="#" class="dropdown-item">第五编 婚姻家庭</a>
            <a href="#" class="dropdown-item">第六编 继承</a>
            <a href="#" class="dropdown-item">第七编 侵权责任</a>
          </div>
        </div>

        <!-- Services Dropdown -->
        <div class="nav-item-dropdown">
          <button class="nav-item dropdown-toggle">
            服务
          </button>
        </div>
        <router-link to="/lawyer-list" class="nav-item">律师</router-link>
        
        <!-- Orders Dropdown -->
        <div class="nav-item-dropdown">
          <button class="nav-item dropdown-toggle">
            订单
            <i class="dropdown-icon">▼</i>
          </button>
          <div class="dropdown-menu">
            <a href="#" class="dropdown-item">全部</a>
            <a href="#" class="dropdown-item">待支付</a>
            <a href="#" class="dropdown-item">待回复</a>
            <a href="#" class="dropdown-item">待评价</a>
            <a href="#" class="dropdown-item">已完成</a>
          </div>
        </div>

        <router-link to="/about" class="nav-item">关于我们</router-link>
      </nav>

      <!-- Right Actions -->
      <div class="header-right">
        <button class="search-btn" @click="toggleSearch">
          <i class="icon-search">🔍</i>
        </button>
        
        <!-- Not Logged In -->
        <template v-if="!isLoggedIn">
          <router-link to="/login" class="login-btn auth-action">登录</router-link>
          <router-link to="/register" class="signup-btn auth-action">注册</router-link>
          <!-- <router-link to="/admin/login" class="admin-login-btn auth-action">管理员登录</router-link> -->
        </template>

        <!-- Logged In - User Menu -->
        <div v-else class="user-menu-dropdown">
          <button class="user-menu-btn">
            <img :src="userAvatar" :alt="userName" class="user-avatar" />
            <span>{{ userName }}</span>
            <i class="dropdown-icon">▼</i>
          </button>
          <div class="dropdown-menu user-dropdown">
            <router-link to="/user-profile" class="dropdown-item">个人资料</router-link>
            <router-link to="/settings" class="dropdown-item">设置</router-link>
            <div class="dropdown-divider"></div>
            <a href="#" class="dropdown-item logout" @click="handleLogout">退出登录</a>
          </div>
        </div>
      </div>
    </div>

    <!-- Search Bar (Hidden by default) -->
    <div v-if="showSearch" class="search-bar">
      <input 
        type="text" 
        placeholder="搜索律师..." 
        class="search-input"
        v-model="searchQuery"
        @keyup.enter="handleSearch"
      />
      <button class="search-submit" @click="handleSearch">搜索</button>
    </div>
  </header>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../store/auth'

const router = useRouter()
const authStore = useAuthStore()
const showSearch = ref(false)
const searchQuery = ref('')

const isLoggedIn = computed(() => authStore.isAuthenticated)
const userName = computed(() => authStore.user?.username || '我的')
const userAvatar = computed(() => authStore.user?.avatarUrl || 'https://via.placeholder.com/32')

const toggleSearch = () => {
  showSearch.value = !showSearch.value
}

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({
      name: 'LawyerList',
      query: { keyword: searchQuery.value }
    })
    showSearch.value = false
  }
}

const handleLogin = () => {
  router.push('/login')
}

const handleSignup = () => {
  router.push('/register')
}

const handleLogout = (e) => {
  e.preventDefault()
  authStore.logout()
  router.push('/')
}
</script>

<style scoped>
.header {
  background: linear-gradient(135deg, #1e40af 0%, #2563eb 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  flex-shrink: 0;
}

.logo-img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: #ffffff;
  letter-spacing: 2px;
}

/* Navigation Menu */
.nav-menu {
  display: flex;
  gap: 5px;
  flex: 1;
  justify-content: flex-start;
  align-items: center;
  margin-left: 40px;
}

.nav-item {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  padding: 8px 16px;
  border-radius: 4px;
  white-space: nowrap;
  border: none;
  background: none;
  cursor: pointer;
}

.nav-item:hover {
  color: #ffffff;
  background-color: rgba(255, 255, 255, 0.2);
}

.nav-item.router-link-active {
  color: #ffffff;
  background-color: rgba(255, 255, 255, 0.25);
  font-weight: 600;
}

/* Dropdown Menu */
.nav-item-dropdown {
  position: relative;
}

.dropdown-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
}

.dropdown-icon {
  font-size: 12px;
  display: inline-block;
  transition: transform 0.3s ease;
}

.nav-item-dropdown:hover .dropdown-menu {
  display: block;
  animation: slideDown 0.3s ease-out;
}

.nav-item-dropdown:hover .dropdown-toggle .dropdown-icon {
  transform: rotate(180deg);
}

.dropdown-menu {
  display: none;
  position: absolute;
  top: 100%;
  left: 0;
  background: #ffffff;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 180px;
  padding: 8px 0;
  margin-top: 8px;
  z-index: 200;
  border-top: 8px solid transparent;
  background-clip: padding-box;
}

.dropdown-item {
  display: block;
  padding: 10px 16px;
  color: #333333;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.dropdown-item:hover {
  background-color: #f3f4f6;
  color: #1e40af;
  padding-left: 20px;
}

.dropdown-divider {
  height: 1px;
  background-color: #e5e7eb;
  margin: 8px 0;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Header Right */
.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-shrink: 0;
}

.search-btn {
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.9);
  font-size: 18px;
  cursor: pointer;
  transition: color 0.3s ease;
  padding: 8px;
  border-radius: 4px;
}

.search-btn:hover {
  color: #ffffff;
  background-color: rgba(255, 255, 255, 0.2);
}

.login-btn {
  background: transparent;
  border: 2px solid rgba(255, 255, 255, 0.8);
  color: rgba(255, 255, 255, 0.9);
  padding: 8px 20px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.login-btn:hover {
  border-color: #ffffff;
  color: #ffffff;
  background-color: rgba(255, 255, 255, 0.1);
}

.signup-btn {
  background: #ffffff;
  border: none;
  color: #1e40af;
  padding: 8px 20px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.signup-btn:hover {
  background: #f3f4f6;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.admin-login-btn {
  background: #ff6b35;
  border: none;
  color: #ffffff;
  padding: 8px 20px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.admin-login-btn:hover {
  background: #e55a2b;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* User Menu */
.user-menu-dropdown {
  position: relative;
}

.user-menu-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: #ffffff;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.user-menu-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.user-menu-dropdown:hover .dropdown-menu {
  display: block;
}

.user-menu-dropdown:hover .dropdown-toggle .dropdown-icon {
  transform: rotate(180deg);
}

.user-dropdown {
  right: 0;
  left: auto;
}

.logout {
  color: #ef4444;
}

.logout:hover {
  background-color: #fee2e2;
}

/* Search Bar */
.search-bar {
  background: rgba(255, 255, 255, 0.95);
  padding: 15px 20px;
  display: flex;
  gap: 10px;
  max-width: 1400px;
  margin: 0 auto;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.search-input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s ease;
}

.search-input:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.search-submit {
  background: #2563eb;
  color: #ffffff;
  border: none;
  padding: 10px 25px;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-submit:hover {
  background: #1e40af;
  box-shadow: 0 2px 8px rgba(30, 64, 175, 0.2);
}

/* Responsive */
@media (max-width: 1024px) {
  .nav-menu {
    gap: 0;
    margin-left: 20px;
  }

  .nav-item {
    padding: 8px 12px;
    font-size: 13px;
  }
}

@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }

  .header-container {
    height: 60px;
    padding: 0 15px;
  }

  .search-bar {
    flex-direction: column;
  }

  .header-right {
    gap: 10px;
  }

  .login-btn,
  .signup-btn {
    padding: 6px 12px;
    font-size: 12px;
  }
}
</style>
