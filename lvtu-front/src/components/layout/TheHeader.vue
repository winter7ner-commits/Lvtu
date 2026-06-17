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
        <router-link to="/laws" class="nav-item">法律法规</router-link>

        <template v-if="isLawyerAccount">
          <router-link v-if="canAccessLawyerOrders" to="/lawyer/orders/available" class="nav-item">接单大厅</router-link>
          <router-link v-if="canAccessLawyerOrders" to="/lawyer/orders/my" class="nav-item">我的接单</router-link>
        </template>

        <template v-else>
          <router-link to="/lawyer-list" class="nav-item">律师查询</router-link>
          <div class="nav-item-dropdown">
            <button type="button" class="nav-item dropdown-toggle">
              发布服务
              <span class="dropdown-icon" aria-hidden="true"></span>
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
            <button type="button" class="nav-item dropdown-toggle">
              我的订单
              <span class="dropdown-icon" aria-hidden="true"></span>
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

        <router-link to="/about" class="nav-item">关于我们</router-link>
      </nav>

      <!-- Right Actions -->
      <div class="header-right">
        <!-- Not Logged In -->
        <template v-if="!isLoggedIn">
          <router-link to="/login" class="login-btn auth-action">登录</router-link>
          <router-link to="/register" class="signup-btn auth-action">注册</router-link>
        </template>

        <!-- Logged In - User Menu (Click Trigger) -->
        <div v-else class="user-menu-dropdown" :class="{ 'is-active': showUserMenu }">
          <button type="button" class="user-menu-btn" @click="toggleUserMenu">
            <img :src="userAvatar" :alt="userName" class="user-avatar" />
            <span>{{ userName }}</span>
            <span class="dropdown-icon" aria-hidden="true"></span>
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

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
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
  background: transparent;
  box-shadow: none;
  position: sticky;
  top: 0;
  z-index: 100;
  padding: 22px 15px 0;
}

.header-container {
  max-width: 1450px;
  margin: 0 auto;
  padding: 0 12px 0 18px;
  height: 68px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #1e40af 0%, #2563eb 100%);
  border-radius: 4px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  flex-shrink: 0;
  min-width: 210px;
  height: 100%;
}

.logo-img {
  width: 46px;
  height: 46px;
  border-radius: 0;
  object-fit: contain;
}

.logo-text {
  font-size: 25px;
  line-height: 1;
  font-weight: bold;
  color: #ffffff;
  letter-spacing: 0.5px;
}

/* Navigation Menu */
.nav-menu {
  display: flex;
  gap: 16px;
  flex: 1;
  justify-content: center;
  align-items: center;
  min-width: 0;
  height: 100%;
  margin-left: 26px;
}

.nav-item {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  font-size: 15px;
  line-height: 1;
  font-weight: 500;
  transition: all 0.3s ease;
  min-height: 34px;
  padding: 0 6px;
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
  display: flex;
  align-items: center;
  height: 100%;
}

.nav-item-dropdown::after,
.user-menu-dropdown::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  top: calc(100% - 2px);
  height: 18px;
}

.dropdown-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
}

.dropdown-icon {
  width: 14px;
  height: 14px;
  flex: 0 0 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  color: currentColor;
  opacity: 0.86;
  transition:
    transform 0.18s ease,
    opacity 0.18s ease,
    background-color 0.18s ease;
}

.dropdown-icon::before {
  content: '';
  width: 6px;
  height: 6px;
  border-right: 1.8px solid currentColor;
  border-bottom: 1.8px solid currentColor;
  border-radius: 1px;
  transform: translateY(-1px) rotate(45deg);
}

.dropdown-toggle:hover .dropdown-icon,
.user-menu-btn:hover .dropdown-icon {
  opacity: 1;
  background-color: rgba(255, 255, 255, 0.16);
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
  top: calc(100% + 6px);
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
  justify-content: flex-end;
  gap: 10px;
  flex-shrink: 0;
  min-width: 150px;
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
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: 2px solid rgba(255, 255, 255, 0.8);
  color: rgba(255, 255, 255, 0.9);
  min-height: 34px;
  padding: 0 18px;
  border-radius: 4px;
  font-size: 14px;
  line-height: 1;
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
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
  border: none;
  color: #1e40af;
  min-height: 34px;
  padding: 0 18px;
  border-radius: 4px;
  font-size: 14px;
  line-height: 1;
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
  min-height: 38px;
  padding: 0 12px;
  border-radius: 4px;
  font-size: 14px;
  line-height: 1;
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
  width: 30px;
  height: 30px;
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

/* Responsive */
@media (max-width: 1024px) {
  .header {
    padding: 16px 12px 0;
  }

  .header-container {
    max-width: calc(100% - 0px);
    padding: 0 10px 0 14px;
  }

  .logo {
    min-width: 150px;
  }

  .logo-img {
    width: 40px;
    height: 40px;
  }

  .logo-text {
    font-size: 21px;
  }

  .nav-menu {
    gap: 4px;
    margin-left: 12px;
  }

  .nav-item {
    padding: 0 8px;
    font-size: 13px;
  }
}

@media (max-width: 768px) {
  .header {
    padding: 10px 10px 0;
  }

  .nav-menu {
    display: none;
  }

  .header-container {
    height: 60px;
    padding: 0 10px 0 12px;
  }

  .header-right {
    min-width: 0;
    gap: 8px;
  }

  .login-btn,
  .signup-btn {
    min-height: 32px;
    padding: 0 12px;
    font-size: 12px;
  }
}
</style>
