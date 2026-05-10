<script setup>
<<<<<<< HEAD
// 导入头部导航组件
import TheHeader from './components/layout/TheHeader.vue'
=======
import TheHeader from './components/layout/TheHeader.vue'
import TheFooter from './components/layout/TheFooter.vue'
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
</script>

<template>
  <div class="app-container">
<<<<<<< HEAD
    <!-- 顶部导航栏 -->
    <TheHeader />
    <!-- 主内容区域：路由视图显示 -->
    <main class="main-content">
      <router-view />
    </main>
=======
    <TheHeader />
    <main class="app-main">
      <router-view />
    </main>
    <TheFooter />
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
  </div>
</template>

<style>
<<<<<<< HEAD
/* 全局样式重置 */
=======
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

<<<<<<< HEAD
body {
  /* 字体配置：支持多系统 */
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f7fa;
  min-height: 100vh;
}

/* 应用容器：最小高度100vh */
.app-container {
  min-height: 100vh;
}

/* 主内容区域：减去导航栏高度 */
.main-content {
  min-height: calc(100vh - 70px);
}
</style>
=======
html, body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto Mono', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #ffffff;
}

.app-main {
  flex: 1;
  width: 100%;
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
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
