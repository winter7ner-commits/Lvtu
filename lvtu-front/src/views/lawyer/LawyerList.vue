<script setup>
import { ref, onMounted } from 'vue'
import { getLawyerList } from '@/api/lawyer'
import LawyerListCard from '@/components/lawyer/LawyerListCard.vue'

const list = ref([])
const keyword = ref('')

// 加载律师数据
const loadData = () => {
  getLawyerList({ keyword: keyword.value }).then(res => {
    list.value = res.data
  })
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="container">
    <!-- 标题 -->
    <div class="header">
      <h1 class="title">律师查询</h1>
    </div>

    <!-- 搜索和筛选栏 -->
    <div class="filter-section">
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="搜索律师或搜索关键词" />
        <el-button type="primary" @click="loadData" class="search-btn">搜索</el-button>
      </div>
    </div>

    <!-- 卡片列表 -->
    <div class="card-list">
      <div 
        v-if="list.length === 0"
        class="empty-state"
      >
        <p>暂无相关律师信息</p>
      </div>
      
      <LawyerListCard
        v-for="item in list"
        :key="item.lawyerId"
        :lawyer="item"
      />
    </div>
  </div>
</template>

<style scoped>
.container {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  background: linear-gradient(135deg, #f0f4f8 0%, #ffffff 100%);
  min-height: 100vh;
}

/* 标题 */
.header {
  margin-bottom: 30px;
  padding: 20px 0;
}

.title {
  font-size: 32px;
  font-weight: 600;
  color: #1890ff;
  margin: 0;
}

/* 搜索和筛选区域 */
.filter-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 25px;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.08);
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.search-bar :deep(.el-input) {
  flex: 1;
}

.search-bar :deep(.el-input__wrapper) {
  background-color: #f5f7fa;
  border-color: #d9e1e8;
}

.search-btn {
  background-color: #1890ff;
  border-color: #1890ff;
  padding: 0 30px;
}

.search-btn:hover {
  background-color: #0050b3;
  border-color: #0050b3;
}

/* 卡片列表 */
.card-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    max-width: 100%;
    padding: 15px;
  }

  .title {
    font-size: 24px;
  }

  .search-bar {
    flex-direction: column;
  }
}
</style>