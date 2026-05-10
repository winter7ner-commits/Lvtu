<<<<<<< HEAD
<template>
  <div class="lawyer-list">
    <h2>律师列表</h2>
    <div class="lawyer-grid">
      <div class="lawyer-card" v-for="lawyer in lawyers" :key="lawyer.id">
        <img :src="lawyer.avatar" :alt="lawyer.name" class="lawyer-avatar" />
        <h3>{{ lawyer.name }}</h3>
        <p class="lawyer-title">{{ lawyer.title }}</p>
        <p class="lawyer-specialty">{{ lawyer.specialty }}</p>
        <button @click="goToDetail(lawyer.id)" class="view-detail-btn">查看详情</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const lawyers = ref([
  { id: 1, name: '张明律师', title: '高级律师', specialty: '民事纠纷', avatar: 'https://via.placeholder.com/100' },
  { id: 2, name: '李华律师', title: '资深律师', specialty: '刑事辩护', avatar: 'https://via.placeholder.com/100' },
  { id: 3, name: '王芳律师', title: '执业律师', specialty: '知识产权', avatar: 'https://via.placeholder.com/100' },
  { id: 4, name: '陈伟律师', title: '高级律师', specialty: '公司法务', avatar: 'https://via.placeholder.com/100' }
])

const goToDetail = (id) => {
  router.push({ name: 'LawyerDetail', params: { id } })
}
</script>

<style scoped>
.lawyer-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.lawyer-list h2 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #1e40af;
}

.lawyer-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.lawyer-card {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
  transition: transform 0.3s ease;
}

.lawyer-card:hover {
  transform: translateY(-5px);
}

.lawyer-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-bottom: 15px;
}

.lawyer-card h3 {
  font-size: 18px;
  margin: 0 0 8px 0;
  color: #333;
}

.lawyer-title {
  font-size: 14px;
  color: #2563eb;
  margin: 0 0 8px 0;
}

.lawyer-specialty {
  font-size: 13px;
  color: #666;
  margin: 0 0 15px 0;
}

.view-detail-btn {
  background: #2563eb;
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s ease;
}

.view-detail-btn:hover {
  background: #1e40af;
=======
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
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
}
</style>