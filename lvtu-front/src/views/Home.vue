<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const categories = ref([])

onMounted(() => {
  loadCategories()
})

const loadCategories = () => {
  categories.value = [
    { id: 1, name: '宪法' },
    { id: 2, name: '民法' },
    { id: 3, name: '刑法' },
    { id: 4, name: '行政法' },
    { id: 5, name: '商法' },
    { id: 6, name: '民事诉讼法' },
    { id: 7, name: '刑事诉讼法' },
    { id: 8, name: '行政诉讼法' }
  ]
}

const goToLawArticle = (categoryId) => {
  router.push({ path: '/law-article-list', query: { category: categoryId } })
}
</script>

<template>
  <div class="home-container">
    <div class="hero-section">
      <div class="hero-content">
        <h1>律途 - 您身边的法律专家</h1>
        <p>专业的法律服务平台，为您提供全面的法律法规查询与咨询服务</p>
        <router-link to="/law-article-list" class="btn-primary">开始查询</router-link>
      </div>
    </div>

    <div class="features-section">
      <h2>平台特色</h2>
      <div class="features-grid">
        <div class="feature-card">
          <span class="feature-icon">📚</span>
          <h3>法律法规查询</h3>
          <p>涵盖宪法、民法、刑法等各类法律法规</p>
        </div>
        <div class="feature-card">
          <span class="feature-icon">⚖️</span>
          <h3>专业法律解读</h3>
          <p>资深律师团队提供专业解读和案例分析</p>
        </div>
        <div class="feature-card">
          <span class="feature-icon">💬</span>
          <h3>在线咨询服务</h3>
          <p>提供在线法律咨询，解答您的法律疑问</p>
        </div>
      </div>
    </div>

    <div class="quick-access">
      <h2>快速访问</h2>
      <div class="category-grid">
        <div 
          v-for="category in categories" 
          :key="category.id"
          class="category-card"
          @click="goToLawArticle(category.id)"
        >
          <span class="category-name">{{ category.name }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  min-height: 100vh;
  padding: 2rem;
}

.hero-section {
  background: linear-gradient(135deg, #e8f4fc 0%, #d4e8f5 100%);
  padding: 4rem 2rem;
  text-align: center;
  border-radius: 12px;
  margin-bottom: 2rem;
}

.hero-content h1 {
  font-size: 2.5rem;
  color: #1e88e5;
  margin-bottom: 1rem;
}

.hero-content p {
  font-size: 1.1rem;
  color: #555;
  margin-bottom: 2rem;
}

.btn-primary {
  display: inline-block;
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
  color: white;
  padding: 0.8rem 2rem;
  border-radius: 4px;
  text-decoration: none;
  font-weight: bold;
  transition: background 0.3s;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #42a5f5 0%, #1e88e5 100%);
}

.features-section,
.quick-access {
  max-width: 1200px;
  margin: 2rem auto;
}

.features-section h2,
.quick-access h2 {
  text-align: center;
  color: #1e88e5;
  margin-bottom: 2rem;
  font-size: 1.8rem;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2rem;
}

.feature-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  text-align: center;
}

.feature-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.feature-card h3 {
  color: #1e88e5;
  margin-bottom: 0.5rem;
}

.feature-card p {
  color: #666;
  font-size: 0.9rem;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
}

.category-card {
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
  color: white;
  padding: 1.5rem;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 20px rgba(30,136,229,0.5);
}

.category-name {
  font-size: 1.1rem;
  font-weight: bold;
}
</style>
