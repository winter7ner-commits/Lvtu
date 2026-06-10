<template>
  <header class="header">
    <div class="header-container">
      <!-- Logo -->
      <div class="logo">
        <img src="/icons/logo.png" alt="LVTU" class="logo-img" />
        <span class="logo-text">律途</span>
      </div>

      <!-- Navigation Menu -->
      <nav class="nav-menu">
        <router-link to="/" class="nav-item">首页</router-link>

        <template v-if="isLawyerAccount">
          <router-link v-if="canAccessLawyerOrders" to="/lawyer/orders/available" class="nav-item">接单大厅</router-link>
          <router-link v-if="canAccessLawyerOrders" to="/lawyer/orders/my" class="nav-item">我的接单</router-link>
        </template>

        <template v-else>
          <router-link to="/lawyer-list" class="nav-item">律师查询</router-link>
          <div class="nav-item-dropdown">
            <button class="nav-item dropdown-toggle">
              发布服务
              <i class="dropdown-icon">▼</i>
            </button>
            <div class="dropdown-menu">
              <button type="button" class="dropdown-item dropdown-button" @click="goProtected({ name: 'OrderCreate', query: { type: 'ONLINE_CONSULT' } })">在线法律咨询</button>
              <button type="button" class="dropdown-item dropdown-button" @click="goProtected({ name: 'OrderCreate', query: { type: 'PHONE_CONSULT' } })">电话法律咨询</button>
              <button type="button" class="dropdown-item dropdown-button" @click="goProtected({ name: 'OrderCreate', query: { type: 'DOCUMENT_WRITING' } })">文书代写</button>
              <button type="button" class="dropdown-item dropdown-button" @click="goProtected({ name: 'OrderCreate', query: { type: 'CONTRACT_REVIEW' } })">合同审核</button>
              <button type="button" class="dropdown-item dropdown-button" @click="goProtected({ name: 'OrderCreate', query: { type: 'MARRIAGE_FAMILY' } })">婚姻家事</button>
              <button type="button" class="dropdown-item dropdown-button" @click="goProtected({ name: 'OrderCreate', query: { type: 'LITIGATION_AGENT' } })">诉讼代理</button>
            </div>
          </div>
          <div class="nav-item-dropdown">
            <button class="nav-item dropdown-toggle">
              我的订单
              <i class="dropdown-icon">▼</i>
            </button>
            <div class="dropdown-menu">
              <button type="button" class="dropdown-item dropdown-button" @click="goProtected({ name: 'ClientOrderList' })">全部订单</button>
              <button type="button" class="dropdown-item dropdown-button" @click="goProtected({ name: 'ClientOrderList', query: { status: 'pending_payment' } })">待支付</button>
              <button type="button" class="dropdown-item dropdown-button" @click="goProtected({ name: 'ClientOrderList', query: { status: 'processing' } })">处理中</button>
              <button type="button" class="dropdown-item dropdown-button" @click="goProtected({ name: 'ClientOrderList', query: { status: 'pending_customer_confirmation' } })">待确认</button>
              <button type="button" class="dropdown-item dropdown-button" @click="goProtected({ name: 'ClientOrderList', query: { status: 'pending_review' } })">待评价</button>
            </div>
          </div>
        </template>

        <router-link v-if="isLoggedIn" to="/notifications" class="nav-item notification-nav-item">
          <span>消息通知</span>
          <em v-if="unreadCount > 0">{{ unreadCount > 99 ? '99+' : unreadCount }}</em>
        </router-link>
        <button v-else type="button" class="nav-item nav-button notification-nav-item" @click="goProtected({ name: 'NotificationList' })">
          <span>消息通知</span>
        </button>

        <router-link to="/help-center" class="nav-item">帮助中心</router-link>
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
        </template>

        <!-- Logged In - User Menu (Click Trigger) -->
        <div v-else class="user-menu-dropdown" :class="{ 'is-active': showUserMenu }">
          <button class="user-menu-btn" @click="toggleUserMenu">
            <img :src="userAvatar" :alt="userName" class="user-avatar" />
            <span>{{ userName }}</span>
            <i class="dropdown-icon">▼</i>
          </button>
          <div class="dropdown-menu user-dropdown">
            <router-link to="/notifications" class="dropdown-item user-notification-item" @click="showUserMenu = false">
              消息通知
              <span v-if="unreadCount > 0">{{ unreadCount > 99 ? '99+' : unreadCount }}</span>
            </router-link>
            <router-link to="/user-profile" class="dropdown-item" @click="showUserMenu = false">个人资料</router-link>
            <router-link to="/settings" class="dropdown-item" @click="showUserMenu = false">设置</router-link>
            <div class="dropdown-divider"></div>
            <a href="#" class="dropdown-item logout" @click.prevent="handleLogout">退出登录</a>
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
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../store/auth'
import { getLawyerProfileByUserId } from '@/api/lawyerOrder'
import { getUnreadNotificationCount } from '@/api/notification'
import { goWithLogin } from '@/utils/loginPrompt'

const router = useRouter()
const authStore = useAuthStore()
const showSearch = ref(false)
const searchQuery = ref('')
const showUserMenu = ref(false)
const lawyerProfileStatus = ref(null)
const unreadCount = ref(0)
let unreadTimer = null

const isLoggedIn = computed(() => authStore.isAuthenticated)
const userName = computed(() => authStore.user?.username || '我的')
const currentUserId = computed(() => authStore.user?.userId || Number(localStorage.getItem('userId')) || null)
const isLawyerApproved = computed(() => Number(authStore.user?.authStatus) === 2)
const isLawyerAccount = computed(() => isLoggedIn.value && isLawyerApproved.value)
const canAccessLawyerOrders = computed(() => isLawyerApproved.value && Number(lawyerProfileStatus.value) === 1)

// 使用本地默认头像，与 UserProfile.vue 保持一致
const defaultAvatar = '/icons/default-avatar.png'
const userAvatar = computed(() => authStore.user?.avatarUrl || defaultAvatar)

const loadLawyerProfileStatus = async () => {
  lawyerProfileStatus.value = null
  if (!currentUserId.value || !isLawyerApproved.value) return

  try {
    const response = await getLawyerProfileByUserId(currentUserId.value)
    const profile = response?.data?.data ?? response?.data ?? null
    lawyerProfileStatus.value = profile?.status ?? null
  } catch {
    lawyerProfileStatus.value = null
  }
}

const loadUnreadCount = async () => {
  if (!currentUserId.value || !isLoggedIn.value) {
    unreadCount.value = 0
    return
  }
  try {
    const response = await getUnreadNotificationCount(currentUserId.value)
    unreadCount.value = Number(response?.data?.data ?? response?.data ?? 0)
  } catch {
    unreadCount.value = 0
  }
}

watch([currentUserId, isLawyerApproved], loadLawyerProfileStatus, { immediate: true })
watch([currentUserId, isLoggedIn], loadUnreadCount, { immediate: true })

onMounted(() => {
  unreadTimer = window.setInterval(loadUnreadCount, 15000)
  window.addEventListener('notifications-updated', loadUnreadCount)
})

onBeforeUnmount(() => {
  if (unreadTimer) {
    window.clearInterval(unreadTimer)
  }
  window.removeEventListener('notifications-updated', loadUnreadCount)
})

const toggleSearch = () => {
  showSearch.value = !showSearch.value
}

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
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

const goProtected = (targetRoute) => {
  goWithLogin(isLoggedIn.value, router, targetRoute)
}

const handleLogout = () => {
  authStore.logout()
  showUserMenu.value = false
  lawyerProfileStatus.value = null
  unreadCount.value = 0
  window.dispatchEvent(new Event('auth-logout'))
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
  left: 0;
  right: 0;
  top: 100%;
  height: 12px;
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

/* 导航栏下拉菜单 - 悬停显示 */
.nav-item-dropdown:hover .dropdown-menu {
  opacity: 1;
  visibility: visible;
  pointer-events: auto;
  transform: translateY(0);
  transition-delay: 0s;
}

.nav-item-dropdown:hover .dropdown-toggle .dropdown-icon {
  transform: rotate(180deg);
}

/* 下拉菜单基础样式 */
.dropdown-menu {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 180px;
  padding: 8px 0;
  margin-top: 0;
  z-index: 200;
  background-clip: padding-box;
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
  width: 100%;
  padding: 10px 16px;
  border: 0;
  background: transparent;
  color: #333333;
  text-decoration: none;
  font-size: 14px;
  text-align: left;
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

.notification-nav-item {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.notification-nav-item em {
  position: absolute;
  right: -7px;
  top: -6px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  border-radius: 999px;
  background: #ef4444;
  color: #ffffff;
  font-size: 12px;
  font-style: normal;
  line-height: 18px;
  text-align: center;
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

/* User Menu - Click Interaction */
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

/* 用户菜单下拉 - 点击显示 */
.user-menu-dropdown.is-active .dropdown-menu {
  opacity: 1;
  visibility: visible;
  pointer-events: auto;
  transform: translateY(0);
  transition-delay: 0s;
}

.user-menu-dropdown.is-active .dropdown-icon {
  transform: rotate(180deg);
}

.user-dropdown {
  right: 0;
  left: auto;
}

.user-notification-item {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.user-notification-item span {
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  border-radius: 999px;
  background: #ef4444;
  color: #ffffff;
  font-size: 12px;
  line-height: 20px;
  text-align: center;
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
