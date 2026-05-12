<script setup>
import { useAuthStore } from './store/auth'
import TheHeader from './components/layout/TheHeader.vue'
import { RouterView } from 'vue-router'

const authStore = useAuthStore()
</script>

<template>
  <div class="admin-app">
    <TheHeader v-if="authStore.isLoggedIn" />
    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.admin-app {
  min-height: 100vh;
}

.main-content {
  min-height: calc(100vh - 70px);
}
</style>