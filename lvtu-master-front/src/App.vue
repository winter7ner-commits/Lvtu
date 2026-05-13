<template>
  <div class="admin-app">
    <AdminHeader />
    <RouterView />
  </div>
</template>

<script setup>
import AdminHeader from './components/AdminHeader.vue'
</script>

<style>
* {
  box-sizing: border-box;
}

body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif;
  background: #f6f7fb;
}

.admin-app {
  min-height: 100vh;
}
</style>
