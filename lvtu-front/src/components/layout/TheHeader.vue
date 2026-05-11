<template>
  <header class="header">
    <div class="header-container">
      <!-- Logo -->
      <div class="logo" @click="goHome">
        <img src="/public/icons/logo.png" alt="LVTU" class="logo-img" />
        <span class="logo-text">律途</span>
      </div>

      <!-- Navigation Menu -->
      <nav class="nav-menu">
        <router-link to="/" class="nav-item" @click="closeDropdowns">首页</router-link>
        
        <!-- Laws & Regulations Dropdown -->
        <div
          class="nav-item-dropdown"
          :class="{ 'is-open': activeDropdown === 'laws' }"
          @mouseenter="openDropdown('laws')"
          @mouseleave="scheduleCloseDropdown"
          @focusin="openDropdown('laws')"
          @focusout="scheduleCloseDropdown"
        >
          <button class="nav-item dropdown-toggle" type="button">
            法律法规
            <i class="dropdown-icon">▼</i>
          </button>
          <div class="dropdown-menu">
            <a href="#" class="dropdown-item" @click.prevent="closeDropdowns">第一编 总则</a>
            <a href="#" class="dropdown-item" @click.prevent="closeDropdowns">第二编 物权</a>
            <a href="#" class="dropdown-item" @click.prevent="closeDropdowns">第三编 合同</a>
            <a href="#" class="dropdown-item" @click.prevent="closeDropdowns">第四编 人格权</a>
            <a href="#" class="dropdown-item" @click.prevent="closeDropdowns">第五编 婚姻家庭</a>
            <a href="#" class="dropdown-item" @click.prevent="closeDropdowns">第六编 继承</a>
            <a href="#" class="dropdown-item" @click.prevent="closeDropdowns">第七编 侵权责任</a>
          </div>
        </div>

        <!-- Services Dropdown -->
        <div
          class="nav-item-dropdown"
          :class="{ 'is-open': activeDropdown === 'services' }"
          @mouseenter="openDropdown('services')"
          @mouseleave="scheduleCloseDropdown"
          @focusin="openDropdown('services')"
          @focusout="scheduleCloseDropdown"
        >
          <button class="nav-item dropdown-toggle" type="button">
            服务
            <i class="dropdown-icon">▼</i>
          </button>
          <div class="dropdown-menu">
            <router-link :to="{ name: 'OrderCreate', query: { type: 'ONLINE_CONSULT' } }" class="dropdown-item" @click="closeDropdowns">在线法律咨询</router-link>
            <router-link :to="{ name: 'OrderCreate', query: { type: 'PHONE_CONSULT' } }" class="dropdown-item" @click="closeDropdowns">电话法律咨询</router-link>
            <router-link :to="{ name: 'OrderCreate', query: { type: 'DOCUMENT_WRITING' } }" class="dropdown-item" @click="closeDropdowns">文书代写</router-link>
            <router-link :to="{ name: 'OrderCreate', query: { type: 'CONTRACT_REVIEW' } }" class="dropdown-item" @click="closeDropdowns">合同审核</router-link>
            <router-link :to="{ name: 'OrderCreate', query: { type: 'MARRIAGE_FAMILY' } }" class="dropdown-item" @click="closeDropdowns">婚姻家事</router-link>
            <router-link :to="{ name: 'OrderCreate', query: { type: 'LITIGATION_AGENT' } }" class="dropdown-item" @click="closeDropdowns">诉讼代理</router-link>
          </div>
        </div>
        <router-link to="/lawyer-list" class="nav-item" @click="closeDropdowns">律师</router-link>
        
        <!-- Orders Dropdown -->
        <div
          class="nav-item-dropdown"
          :class="{ 'is-open': activeDropdown === 'orders' }"
          @mouseenter="openDropdown('orders')"
          @mouseleave="scheduleCloseDropdown"
          @focusin="openDropdown('orders')"
          @focusout="scheduleCloseDropdown"
        >
          <button class="nav-item dropdown-toggle" type="button">
            订单
            <i class="dropdown-icon">▼</i>
          </button>
          <div class="dropdown-menu">
            <a href="#" class="dropdown-item" @click.prevent="closeDropdowns">全部</a>
            <a href="#" class="dropdown-item" @click.prevent="closeDropdowns">待支付</a>
            <a href="#" class="dropdown-item" @click.prevent="closeDropdowns">待回复</a>
            <a href="#" class="dropdown-item" @click.prevent="closeDropdowns">待评价</a>
            <a href="#" class="dropdown-item" @click.prevent="closeDropdowns">已完成</a>
          </div>
        </div>

        <router-link to="/about" class="nav-item" @click="closeDropdowns">关于我们</router-link>
      </nav>

      <!-- Right Actions -->
      <div class="header-right">
        <button class="search-btn" @click="toggleSearch">
          <i class="icon-search">🔍</i>
        </button>
        
        <!-- Not Logged In -->
        <template v-if="!isLoggedIn">
          <button class="login-btn" type="button" @click="handleLogin">登录</button>
          <button class="signup-btn" type="button" @click="handleSignup">注册</button>
        </template>

        <!-- Logged In - User Menu -->
        <div
          v-else
          class="user-menu-dropdown"
          :class="{ 'is-open': activeDropdown === 'user' }"
          @mouseenter="openDropdown('user')"
          @mouseleave="scheduleCloseDropdown"
          @focusin="openDropdown('user')"
          @focusout="scheduleCloseDropdown"
        >
          <button class="user-menu-btn" type="button">
            <img :src="userAvatar" :alt="userName" class="user-avatar" />
            <span>{{ userName }}</span>
            <i class="dropdown-icon">▼</i>
          </button>
          <div class="dropdown-menu user-dropdown">
            <router-link to="/user-profile" class="dropdown-item" @click="closeDropdowns">个人资料</router-link>
            <router-link to="/settings" class="dropdown-item" @click="closeDropdowns">设置</router-link>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const showSearch = ref(false)
const searchQuery = ref('')
const isLoggedIn = ref(false)
const userName = ref('我的')
const userAvatar = ref('https://via.placeholder.com/32')
const activeDropdown = ref(null)
let closeDropdownTimer = null

const openDropdown = (name) => {
  if (closeDropdownTimer) {
    clearTimeout(closeDropdownTimer)
    closeDropdownTimer = null
  }
  activeDropdown.value = name
}

const scheduleCloseDropdown = () => {
  if (closeDropdownTimer) {
    clearTimeout(closeDropdownTimer)
  }
  closeDropdownTimer = setTimeout(() => {
    activeDropdown.value = null
    closeDropdownTimer = null
  }, 120)
}

const closeDropdowns = () => {
  if (closeDropdownTimer) {
    clearTimeout(closeDropdownTimer)
    closeDropdownTimer = null
  }
  activeDropdown.value = null
}

onMounted(() => {
  // 从本地存储检查登录状态
  const loginStatus = localStorage.getItem('isLoggedIn')
  const userInfo = localStorage.getItem('userInfo')
  
  if (loginStatus === 'true' && userInfo) {
    isLoggedIn.value = true
    const user = JSON.parse(userInfo)
    userName.value = user.name || '我的'
    userAvatar.value = user.avatar || 'https://via.placeholder.com/32'
  }
})

const toggleSearch = () => {
  closeDropdowns()
  showSearch.value = !showSearch.value
}

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    closeDropdowns()
    router.push({
      name: 'LawyerList',
      query: { keyword: searchQuery.value }
    })
    showSearch.value = false
  }
}

const goHome = () => {
  closeDropdowns()
  router.push({ name: 'Home' })
}

const handleLogin = () => {
  closeDropdowns()
  router.push('/login')
}

const handleSignup = () => {
  closeDropdowns()
  router.push('/signup')
}

const handleLogout = (e) => {
  e.preventDefault()
  closeDropdowns()
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('userInfo')
  isLoggedIn.value = false
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

.nav-item-dropdown::after,
.user-menu-dropdown::after {
  content: '';
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  height: 8px;
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

.nav-item-dropdown.is-open .dropdown-menu,
.user-menu-dropdown.is-open .dropdown-menu {
  opacity: 1;
  visibility: visible;
  pointer-events: auto;
  transform: translateY(0);
  transition-delay: 0s;
}

.nav-item-dropdown.is-open .dropdown-toggle .dropdown-icon,
.user-menu-dropdown.is-open .dropdown-icon {
  transform: rotate(180deg);
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  background: #ffffff;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 180px;
  padding: 8px 0;
  z-index: 200;
  opacity: 0;
  visibility: hidden;
  pointer-events: none;
  transform: translateY(-6px);
  transition:
    opacity 0.18s ease,
    transform 0.18s ease,
    visibility 0s linear 0.18s;
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

/* 确保 router-link 在 dropdown 中正确显示 */
.dropdown-menu .router-link {
  display: block;
  padding: 10px 16px;
  color: #333333;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.dropdown-menu .router-link:hover {
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
