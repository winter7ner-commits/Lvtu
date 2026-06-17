<template>
  <div class="admin-app" :class="{ 'is-auth-route': isAuthRoute }">
    <template v-if="isAuthRoute">
      <RouterView />
    </template>
    <template v-else>
      <AdminHeader />
      <div class="admin-main">
        <RouterView />
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AdminHeader from './components/AdminHeader.vue'

const route = useRoute()
const isAuthRoute = computed(() => route.path === '/admin/login')
</script>

<style>
:root {
  --admin-primary: #2563eb;
  --admin-primary-deep: #1e40af;
  --admin-primary-text: #1d4ed8;
  --admin-surface: #ffffff;
  --admin-bg: #f5f7fb;
  --admin-panel: #f6f8fc;
  --admin-soft-blue: #eff6ff;
  --admin-soft-blue-border: #d6e4ff;
  --admin-border: #e5eaf3;
  --admin-divider: #edf1f7;
  --admin-text: #172033;
  --admin-text-secondary: #344054;
  --admin-text-muted: #667085;
  --admin-text-disabled: #98a2b3;
  --admin-success: #15803d;
  --admin-success-bg: #f0fdf4;
  --admin-warning: #92400e;
  --admin-warning-bg: #fef3c7;
  --admin-danger: #b42318;
  --admin-danger-bg: #fff1f0;
  --admin-radius-sm: 6px;
  --admin-radius-md: 8px;
  --admin-radius-lg: 12px;
  --admin-shadow-panel: 0 8px 24px rgba(15, 23, 42, 0.06);
  --admin-shadow-soft: 0 5px 18px rgba(30, 55, 90, 0.08);
}

* {
  box-sizing: border-box;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

*::-webkit-scrollbar {
  width: 0;
  height: 0;
  display: none;
}

html {
  min-width: 320px;
  background: var(--admin-bg);
}

body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif;
  color: var(--admin-text);
  background: var(--admin-bg);
  font-size: 14px;
  line-height: 1.6;
  letter-spacing: 0;
  text-rendering: geometricPrecision;
}

button,
input,
select,
textarea {
  font: inherit;
}

.admin-app {
  min-height: 100vh;
  background: var(--admin-bg);
}

.admin-main {
  min-height: 100vh;
  margin-left: 256px;
}

.admin-app.is-auth-route .admin-main {
  margin-left: 0;
}

@media (max-width: 860px) {
  .admin-main {
    margin-left: 0;
  }
}
</style>
