<template>
  <main class="search-page">
    <section class="search-header">
      <el-button class="back-btn" @click="handleBack">返回</el-button>
      <div class="header-copy">
        <p class="eyebrow">全站搜索</p>
        <h1>搜索结果</h1>
        <p>按关键词检索律师、个人订单与法条。</p>
      </div>
      <div class="search-bar">
        <el-input
          v-model="keywordInput"
          clearable
          size="large"
          placeholder="输入律师、法条、订单号、业务类型或状态"
          @keyup.enter="submitSearch"
        />
        <el-button type="primary" size="large" :loading="searching" @click="submitSearch">
          搜索
        </el-button>
      </div>
    </section>

    <section class="results-shell" v-loading="searching">
      <div class="section-head">
        <div>
          <p class="eyebrow">搜索结果</p>
          <h2 v-if="activeKeyword">“{{ activeKeyword }}”相关内容</h2>
          <h2 v-else>输入关键词开始搜索</h2>
        </div>
        <span>{{ totalResultCount }} 条结果</span>
      </div>

      <el-empty v-if="!activeKeyword && !searching" description="请输入关键词开始搜索" />

      <div v-else class="result-stack">
        <article class="result-panel">
          <div class="panel-title">
            <h3>律师</h3>
            <el-tag effect="plain">{{ lawyerResults.length }}</el-tag>
          </div>
          <div v-if="lawyerResults.length" class="result-list">
            <button
              v-for="lawyer in lawyerResults"
              :key="lawyer.lawyerId || lawyer.id"
              type="button"
              class="result-item"
              @click="goToLawyerDetail(lawyer.lawyerId || lawyer.id)"
            >
              <span class="item-title">{{ lawyer.name || '未命名律师' }}</span>
              <small>{{ lawyer.lawFirm || '暂未填写律所' }} · {{ lawyer.practiceYears || '-' }} 年</small>
              <p>{{ getSpecialtyNames(lawyer).join('、') || lawyer.description || '暂无专长信息' }}</p>
            </button>
          </div>
          <el-empty v-else description="暂无律师结果" />
        </article>

        <article class="result-panel">
          <div class="panel-title">
            <h3>个人订单</h3>
            <el-tag effect="plain">{{ orderResults.length }}</el-tag>
          </div>
          <div v-if="orderNotice" class="login-notice">
            {{ orderNotice }}
            <el-button link type="primary" @click="router.push('/login')">去登录</el-button>
          </div>
          <div v-else-if="orderResults.length" class="result-list">
            <button
              v-for="order in orderResults"
              :key="order.orderId || order.id"
              type="button"
              class="result-item"
              @click="goToOrderDetail(order.orderId || order.id)"
            >
              <span class="item-title">订单 #{{ order.orderId || order.id }}</span>
              <small>{{ formatServiceType(order.serviceTypeId) }} · {{ order.status || '未知状态' }}</small>
              <p>{{ formatMoney(order.totalAmount) }} · {{ formatTime(order.createdTime) }}</p>
            </button>
          </div>
          <el-empty v-else description="暂无订单结果" />
        </article>

        <article class="result-panel">
          <div class="panel-title">
            <h3>法条</h3>
            <el-tag effect="plain">{{ articleResults.length }}</el-tag>
          </div>
          <div v-if="articleResults.length" class="result-list">
            <button
              v-for="article in articleResults"
              :key="article.id"
              type="button"
              class="result-item"
              @click="openArticle(article)"
            >
              <span class="item-title">{{ article.articleNumber || `法条 ${article.id}` }}</span>
              <small>{{ article.title || '法律条文' }}</small>
              <p>{{ truncate(article.content, 92) }}</p>
            </button>
          </div>
          <el-empty v-else description="暂无法条结果" />
        </article>
      </div>
    </section>

    <el-dialog v-model="articleDialogVisible" title="法条详情" width="640px">
      <div v-if="selectedArticle" class="article-detail">
        <p class="article-number">{{ selectedArticle.articleNumber || `法条 ${selectedArticle.id}` }}</p>
        <h3>{{ selectedArticle.title || '法律条文' }}</h3>
        <p>{{ selectedArticle.content || '暂无条文内容' }}</p>
      </div>
    </el-dialog>
  </main>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { getLawyerList } from '@/api/lawyer'
import { getUserOrders } from '@/api/order'
import { goBack } from '@/utils/navigation'

const route = useRoute()
const router = useRouter()

const keywordInput = ref('')
const activeKeyword = ref('')
const searching = ref(false)
const lawyerResults = ref([])
const articleResults = ref([])
const orderResults = ref([])
const orderNotice = ref('')
const articleDialogVisible = ref(false)
const selectedArticle = ref(null)

const serviceTypeMap = {
  101: '在线法律咨询',
  102: '电话法律咨询',
  103: '文书代写',
  104: '合同审核',
  105: '婚姻家事',
  106: '诉讼代理'
}

const totalResultCount = computed(() => {
  return lawyerResults.value.length + articleResults.value.length + orderResults.value.length
})

const unwrapData = (res) => {
  const payload = res?.data ?? res
  if (payload && typeof payload === 'object' && 'code' in payload) {
    return Number(payload.code) === 200 ? payload.data : null
  }
  return payload
}

const safeParseJson = (value) => {
  try {
    return JSON.parse(value || 'null')
  } catch {
    return null
  }
}

const getCurrentUserId = () => {
  const currentUser = safeParseJson(localStorage.getItem('currentUser'))
  const userInfo = safeParseJson(localStorage.getItem('userInfo'))
  return currentUser?.userId || currentUser?.id || userInfo?.userId || Number(localStorage.getItem('userId')) || null
}

const getRouteKeyword = () => {
  const keyword = route.query.keyword
  return (Array.isArray(keyword) ? keyword[0] : keyword || '').trim()
}

const getSearchText = (value) => {
  if (value === null || value === undefined) return ''
  if (typeof value === 'object') return Object.values(value).map(getSearchText).join(' ')
  return String(value)
}

const matchesKeyword = (value, keyword) => {
  return getSearchText(value).toLowerCase().includes(keyword.toLowerCase())
}

const getSpecialtyNames = (lawyer) => {
  return (lawyer.specialties || [])
    .map((item) => item?.name || item?.specialtyName)
    .filter(Boolean)
}

const searchLawyers = async (keyword) => {
  const list = unwrapData(await getLawyerList())
  if (!Array.isArray(list)) return []

  return list.filter((lawyer) => matchesKeyword([
    lawyer.name,
    lawyer.lawFirm,
    lawyer.description,
    lawyer.specialty,
    getSpecialtyNames(lawyer).join(' ')
  ], keyword)).slice(0, 8)
}

const searchArticles = async (keyword) => {
  const res = await request.get('/api/articles')
  const list = unwrapData(res)
  if (!Array.isArray(list)) return []

  return list.filter((article) => matchesKeyword([
    article.articleNumber,
    article.title,
    article.content
  ], keyword)).slice(0, 8)
}

const formatServiceType = (id) => serviceTypeMap[id] || `服务类型 ${id || '-'}`
const formatMoney = (value) => `¥${Number(value || 0).toFixed(2)}`
const formatTime = (value) => (value ? String(value).replace('T', ' ').slice(0, 16) : '-')

const parseFormData = (raw) => {
  if (!raw) return {}
  if (typeof raw === 'object') return raw
  try {
    return JSON.parse(raw)
  } catch {
    return { content: raw }
  }
}

const normalizeOrderForSearch = (order) => ({
  ...order,
  serviceName: formatServiceType(order.serviceTypeId),
  amountText: formatMoney(order.totalAmount),
  formText: getSearchText(parseFormData(order.formData))
})

const searchOrders = async (keyword) => {
  orderNotice.value = ''
  const userId = getCurrentUserId()
  if (!userId) {
    orderNotice.value = '登录后可搜索个人订单'
    return []
  }

  const res = await getUserOrders(userId)
  const list = unwrapData(res)
  if (!Array.isArray(list)) return []

  return list
    .map(normalizeOrderForSearch)
    .filter((order) => matchesKeyword([
      order.orderId,
      order.id,
      order.status,
      order.serviceName,
      order.lawyerId,
      order.amountText,
      order.formText
    ], keyword))
    .slice(0, 8)
}

const clearResults = () => {
  lawyerResults.value = []
  articleResults.value = []
  orderResults.value = []
  orderNotice.value = ''
}

const runSearch = async (keyword) => {
  activeKeyword.value = keyword
  keywordInput.value = keyword
  clearResults()

  if (!keyword) return

  searching.value = true
  const [lawyers, articles, orders] = await Promise.allSettled([
    searchLawyers(keyword),
    searchArticles(keyword),
    searchOrders(keyword)
  ])

  lawyerResults.value = lawyers.status === 'fulfilled' ? lawyers.value : []
  articleResults.value = articles.status === 'fulfilled' ? articles.value : []
  orderResults.value = orders.status === 'fulfilled' ? orders.value : []

  if (lawyers.status === 'rejected' || articles.status === 'rejected' || orders.status === 'rejected') {
    ElMessage.warning('部分搜索结果加载失败，请稍后重试')
  }

  searching.value = false
}

const runSearchFromRoute = () => {
  runSearch(getRouteKeyword())
}

const submitSearch = () => {
  const keyword = keywordInput.value.trim()
  if (!keyword) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  if (keyword === activeKeyword.value) {
    runSearch(keyword)
    return
  }

  router.push({
    name: 'SearchResults',
    query: { keyword }
  })
}

const handleBack = () => {
  goBack(router, '/')
}

const goToLawyerDetail = (id) => {
  if (!id) return
  router.push(`/lawyer/${id}`)
}

const goToOrderDetail = (id) => {
  if (!id) return
  router.push(`/orders/${id}`)
}

const openArticle = (article) => {
  selectedArticle.value = article
  articleDialogVisible.value = true
}

const truncate = (value, length = 80) => {
  const text = value || ''
  return text.length > length ? `${text.slice(0, length)}...` : text
}

watch(() => route.query.keyword, runSearchFromRoute)

onMounted(runSearchFromRoute)
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  padding: 32px 20px 56px;
  background:
    linear-gradient(180deg, rgba(37, 99, 235, 0.1) 0, rgba(245, 247, 251, 0) 260px),
    #f5f7fb;
  color: #172033;
  text-align: left;
}

.search-page *,
.search-page *::before,
.search-page *::after {
  box-sizing: border-box;
}

.search-header,
.results-shell {
  width: min(1180px, 100%);
  margin-left: auto;
  margin-right: auto;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 4px 8px rgba(15, 23, 42, 0.08);
}

.search-header {
  display: grid;
  grid-template-columns: auto minmax(220px, 0.7fr) minmax(320px, 1fr);
  gap: 18px;
  align-items: center;
  padding: 22px;
}

.back-btn {
  border-color: #d6e4ff;
  color: #1d4ed8;
  background: #ffffff;
}

.header-copy {
  min-width: 0;
}

.eyebrow {
  margin: 0 0 8px;
  color: #2563eb;
  font-size: 13px;
  font-weight: 800;
}

h1,
h2,
h3,
.header-copy p {
  margin: 0;
}

h1 {
  color: #172033;
  font-size: 28px;
  font-weight: 800;
  letter-spacing: 0;
}

h2 {
  color: #172033;
  font-size: 22px;
  font-weight: 800;
  letter-spacing: 0;
}

h3 {
  color: #172033;
  font-size: 18px;
  font-weight: 800;
}

.header-copy p:not(.eyebrow) {
  margin-top: 8px;
  color: #667085;
}

.search-bar {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 112px;
  gap: 12px;
}

.search-bar :deep(.el-input__wrapper),
.search-bar :deep(.el-button) {
  border-radius: 8px;
}

.results-shell {
  margin-top: 20px;
  padding: 22px;
}

.section-head,
.panel-title {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
}

.section-head {
  margin-bottom: 18px;
}

.section-head > span {
  color: #1d4ed8;
  font-weight: 800;
}

.result-stack {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.result-panel {
  min-height: 220px;
  padding: 18px;
  border: 1px solid #dbeafe;
  border-radius: 8px;
  background: linear-gradient(180deg, #f8fbff 0%, #ffffff 100%);
}

.panel-title {
  margin-bottom: 12px;
}

.result-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.result-item {
  width: 100%;
  padding: 12px;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
  cursor: pointer;
  text-align: left;
  transition: all 0.2s ease;
}

.result-item:hover {
  border-color: #93c5fd;
  box-shadow: 0 4px 8px rgba(37, 99, 235, 0.12);
  transform: translateY(-1px);
}

.item-title {
  display: block;
  color: #172033;
  font-size: 15px;
  font-weight: 800;
}

.result-item small {
  display: block;
  margin-top: 4px;
  color: #667085;
  font-size: 12px;
}

.result-item p {
  margin: 8px 0 0;
  color: #475467;
  font-size: 13px;
  line-height: 1.6;
}

.login-notice {
  padding: 16px;
  border: 1px dashed #bfdbfe;
  border-radius: 8px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 14px;
}

.article-detail h3 {
  margin: 0 0 12px;
}

.article-detail p {
  color: #475467;
  line-height: 1.9;
  white-space: pre-wrap;
}

.article-number {
  margin: 0 0 8px;
  color: #1d4ed8;
  font-weight: 800;
}

@media (max-width: 1024px) {
  .search-header {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .search-page {
    padding: 20px 12px 40px;
  }

  .search-header,
  .results-shell {
    padding: 16px;
  }

  .search-bar,
  .section-head {
    grid-template-columns: 1fr;
  }

  .search-bar {
    display: flex;
    flex-direction: column;
  }

  .section-head {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
