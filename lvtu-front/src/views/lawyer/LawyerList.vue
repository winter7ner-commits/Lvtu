<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getLawyerList } from '@/api/lawyer'
import LawyerListCard from '@/components/lawyer/LawyerListCard.vue'

const route = useRoute()
const router = useRouter()

const list = ref([])
const keyword = ref('')
const loading = ref(false)
const selectedSpecialty = ref('')
const minRating = ref('')
const sortBy = ref('rating')

const ratingOptions = [
  { label: '全部评分', value: '' },
  { label: '4.5 分以上', value: 4.5 },
  { label: '4.0 分以上', value: 4 },
  { label: '3.5 分以上', value: 3.5 }
]

const sortOptions = [
  { label: '综合评分优先', value: 'rating' },
  { label: '执业年限优先', value: 'years' },
  { label: '姓名排序', value: 'name' }
]

const normalizeList = (res) => {
  const data = res?.data
  if (Array.isArray(data)) return data
  if (Array.isArray(data?.records)) return data.records
  if (Array.isArray(data?.list)) return data.list
  return []
}

const getSpecialtyNames = (lawyer) => {
  return (lawyer.specialties || [])
    .map((item) => item?.name || item?.specialtyName)
    .filter(Boolean)
}

const specialties = computed(() => {
  const names = list.value.flatMap(getSpecialtyNames)
  return [...new Set(names)].slice(0, 12)
})

const filteredList = computed(() => {
  const minScore = Number(minRating.value) || 0
  const result = list.value.filter((lawyer) => {
    const specialtyMatched = selectedSpecialty.value
      ? getSpecialtyNames(lawyer).includes(selectedSpecialty.value)
      : true
    const ratingMatched = minScore ? Number(lawyer.rating || 0) >= minScore : true
    return specialtyMatched && ratingMatched
  })

  return [...result].sort((a, b) => {
    if (sortBy.value === 'years') {
      return Number(b.practiceYears || 0) - Number(a.practiceYears || 0)
    }
    if (sortBy.value === 'name') {
      return String(a.name || '').localeCompare(String(b.name || ''), 'zh-Hans-CN')
    }
    return Number(b.rating || 0) - Number(a.rating || 0)
  })
})

const averageRating = computed(() => {
  if (!list.value.length) return '0.0'
  const total = list.value.reduce((sum, lawyer) => sum + Number(lawyer.rating || 0), 0)
  return (total / list.value.length).toFixed(1)
})

const seniorLawyerCount = computed(() => {
  return list.value.filter((lawyer) => Number(lawyer.practiceYears || 0) >= 5).length
})

const hasActiveFilter = computed(() => {
  return Boolean(keyword.value.trim() || selectedSpecialty.value || minRating.value)
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getLawyerList({ keyword: keyword.value.trim() })
    list.value = normalizeList(res)
  } catch (error) {
    console.error('加载律师列表失败:', error)
    list.value = []
    ElMessage.error('律师列表加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  const nextKeyword = keyword.value.trim()
  const currentKeyword = Array.isArray(route.query.keyword) ? route.query.keyword[0] : route.query.keyword

  if ((currentKeyword || '') === nextKeyword) {
    await loadData()
    return
  }

  await router.replace({
    name: 'LawyerList',
    query: nextKeyword ? { keyword: nextKeyword } : {}
  })
}

const selectSpecialty = (specialty) => {
  selectedSpecialty.value = selectedSpecialty.value === specialty ? '' : specialty
}

const clearFilters = async () => {
  keyword.value = ''
  selectedSpecialty.value = ''
  minRating.value = ''
  sortBy.value = 'rating'
  await router.replace({ name: 'LawyerList' })
  await loadData()
}

watch(
  () => route.query.keyword,
  async (value) => {
    const nextKeyword = Array.isArray(value) ? value[0] : value
    keyword.value = nextKeyword || ''
    await loadData()
  },
  { immediate: true }
)
</script>

<template>
  <main class="lawyer-page">
    <section class="page-head">
      <div>
        <p class="eyebrow">律师查询</p>
        <h1>匹配适合你的专业律师</h1>
        <p class="subtext">按姓名、律所、专长或地区快速查找律师，结合评分与执业经验筛选。</p>
      </div>

      <div class="summary-grid">
        <div class="summary-card">
          <span>律师数量</span>
          <strong>{{ list.length }}</strong>
        </div>
        <div class="summary-card">
          <span>平均评分</span>
          <strong>{{ averageRating }}</strong>
        </div>
        <div class="summary-card">
          <span>资深律师</span>
          <strong>{{ seniorLawyerCount }}</strong>
        </div>
      </div>
    </section>

    <section class="filter-panel">
      <div class="search-row">
        <el-input
          v-model="keyword"
          clearable
          size="large"
          placeholder="输入律师姓名、律所、专长或地区"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" size="large" class="search-btn" @click="handleSearch">搜索</el-button>
      </div>

      <div class="filter-row">
        <div class="specialty-filter">
          <span class="filter-label">常用领域</span>
          <button
            v-for="specialty in specialties"
            :key="specialty"
            type="button"
            :class="['specialty-chip', { active: selectedSpecialty === specialty }]"
            @click="selectSpecialty(specialty)"
          >
            {{ specialty }}
          </button>
          <span v-if="!specialties.length" class="muted">暂无领域数据</span>
        </div>

        <div class="select-group">
          <el-select v-model="minRating" placeholder="评分筛选" size="large">
            <el-option
              v-for="option in ratingOptions"
              :key="option.label"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
          <el-button :disabled="!hasActiveFilter" size="large" @click="clearFilters">清空</el-button>
        </div>
      </div>
    </section>

    <section class="result-panel" v-loading="loading">
      <div class="result-toolbar">
        <div>
          <strong>{{ filteredList.length }}</strong>
          <span> 位律师符合条件</span>
        </div>
        <el-select v-model="sortBy" class="sort-select" size="large">
          <el-option
            v-for="option in sortOptions"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>
      </div>

      <div v-if="filteredList.length" class="card-list">
        <LawyerListCard
          v-for="item in filteredList"
          :key="item.lawyerId"
          :lawyer="item"
        />
      </div>

      <el-empty
        v-else
        class="empty-state"
        description="暂无符合条件的律师"
      >
        <el-button type="primary" @click="clearFilters">重置筛选</el-button>
      </el-empty>
    </section>
  </main>
</template>

<style scoped>
.lawyer-page {
  min-height: 100vh;
  padding: 32px 20px 56px;
  background: #f5f7fb;
  color: #172033;
}

.page-head,
.filter-panel,
.result-panel {
  width: min(1120px, 100%);
  margin-left: auto;
  margin-right: auto;
}

.page-head {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: flex-end;
  margin-bottom: 22px;
}

.eyebrow {
  margin: 0 0 8px;
  color: #2563eb;
  font-size: 13px;
  font-weight: 800;
}

h1 {
  margin: 0;
  font-size: 30px;
  line-height: 1.25;
}

.subtext {
  margin: 10px 0 0;
  color: #667085;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, 96px);
  gap: 10px;
}

.summary-card {
  padding: 12px 10px;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
  text-align: center;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
}

.summary-card span {
  display: block;
  color: #667085;
  font-size: 12px;
}

.summary-card strong {
  display: block;
  margin-top: 5px;
  color: #1d4ed8;
  font-size: 22px;
}

.filter-panel,
.result-panel {
  border: 1px solid #e5eaf3;
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.filter-panel {
  padding: 18px;
  margin-bottom: 18px;
}

.search-row {
  display: flex;
  gap: 12px;
}

.search-row :deep(.el-input__wrapper),
.select-group :deep(.el-select__wrapper),
.sort-select :deep(.el-select__wrapper) {
  border-radius: 8px;
}

.search-btn {
  min-width: 108px;
  border-radius: 8px;
  font-weight: 700;
}

.filter-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-top: 16px;
}

.specialty-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.filter-label {
  margin-right: 2px;
  color: #344054;
  font-size: 14px;
  font-weight: 800;
}

.specialty-chip {
  padding: 7px 12px;
  border: 1px solid #dbe4f0;
  border-radius: 999px;
  background: #fafcff;
  color: #475467;
  cursor: pointer;
  font-size: 13px;
  font-weight: 700;
  transition: all 0.2s ease;
}

.specialty-chip:hover,
.specialty-chip.active {
  border-color: #2563eb;
  background: #eff6ff;
  color: #1d4ed8;
}

.muted {
  color: #98a2b3;
  font-size: 13px;
}

.select-group {
  display: flex;
  gap: 10px;
  flex: 0 0 auto;
}

.select-group :deep(.el-select) {
  width: 150px;
}

.result-panel {
  min-height: 320px;
  padding: 18px;
}

.result-toolbar {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  margin-bottom: 16px;
  color: #667085;
}

.result-toolbar strong {
  color: #1d4ed8;
  font-size: 20px;
}

.sort-select {
  width: 172px;
}

.card-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.empty-state {
  padding: 54px 0 44px;
}

@media (max-width: 900px) {
  .page-head {
    flex-direction: column;
    align-items: stretch;
  }

  .summary-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .filter-row {
    flex-direction: column;
  }

  .select-group {
    width: 100%;
  }

  .select-group :deep(.el-select) {
    flex: 1;
    width: auto;
  }
}

@media (max-width: 640px) {
  .lawyer-page {
    padding: 20px 12px 40px;
  }

  h1 {
    font-size: 24px;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }

  .search-row,
  .result-toolbar,
  .select-group {
    flex-direction: column;
  }

  .search-btn,
  .sort-select {
    width: 100%;
  }
}
</style>
