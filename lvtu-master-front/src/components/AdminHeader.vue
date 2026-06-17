<template>
  <aside class="admin-sidebar" aria-label="后台主导航">
    <router-link :to="homePath" class="brand">
      <img src="/icons/logo.png" alt="律途" class="brand-logo" />
      <span class="brand-copy">
        <strong>律途后台</strong>
        <small>法律服务管理台</small>
      </span>
    </router-link>

    <nav class="side-nav">
      <section v-for="group in groupedMenus" :key="group.title" class="nav-section">
        <p class="nav-section-title">{{ group.title }}</p>
        <router-link
          v-for="item in group.items"
          :key="item.path"
          :to="item.path"
          class="nav-item"
        >
          <span class="nav-mark">{{ item.label.slice(0, 1) }}</span>
          <span>{{ item.label }}</span>
        </router-link>
      </section>
    </nav>

    <div class="sidebar-footer">
      <template v-if="!isLoggedIn">
        <router-link to="/admin/login" class="login-link">登录后台</router-link>
      </template>
      <template v-else>
        <div class="user-card">
          <span class="avatar-text">{{ userInitial }}</span>
          <span class="user-meta">
            <strong>{{ userName }}</strong>
            <small>{{ roleLabel }}</small>
          </span>
        </div>
        <button class="logout-btn" type="button" @click="handleLogout">退出登录</button>
      </template>
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { ADMIN_MENU_ITEMS, ADMIN_ROLE_LABELS, canAccessAdminRoute, firstAdminPath, normalizeAdminRole } from '../utils/adminPermissions'

const router = useRouter()
const authStore = useAuthStore()

const NAV_GROUPS = [
  { title: '运营管理', labels: ['用户管理', '订单查询', '平台介入', '结算管理', '评价管理'] },
  { title: '内容管理', labels: ['法条管理', '法条浏览', '解释反馈'] },
  { title: '审核专区', labels: ['认证审核'] },
  { title: '权限与系统', labels: ['角色管理', '系统设置'] }
]

const isLoggedIn = computed(() => authStore.isAuthenticated)
const userName = computed(() => authStore.user?.username || 'admin')
const userInitial = computed(() => userName.value.slice(0, 1).toUpperCase())
const roleLabel = computed(() => ADMIN_ROLE_LABELS[normalizeAdminRole(authStore.user)] || '管理员')
const visibleMenus = computed(() => ADMIN_MENU_ITEMS.filter((item) => canAccessAdminRoute(authStore.user, item.roles)))
const homePath = computed(() => isLoggedIn.value ? firstAdminPath(authStore.user) : '/admin/login')

const groupedMenus = computed(() => {
  const visible = visibleMenus.value
  const used = new Set()
  const groups = NAV_GROUPS.map((group) => {
    const items = visible.filter((item) => {
      const matched = group.labels.includes(item.label)
      if (matched) used.add(item.path)
      return matched
    })
    return { title: group.title, items }
  }).filter((group) => group.items.length)

  const rest = visible.filter((item) => !used.has(item.path))
  if (rest.length) groups.push({ title: '其他', items: rest })
  return groups
})

const handleLogout = () => {
  authStore.logout()
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-sidebar {
  position: fixed;
  inset: 0 auto 0 0;
  z-index: 80;
  width: 256px;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  border-right: 1px solid #e5eaf3;
  color: #172033;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  min-height: 76px;
  padding: 18px 20px;
  color: inherit;
  text-decoration: none;
  border-bottom: 1px solid #edf1f7;
}

.brand-logo {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  border: 1px solid #d6e4ff;
}

.brand-copy {
  min-width: 0;
  display: grid;
  gap: 2px;
}

.brand-copy strong {
  font-size: 17px;
  line-height: 1.25;
  letter-spacing: 0;
}

.brand-copy small,
.user-meta small,
.nav-section-title {
  color: #667085;
}

.brand-copy small {
  font-size: 12px;
}

.side-nav {
  flex: 1;
  overflow-y: auto;
  padding: 18px 12px;
}

.nav-section + .nav-section {
  margin-top: 18px;
}

.nav-section-title {
  margin: 0 0 8px;
  padding: 0 10px;
  font-size: 12px;
  font-weight: 800;
  line-height: 1.4;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  min-height: 42px;
  padding: 9px 10px;
  border-radius: 6px;
  color: #344054;
  font-size: 14px;
  font-weight: 700;
  text-decoration: none;
  transition: background-color 0.18s ease, color 0.18s ease;
}

.nav-item:hover {
  background: #f6f8fc;
  color: #1d4ed8;
}

.nav-item.router-link-active,
.nav-item.router-link-exact-active {
  background: #eff6ff;
  color: #1d4ed8;
}

.nav-mark {
  width: 24px;
  height: 24px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
  border-radius: 6px;
  background: #f6f8fc;
  color: #667085;
  font-size: 12px;
  font-weight: 800;
}

.nav-item.router-link-active .nav-mark,
.nav-item.router-link-exact-active .nav-mark {
  background: #2563eb;
  color: #ffffff;
}

.sidebar-footer {
  padding: 14px 14px 18px;
  border-top: 1px solid #edf1f7;
  background: #fbfcff;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
  padding: 10px;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
}

.avatar-text {
  width: 34px;
  height: 34px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
  border-radius: 8px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 12px;
  font-weight: 900;
}

.user-meta {
  min-width: 0;
  display: grid;
  gap: 2px;
}

.user-meta strong,
.user-meta small {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-meta strong {
  font-size: 14px;
  line-height: 1.3;
}

.user-meta small {
  font-size: 12px;
}

.logout-btn,
.login-link {
  width: 100%;
  min-height: 38px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-top: 10px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 800;
  text-decoration: none;
}

.logout-btn {
  border: 1px solid #e5eaf3;
  background: #ffffff;
  color: #b42318;
  cursor: pointer;
}

.logout-btn:hover {
  border-color: #f1b8b4;
  background: #fff1f0;
}

.login-link {
  background: #2563eb;
  color: #ffffff;
}

@media (max-width: 860px) {
  .admin-sidebar {
    position: static;
    width: 100%;
    min-height: auto;
    border-right: 0;
    border-bottom: 1px solid #e5eaf3;
  }

  .brand {
    min-height: 64px;
  }

  .side-nav {
    display: flex;
    gap: 12px;
    overflow-x: auto;
    padding: 0 12px 12px;
  }

  .nav-section {
    min-width: max-content;
  }

  .nav-section + .nav-section {
    margin-top: 0;
  }

  .nav-section-title {
    display: none;
  }

  .nav-item {
    display: inline-flex;
  }

  .sidebar-footer {
    display: none;
  }
}
</style>
