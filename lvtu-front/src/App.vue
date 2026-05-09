<script setup>
// 导入头部导航组件
import TheHeader from './components/layout/TheHeader.vue'
</script>

<template>
  <div class="app-container">
    <!-- 顶部导航栏 -->
    <TheHeader />
    <!-- 主内容区域：路由视图显示 -->
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

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