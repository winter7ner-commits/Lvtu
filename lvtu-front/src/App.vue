<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import TheHeader from './components/layout/TheHeader.vue'
import TheFooter from './components/layout/TheFooter.vue'

const route = useRoute()
const isHomeRoute = computed(() => route.name === 'Home')
</script>

<template>
  <div :class="['app-container', { 'home-shell': isHomeRoute, 'subpage-shell': !isHomeRoute }]">
    <TheHeader />
    <main class="app-main">
      <router-view />
    </main>
    <TheFooter />
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto Mono', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.app-container {
  --page-shell-bg: #f5f7fb;
  --page-shell-bg-soft: #eef3fb;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: var(--page-shell-bg);
}

.app-container.home-shell {
  background:
    linear-gradient(180deg, #2f6edf 0, #2f6edf 132px, #f5f7fb 470px, #f5f7fb 100%);
}

.app-container.subpage-shell {
  background:
    linear-gradient(180deg, var(--page-shell-bg-soft) 0, var(--page-shell-bg) 150px, var(--page-shell-bg) 100%);
}

.app-container.home-shell .header {
  padding: 24px 26px 14px;
  background: transparent;
}

.app-container.home-shell .header-container {
  max-width: 1560px;
  border-radius: 6px;
  box-shadow: 0 18px 42px rgba(15, 23, 42, 0.18);
}

.app-main {
  flex: 1;
  width: 100%;
}

.app-container.subpage-shell .app-main {
  background:
    linear-gradient(180deg, rgba(238, 243, 251, 0.96) 0, var(--page-shell-bg) 220px, var(--page-shell-bg) 100%);
}

.app-container.subpage-shell .app-main > :where(
  .about-page,
  .search-page,
  .lawyer-page,
  .law-article-page,
  .lawyer-detail-page,
  .notification-page,
  .profile-page,
  .login-page,
  .auth-page,
  .page,
  .order-page,
  .my-orders-page,
  .detail-page,
  .client-order-list,
  .evaluation-dashboard
) {
  background-color: transparent;
}

a {
  text-decoration: none;
  color: inherit;
}

button {
  font-family: inherit;
}

input, textarea, select {
  font-family: inherit;
}
</style>
