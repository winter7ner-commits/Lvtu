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
}
</style>