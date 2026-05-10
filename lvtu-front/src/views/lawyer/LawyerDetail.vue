<<<<<<< HEAD
<template>
  <div class="lawyer-detail">
    <button @click="goBack" class="back-btn">← 返回列表</button>
    
    <div class="lawyer-info">
      <img :src="lawyer.avatar" :alt="lawyer.name" class="lawyer-avatar" />
      <div class="lawyer-content">
        <h2>{{ lawyer.name }}</h2>
        <p class="lawyer-title">{{ lawyer.title }}</p>
        <p class="lawyer-specialty">专业领域：{{ lawyer.specialty }}</p>
        <p class="lawyer-experience">从业年限：{{ lawyer.experience }}年</p>
        <p class="lawyer-intro">{{ lawyer.intro }}</p>
        <button class="contact-btn">联系律师</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const lawyer = ref({
  id: 1,
  name: '张明律师',
  title: '高级律师',
  specialty: '民事纠纷',
  experience: 15,
  avatar: 'https://via.placeholder.com/150',
  intro: '专注于民商事纠纷解决，拥有丰富的诉讼经验，成功代理各类案件数百起，深受客户信赖。'
})

onMounted(() => {
  const lawyerData = [
    { id: 1, name: '张明律师', title: '高级律师', specialty: '民事纠纷', experience: 15, avatar: 'https://via.placeholder.com/150', intro: '专注于民商事纠纷解决，拥有丰富的诉讼经验，成功代理各类案件数百起，深受客户信赖。' },
    { id: 2, name: '李华律师', title: '资深律师', specialty: '刑事辩护', experience: 12, avatar: 'https://via.placeholder.com/150', intro: '专业刑事辩护律师，擅长各类刑事案件的辩护工作，为当事人提供专业的法律帮助。' },
    { id: 3, name: '王芳律师', title: '执业律师', specialty: '知识产权', experience: 8, avatar: 'https://via.placeholder.com/150', intro: '专注于知识产权领域，包括专利、商标、著作权等方面的法律服务。' },
    { id: 4, name: '陈伟律师', title: '高级律师', specialty: '公司法务', experience: 20, avatar: 'https://via.placeholder.com/150', intro: '资深公司法务律师，为企业提供全方位的法律顾问服务，包括合同审查、股权设计等。' }
  ]
  
  const id = parseInt(route.params.id) || 1
  lawyer.value = lawyerData.find(l => l.id === id) || lawyerData[0]
})

const goBack = () => {
  router.push('/lawyer-list')
}
</script>

<style scoped>
.lawyer-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.back-btn {
  background: none;
  border: none;
  color: #2563eb;
  font-size: 14px;
  cursor: pointer;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.back-btn:hover {
  text-decoration: underline;
}

.lawyer-info {
  display: flex;
  gap: 30px;
  background: #ffffff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.lawyer-avatar {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  flex-shrink: 0;
}

.lawyer-content {
  flex: 1;
}

.lawyer-content h2 {
  font-size: 24px;
  margin: 0 0 10px 0;
  color: #333;
}

.lawyer-title {
  font-size: 16px;
  color: #2563eb;
  margin: 0 0 10px 0;
}

.lawyer-specialty,
.lawyer-experience {
  font-size: 14px;
  color: #666;
  margin: 0 0 8px 0;
}

.lawyer-intro {
  font-size: 14px;
  color: #555;
  line-height: 1.6;
  margin: 15px 0 20px 0;
}

.contact-btn {
  background: #2563eb;
  color: white;
  border: none;
  padding: 10px 30px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background 0.3s ease;
}

.contact-btn:hover {
  background: #1e40af;
}

@media (max-width: 600px) {
  .lawyer-info {
    flex-direction: column;
    text-align: center;
  }
}
</style>
=======
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getLawyerDetail } from '@/api/lawyer'

const route = useRoute()
const detail = ref(null)

onMounted(() => {
  const id = route.params.id
  getLawyerDetail(id).then(res => {
    detail.value = res.data
  })
})
</script>

<template>
  <div v-if="detail">
    <h2>{{ detail.name }}</h2>

    <p>ID：{{ detail.lawyerId }}</p>
    <p>事务所：{{ detail.lawFirm }}</p>
    <p>执业年限：{{ detail.practiceYears }} 年</p>
    <p>评分：⭐{{ detail.rating }}</p>

    <p>擅长领域：
      <span v-for="specialty in detail.specialties" :key="specialty.id || specialty.specialtyId">
        {{ specialty.name }}
      </span>
    </p>
  </div>
</template>
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
