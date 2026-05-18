<template>
  <main class="home-page">
    <section class="hero-section">
      <div class="hero-copy">
        <p class="eyebrow">律途法律服务</p>
        <h1>把法律问题交给更合适的专业支持</h1>
        <p class="subtext">检索律师、法条与个人订单，快速进入咨询、查询和跟进流程。</p>

        <div class="hero-search">
          <el-input
            v-model="searchKeyword"
            clearable
            size="large"
            placeholder="输入律师、法条、订单号、业务类型或状态"
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" size="large" :loading="searching" @click="handleSearch">
            搜索
          </el-button>
        </div>

        <div class="quick-tags">
          <button
            v-for="tag in quickTags"
            :key="tag"
            type="button"
            @click="searchByKeyword(tag)"
          >
            {{ tag }}
          </button>
        </div>
      </div>

      <div class="hero-panel">
        <div class="logo-stage">
          <span class="logo-ring"></span>
          <img src="/src/assets/images/logo.png" alt="律途" />
        </div>
        <div class="hero-metrics">
          <div>
            <strong>{{ hotLawyers.length || '-' }}</strong>
            <span>热门律师</span>
          </div>
          <div>
            <strong>6</strong>
            <span>服务类型</span>
          </div>
          <div>
            <strong>24h</strong>
            <span>需求流转</span>
          </div>
        </div>
      </div>
    </section>

    <section class="service-band">
      <div class="section-head">
        <div>
          <p class="eyebrow">常用服务</p>
          <h2>从问题到订单的快速入口</h2>
        </div>
        <el-button class="soft-btn" @click="router.push('/order-create')">创建订单</el-button>
      </div>

      <div class="service-grid">
        <button
          v-for="service in services"
          :key="service.type"
          type="button"
          class="service-tile"
          @click="router.push({ name: 'OrderCreate', query: { type: service.type } })"
        >
          <span>{{ service.short }}</span>
          <strong>{{ service.name }}</strong>
          <small>{{ service.desc }}</small>
        </button>
      </div>
    </section>

    <section class="home-columns">
      <article class="home-panel" v-loading="loadingHotLawyers">
        <div class="section-head compact">
          <div>
            <p class="eyebrow">律师推荐</p>
            <h2>高评分律师</h2>
          </div>
          <el-button link type="primary" @click="router.push('/lawyer-list')">全部律师</el-button>
        </div>
        <div v-if="hotLawyers.length" class="lawyer-list">
          <button
            v-for="lawyer in hotLawyers"
            :key="lawyer.lawyerId || lawyer.id"
            type="button"
            class="lawyer-row"
            @click="goToLawyerDetail(lawyer.lawyerId || lawyer.id)"
          >
            <div class="avatar">{{ getInitials(lawyer.name) }}</div>
            <div>
              <strong>{{ lawyer.name || '未命名律师' }}</strong>
              <span>{{ lawyer.lawFirm || '暂未填写律所' }}</span>
            </div>
            <em>{{ Number(lawyer.rating || 0).toFixed(1) }}</em>
          </button>
        </div>
        <el-empty v-else description="暂无推荐律师" />
      </article>

      <article class="home-panel">
        <div class="section-head compact">
          <div>
            <p class="eyebrow">办事进度</p>
            <h2>个人订单</h2>
          </div>
          <el-button link type="primary" @click="router.push('/orders')">我的订单</el-button>
        </div>
        <div class="order-entry">
          <div>
            <strong>跟进咨询、支付、服务结果与评价</strong>
            <p>登录后可在首页搜索自己的订单号、状态和业务类型。</p>
          </div>
          <el-button type="primary" plain @click="router.push('/orders')">查看订单</el-button>
        </div>
      </article>
    </section>

  </main>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTopRatedLawyers } from '@/api/lawyer'

const router = useRouter()

const searchKeyword = ref('')
const searching = ref(false)
const loadingHotLawyers = ref(false)
const hotLawyers = ref([])

const quickTags = ['婚姻家事', '合同纠纷', '劳动争议', '知识产权', '待支付']

const services = [
  { short: '咨', name: '在线法律咨询', type: 'ONLINE_CONSULT', desc: '图文沟通' },
  { short: '电', name: '电话法律咨询', type: 'PHONE_CONSULT', desc: '预约通话' },
  { short: '文', name: '文书代写', type: 'DOCUMENT_WRITING', desc: '起草修改' },
  { short: '合', name: '合同审核', type: 'CONTRACT_REVIEW', desc: '风险排查' },
  { short: '家', name: '婚姻家事', type: 'MARRIAGE_FAMILY', desc: '家事纠纷' },
  { short: '诉', name: '诉讼代理', type: 'LITIGATION_AGENT', desc: '案件代理' }
]

const unwrapData = (res) => {
  const payload = res?.data ?? res
  if (payload && typeof payload === 'object' && 'code' in payload) {
    return Number(payload.code) === 200 ? payload.data : null
  }
  return payload
}

const handleSearch = () => {
  const keyword = searchKeyword.value.trim()
  if (!keyword) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  router.push({
    name: 'SearchResults',
    query: { keyword }
  })
}

const searchByKeyword = (keyword) => {
  searchKeyword.value = keyword
  handleSearch()
}

const loadHotLawyers = async () => {
  loadingHotLawyers.value = true
  try {
    const list = unwrapData(await getTopRatedLawyers(4))
    hotLawyers.value = Array.isArray(list) ? list : []
  } catch (error) {
    hotLawyers.value = []
  } finally {
    loadingHotLawyers.value = false
  }
}

const goToLawyerDetail = (id) => {
  if (!id) return
  router.push(`/lawyer/${id}`)
}

const getInitials = (name = '律师') => name.slice(-2)

onMounted(loadHotLawyers)
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  padding: 32px 20px 56px;
  background: #f5f7fb;
  color: #172033;
  text-align: left;
  overflow-x: hidden;
}

.home-page *,
.home-page *::before,
.home-page *::after {
  box-sizing: border-box;
}

.home-page .hero-section,
.home-page .service-band,
.home-page .home-columns {
  width: min(1180px, 100%);
  margin-left: auto;
  margin-right: auto;
}

.home-page .hero-section {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(320px, 0.8fr);
  gap: 28px;
  align-items: stretch;
}

.home-page .hero-copy,
.home-page .hero-panel,
.home-page .service-band,
.home-page .home-panel {
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.home-page .hero-copy,
.home-page .hero-panel {
  min-width: 0;
}

.home-page .hero-copy {
  padding: 34px;
  animation: homeFadeUp 0.58s ease both;
}

.home-page .eyebrow {
  margin: 0 0 8px;
  color: #2563eb;
  font-size: 13px;
  font-weight: 800;
}

.home-page h1,
.home-page h2,
.home-page h3,
.home-page .subtext {
  margin: 0;
}

.home-page h1 {
  max-width: 720px;
  color: #172033;
  font-size: 38px;
  font-weight: 800;
  line-height: 1.18;
  letter-spacing: 0;
}

.home-page h2 {
  color: #172033;
  font-size: 22px;
  font-weight: 800;
  letter-spacing: 0;
}

.home-page h3 {
  color: #172033;
  font-size: 18px;
  font-weight: 800;
}

.home-page .subtext {
  margin-top: 14px;
  color: #667085;
  font-size: 16px;
}

.home-page .hero-search {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 112px;
  gap: 12px;
  margin-top: 26px;
}

.home-page .hero-search :deep(.el-input__wrapper),
.home-page .hero-search :deep(.el-button) {
  border-radius: 8px;
}

.home-page .quick-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 16px;
}

.home-page .quick-tags button {
  padding: 8px 12px;
  border: 1px solid #dbe4f0;
  border-radius: 999px;
  background: #f8fafc;
  color: #475467;
  cursor: pointer;
  font-size: 13px;
  font-weight: 700;
}

.home-page .quick-tags button:hover {
  border-color: #2563eb;
  color: #1d4ed8;
}

.home-page .hero-panel {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: stretch;
  padding: 24px;
  overflow: hidden;
  animation: homeFadeUp 0.7s ease 0.08s both;
}

.home-page .logo-stage {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 236px;
  isolation: isolate;
}

.home-page .logo-stage::before {
  content: '';
  position: absolute;
  width: 260px;
  height: 260px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.16), rgba(37, 99, 235, 0) 62%);
  animation: logoGlow 3.6s ease-in-out infinite;
  z-index: -2;
}

.home-page .logo-stage::after {
  content: '';
  position: absolute;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #34d399;
  box-shadow: 0 0 20px rgba(52, 211, 153, 0.45);
  transform: translate(116px, -58px);
  animation: logoDot 4.8s ease-in-out infinite;
  z-index: -1;
}

.home-page .logo-ring {
  position: absolute;
  width: 250px;
  height: 250px;
  border: 1px solid rgba(37, 99, 235, 0.16);
  border-radius: 50%;
  animation: logoOrbit 10s linear infinite;
  z-index: -1;
}

.home-page .logo-ring::before,
.home-page .logo-ring::after {
  content: '';
  position: absolute;
  border-radius: 50%;
}

.home-page .logo-ring::before {
  inset: 20px;
  border: 1px dashed rgba(20, 184, 166, 0.18);
}

.home-page .logo-ring::after {
  top: 28px;
  right: 46px;
  width: 8px;
  height: 8px;
  background: #2563eb;
  box-shadow: 0 0 18px rgba(37, 99, 235, 0.48);
}

.home-page .hero-panel img {
  display: block;
  width: min(100%, 320px);
  max-width: 100%;
  height: auto;
  max-height: 210px;
  margin: 0 auto;
  object-fit: contain;
  filter: drop-shadow(0 16px 18px rgba(37, 99, 235, 0.14));
  animation: logoFloat 4.2s ease-in-out infinite;
}

.home-page .hero-metrics {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-top: 18px;
}

.home-page .hero-metrics div {
  padding: 12px 10px;
  border-radius: 8px;
  background: #f6f8fc;
  text-align: center;
  animation: homeFadeUp 0.5s ease both;
}

.home-page .hero-metrics div:nth-child(1) {
  animation-delay: 0.18s;
}

.home-page .hero-metrics div:nth-child(2) {
  animation-delay: 0.26s;
}

.home-page .hero-metrics div:nth-child(3) {
  animation-delay: 0.34s;
}

.home-page .hero-metrics strong {
  display: block;
  color: #1d4ed8;
  font-size: 22px;
}

.home-page .hero-metrics span {
  color: #667085;
  font-size: 12px;
}

.home-page .service-band {
  margin-top: 20px;
  padding: 22px;
}

.home-page .section-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
}

.home-page .section-head {
  margin-bottom: 18px;
}

.home-page .section-head > span {
  color: #1d4ed8;
  font-weight: 800;
}

.home-page .compact {
  margin-bottom: 16px;
}

.home-page .lawyer-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.home-page .lawyer-row,
.home-page .service-tile {
  width: 100%;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
  cursor: pointer;
  text-align: left;
  transition: all 0.2s ease;
}

.home-page .lawyer-row:hover,
.home-page .service-tile:hover {
  border-color: #93c5fd;
  box-shadow: 0 10px 22px rgba(37, 99, 235, 0.1);
  transform: translateY(-1px);
}

.home-page .lawyer-row strong {
  display: block;
  color: #172033;
  font-size: 15px;
  font-weight: 800;
}

.home-page .lawyer-row span {
  display: block;
  margin-top: 4px;
  color: #667085;
  font-size: 12px;
}

.home-page .soft-btn {
  border-color: #d6e4ff;
  color: #1d4ed8;
  background: #ffffff;
}

.home-page .service-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 12px;
}

.home-page .service-tile {
  min-height: 126px;
  padding: 16px;
  animation: homeFadeUp 0.5s ease both;
}

.home-page .service-tile:nth-child(1) {
  animation-delay: 0.08s;
}

.home-page .service-tile:nth-child(2) {
  animation-delay: 0.12s;
}

.home-page .service-tile:nth-child(3) {
  animation-delay: 0.16s;
}

.home-page .service-tile:nth-child(4) {
  animation-delay: 0.2s;
}

.home-page .service-tile:nth-child(5) {
  animation-delay: 0.24s;
}

.home-page .service-tile:nth-child(6) {
  animation-delay: 0.28s;
}

.home-page .service-tile span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-weight: 900;
}

.home-page .service-tile strong,
.home-page .service-tile small {
  display: block;
}

.home-page .service-tile strong {
  margin-top: 12px;
  color: #172033;
  font-size: 15px;
}

.home-page .service-tile small {
  margin-top: 5px;
  color: #667085;
}

.home-page .home-columns {
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 18px;
  margin-top: 20px;
}

.home-page .home-panel {
  padding: 22px;
}

.home-page .lawyer-row {
  display: grid;
  grid-template-columns: 46px minmax(0, 1fr) auto;
  gap: 12px;
  align-items: center;
  padding: 12px;
}

.home-page .avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  border-radius: 8px;
  background: #eff6ff;
  color: #1d4ed8;
  font-weight: 900;
}

.home-page .lawyer-row em {
  color: #1d4ed8;
  font-style: normal;
  font-weight: 900;
}

.home-page .order-entry {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  min-height: 144px;
  padding: 18px;
  border-radius: 8px;
  background: #f6f8fc;
}

.home-page .order-entry strong {
  color: #172033;
}

.home-page .order-entry p {
  margin: 8px 0 0;
  color: #667085;
  line-height: 1.7;
}

@keyframes homeFadeUp {
  from {
    opacity: 0;
    translate: 0 14px;
  }

  to {
    opacity: 1;
    translate: 0 0;
  }
}

@keyframes logoFloat {
  0%,
  100% {
    transform: translateY(0) rotate(0deg);
  }

  50% {
    transform: translateY(-10px) rotate(1deg);
  }
}

@keyframes logoGlow {
  0%,
  100% {
    opacity: 0.68;
    transform: scale(0.96);
  }

  50% {
    opacity: 1;
    transform: scale(1.04);
  }
}

@keyframes logoOrbit {
  to {
    transform: rotate(360deg);
  }
}

@keyframes logoDot {
  0%,
  100% {
    transform: translate(116px, -58px) scale(1);
  }

  50% {
    transform: translate(102px, -70px) scale(1.28);
  }
}

@media (prefers-reduced-motion: reduce) {
  .home-page *,
  .home-page *::before,
  .home-page *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    scroll-behavior: auto !important;
  }
}

@media (max-width: 1024px) {
  .home-page .hero-section,
  .home-page .home-columns {
    grid-template-columns: 1fr;
  }

  .home-page .service-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .home-page {
    padding: 20px 12px 40px;
  }

  .home-page .hero-copy,
  .home-page .hero-panel,
  .home-page .service-band,
  .home-page .home-panel {
    padding: 16px;
  }

  .home-page h1 {
    font-size: 28px;
  }

  .home-page .hero-search,
  .home-page .service-grid {
    grid-template-columns: 1fr;
  }

  .home-page .section-head,
  .home-page .order-entry {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
