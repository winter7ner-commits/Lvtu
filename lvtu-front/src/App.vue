<script setup>
import TheHeader from './components/layout/TheHeader.vue'
</script>

<template>
  <div class="app-container">
    <TheHeader />
    <main class="main-content">
      <router-view />
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

.app-container {
  min-height: 100vh;
}

.main-content {
  min-height: calc(100vh - 70px);
}
</style>