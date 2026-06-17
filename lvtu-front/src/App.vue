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
      <router-view v-slot="{ Component, route: activeRoute }">
        <Transition :name="isHomeRoute ? 'home-view' : 'page-view'" mode="out-in">
          <component :is="Component" :key="activeRoute.fullPath" />
        </Transition>
      </router-view>
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
  --home-header-overlap: 106px;
  background: var(--page-shell-bg);
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
  animation: homeHeaderFloatIn 820ms cubic-bezier(0.22, 1, 0.36, 1) both;
}

.app-main {
  flex: 1;
  width: 100%;
}

.app-container.home-shell .app-main {
  margin-top: calc(var(--home-header-overlap) * -1);
}

.page-view-enter-active,
.page-view-leave-active {
  transition:
    opacity 0.18s cubic-bezier(0.22, 1, 0.36, 1),
    transform 0.18s cubic-bezier(0.22, 1, 0.36, 1);
}

.home-view-enter-active,
.home-view-leave-active {
  transition:
    opacity 0.3s cubic-bezier(0.22, 1, 0.36, 1),
    transform 0.3s cubic-bezier(0.22, 1, 0.36, 1);
}

.page-view-enter-from {
  opacity: 0.94;
  transform: translateY(8px);
}

.page-view-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

.home-view-enter-from {
  opacity: 0.96;
  transform: translateY(6px);
}

.home-view-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

@keyframes homeHeaderFloatIn {
  from {
    opacity: 0;
    transform: translate3d(0, 18px, 0);
  }

  to {
    opacity: 1;
    transform: translate3d(0, 0, 0);
  }
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

@media (max-width: 1024px) {
  .app-container.home-shell {
    --home-header-overlap: 96px;
  }

  .app-container.home-shell .header {
    padding: 16px 12px 12px;
  }
}

@media (max-width: 768px) {
  .app-container.home-shell {
    --home-header-overlap: 80px;
  }

  .app-container.home-shell .header {
    padding: 10px 10px 10px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .app-container.home-shell .header-container {
    animation-duration: 0.01ms !important;
  }

  .page-view-enter-active,
  .page-view-leave-active,
  .home-view-enter-active,
  .home-view-leave-active {
    transition-duration: 0.01ms !important;
  }

  .page-view-enter-from,
  .page-view-leave-to,
  .home-view-enter-from,
  .home-view-leave-to {
    opacity: 1;
    transform: none;
  }
}
</style>
