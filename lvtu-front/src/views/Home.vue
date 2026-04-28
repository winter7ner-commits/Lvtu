<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        
        <!-- Search Bar -->
        <div class="hero-search">
          <input 
            v-model="searchKeyword"
            type="text" 
            placeholder="输入律师名称、专长或地区..." 
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <button class="search-button" @click="handleSearch">
            <i class="icon-search"></i>
            搜索律师
          </button>
        </div>

        <!-- Quick Tags -->
        <div class="quick-tags">
          <span class="tag" @click="searchByKeyword('婚姻法')">婚姻法</span>
          <span class="tag" @click="searchByKeyword('刑事案件')">刑事案件</span>
          <span class="tag" @click="searchByKeyword('合同纠纷')">合同纠纷</span>
          <span class="tag" @click="searchByKeyword('知识产权')">知识产权</span>
        </div>
      </div>
      <div class="hero-image">
        <img src="/src/assets/images/logo.png" alt="律师服务" />
      </div>
    </section>        
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getTopRatedLawyers } from '@/api/lawyer'

const router = useRouter()
const searchKeyword = ref('')
const hotLawyers = ref([])
const loading = ref(false)



const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/lawyer-list',
      query: { keyword: searchKeyword.value }
    })
  }
}

const searchByKeyword = (keyword) => {
  router.push({
    path: '/lawyer-list',
    query: { keyword }
  })
}

const goToLawyerDetail = (id) => {
  router.push(`/lawyer/${id}`)
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  loadHotLawyers()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #ffffff;
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, #1e40af 0%, #2563eb 50%, #60a5fa 100%);
  padding: 80px 20px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.hero-content {
  color: #ffffff;
  animation: slideInLeft 0.6s ease-out;
}

.hero-search {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
}

.search-input {
  flex: 1;
  padding: 14px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: all 0.3s ease;
}

.search-input:focus {
  box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.2);
}

.search-button {
  background: #ffffff;
  color: #1e40af;
  border: none;
  padding: 14px 30px;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.quick-tags {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.tag {
  background: rgba(255, 255, 255, 0.2);
  color: #ffffff;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.tag:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.hero-image {
  animation: slideInRight 0.6s ease-out;
}

.hero-image img {
  width: 100%;
  height: auto;
  object-fit: cover;
}



/* Animations */
@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Responsive */
@media (max-width: 768px) {
  .hero-section {
    grid-template-columns: 1fr;
    padding: 50px 15px;
    gap: 30px;
  }

  .hero-search {
    flex-direction: column;
  }

  .search-button {
    justify-content: center;
  }

}

@media (max-width: 480px) {

  .quick-tags {
    gap: 8px;
  }

  .tag {
    font-size: 12px;
    padding: 6px 12px;
  }


}
</style>
