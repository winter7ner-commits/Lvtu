<template>
  <!-- 顶部导航栏 -->
  <header class="header">
    <div class="header-container">
      <!-- Logo区域：点击返回首页 -->
      <div class="logo" @click="goToHome">
        <img src="/public/icons/sign.png" alt="LVTU" class="logo-img" />
        <span class="logo-text">律途</span>
      </div>

      <!-- 主导航菜单 -->
      <nav class="nav-menu">
        <!-- 首页链接 -->
        <router-link to="/" class="nav-item">首页</router-link>

        <!-- 法律法规下拉菜单 -->
        <div class="nav-item-dropdown">
          <button class="nav-item dropdown-toggle">
            法律法规
            <i class="dropdown-icon">▼</i>
          </button>
          <!-- 下拉选项：通过category参数传递分类ID -->
          <div class="dropdown-menu">
            <router-link to="/law-article-list" class="dropdown-item" @click="goToLawArticle(1)">宪法</router-link>
            <router-link to="/law-article-list" class="dropdown-item" @click="goToLawArticle(2)">民法</router-link>
            <router-link to="/law-article-list" class="dropdown-item" @click="goToLawArticle(3)">刑法</router-link>
            <router-link to="/law-article-list" class="dropdown-item" @click="goToLawArticle(4)">行政法</router-link>
            <router-link to="/law-article-list" class="dropdown-item" @click="goToLawArticle(5)">商法</router-link>
            <router-link to="/law-article-list" class="dropdown-item" @click="goToLawArticle(6)">民事诉讼法</router-link>
            <router-link to="/law-article-list" class="dropdown-item" @click="goToLawArticle(7)">刑事诉讼法</router-link>
            <router-link to="/law-article-list" class="dropdown-item" @click="goToLawArticle(8)">行政诉讼法</router-link>
          </div>
        </div>

        <!-- 服务下拉菜单 -->
        <div class="nav-item-dropdown">
          <button class="nav-item dropdown-toggle">
            服务
          </button>
        </div>

        <!-- 律师列表链接 -->
        <router-link to="/lawyer-list" class="nav-item">律师</router-link>

        <!-- 订单下拉菜单 -->
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

        <!-- 关于我们链接 -->
        <router-link to="/about" class="nav-item">关于我们</router-link>
      </nav>

      <!-- 右侧操作区：搜索和用户登录 -->
      <div class="header-right">
        <!-- 搜索按钮 -->
        <button class="search-btn" @click="toggleSearch">
          <i class="icon-search">🔍</i>
        </button>

        <!-- 未登录状态：显示登录和注册按钮 -->
        <template v-if="!isLoggedIn">
          <button class="login-btn" @click="handleLogin">登录</button>
          <button class="signup-btn" @click="handleSignup">注册</button>
        </template>

        <!-- 已登录状态：显示用户菜单 -->
        <div v-else class="user-menu-dropdown">
          <button class="user-menu-btn">
            <img :src="userAvatar" :alt="userName" class="user-avatar" />
            <span>{{ userName }}</span>
            <i class="dropdown-icon">▼</i>
          </button>
          <!-- 用户下拉菜单 -->
          <div class="dropdown-menu user-dropdown">
            <a href="/user-profile" class="dropdown-item">个人资料</a>
            <a href="/settings" class="dropdown-item">设置</a>
            <div class="dropdown-divider"></div>
            <a href="#" class="dropdown-item logout" @click="handleLogout">退出登录</a>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索栏：显示/隐藏 -->
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
// Vue组合式API导入
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

// 路由实例
const router = useRouter()

// 响应式状态
const showSearch = ref(false)      // 搜索栏显示状态
const searchQuery = ref('')         // 搜索关键词
const isLoggedIn = ref(false)      // 登录状态
const userName = ref('我的')       // 用户名称
const userAvatar = ref('https://via.placeholder.com/32')  // 用户头像

// 组件挂载时：检查本地存储的登录状态
onMounted(() => {
  const loginStatus = localStorage.getItem('isLoggedIn')
  const userInfo = localStorage.getItem('userInfo')

  if (loginStatus === 'true' && userInfo) {
    isLoggedIn.value = true
    const user = JSON.parse(userInfo)
    userName.value = user.name || '我的'
    userAvatar.value = user.avatar || 'https://via.placeholder.com/32'
  }
})

// 切换搜索栏显示/隐藏
const toggleSearch = () => {
  showSearch.value = !showSearch.value
}

// 处理搜索提交：跳转到律师列表页
const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({
      name: 'LawyerList',
      query: { keyword: searchQuery.value }
    })
    showSearch.value = false
  }
}

// 跳转到登录页
const handleLogin = () => {
  router.push('/login')
}

// 跳转到注册页
const handleSignup = () => {
  router.push('/signup')
}

// 处理退出登录
const handleLogout = (e) => {
  e.preventDefault()
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('userInfo')
  isLoggedIn.value = false
  router.push('/')
}

// 返回首页
const goToHome = () => {
  router.push('/')
}

// 跳转到法律法规列表页（带分类参数）
const goToLawArticle = (categoryId) => {
  router.push({
    name: 'LawArticleList',
    query: { category: categoryId }
  })
}
</script>

<style scoped>
/* 导航栏整体样式：蓝色渐变背景，固定在顶部 */
.header {
  background: linear-gradient(135deg, #1e40af 0%, #2563eb 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

/* 导航栏容器：最大宽度1400px，居中显示 */
.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* Logo样式 */
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

/* 导航菜单样式 */
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

/* 下拉菜单样式 */
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

/* 下拉动画 */
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

/* 右侧操作区样式 */
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

/* 用户菜单样式 */
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

/* 搜索栏样式 */
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

/* 响应式布局：平板和手机端适配 */
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