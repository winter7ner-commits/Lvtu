<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getLawyerDetail, getLawyerList } from '@/api/lawyer'
import EvaluationList from '@/components/evaluation/EvaluationList.vue'

const route = useRoute()
const router = useRouter()

const detail = ref(null)
const loading = ref(false)
const loadFailed = ref(false)

const unwrapData = (res) => {
  const payload = res?.data
  if (payload && typeof payload === 'object' && 'code' in payload) {
    return Number(payload.code) === 200 ? payload.data : null
  }
  return payload
}

const normalizeLawyer = (lawyer) => {
  if (!lawyer || typeof lawyer !== 'object') return null
  if (!lawyer.lawyerId && !lawyer.id) return null

  return {
    ...lawyer,
    lawyerId: lawyer.lawyerId || lawyer.id,
    specialties: Array.isArray(lawyer.specialties) ? lawyer.specialties : []
  }
}

const loadFromList = async (id) => {
  const res = await getLawyerList()
  const list = unwrapData(res)
  if (!Array.isArray(list)) return null
  return list.find((item) => String(item.lawyerId || item.id) === String(id)) || null
}

const loadDetail = async () => {
  const id = route.params.id
  if (!id) {
    detail.value = null
    loadFailed.value = true
    return
  }

  loading.value = true
  loadFailed.value = false

  try {
    let lawyer = null

    try {
      lawyer = unwrapData(await getLawyerDetail(id))
    } catch (error) {
      lawyer = await loadFromList(id)
    }

    if (!lawyer) {
      lawyer = await loadFromList(id)
    }

    detail.value = normalizeLawyer(lawyer)
    loadFailed.value = !detail.value
  } catch (error) {
    console.error('加载律师详情失败:', error)
    detail.value = null
    loadFailed.value = true
    ElMessage.error('律师详情加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const rating = computed(() => Number(detail.value?.rating || 0))
const practiceYears = computed(() => Number(detail.value?.practiceYears || 0))

const specialtyNames = computed(() => {
  const names = detail.value?.specialties
    ?.map((item) => item?.name || item?.specialtyName)
    .filter(Boolean) || []

  if (names.length) return names
  return detail.value?.specialty ? String(detail.value.specialty).split(',').filter(Boolean) : []
})

const avatarUrl = computed(() => {
  return detail.value?.avatarUrl || detail.value?.avatar || detail.value?.photoUrl || ''
})

const initials = computed(() => {
  const name = detail.value?.name || '律师'
  return name.slice(-2)
})

const contactItems = computed(() => [
  { label: '联系电话', value: detail.value?.phone || '暂未公开' },
  { label: '执业证号', value: detail.value?.licenseNo || '暂未填写' },
  { label: '律师编号', value: detail.value?.lawyerId || '-' }
])

const highlights = computed(() => [
  { label: '服务评分', value: rating.value.toFixed(1), suffix: '分' },
  { label: '执业年限', value: practiceYears.value || '-', suffix: practiceYears.value ? '年' : '' },
  { label: '擅长领域', value: specialtyNames.value.length || 0, suffix: '项' }
])

const goBack = () => {
  router.push({ name: 'LawyerList' })
}

const createConsultOrder = () => {
  if (!detail.value?.lawyerId) return

  router.push({
    name: 'OrderCreate',
    query: {
      type: 'ONLINE_CONSULT',
      lawyerId: detail.value.lawyerId
    }
  })
}

onMounted(loadDetail)

watch(
  () => route.params.id,
  () => {
    loadDetail()
  }
)
</script>

<template>
  <main class="lawyer-detail-page">
    <section v-loading="loading" class="detail-shell">
      <template v-if="detail">
        <div class="toolbar">
          <el-button class="back-order-btn" @click="goBack">
            <span class="back-icon">‹</span>
            返回律师列表
          </el-button>
          <el-button type="primary" size="large" @click="createConsultOrder">发起在线咨询</el-button>
        </div>

        <section class="profile-hero">
          <div class="avatar-wrap">
            <img v-if="avatarUrl" :src="avatarUrl" class="avatar" :alt="detail.name" />
            <div v-else class="avatar-placeholder">{{ initials }}</div>
          </div>

          <div class="hero-main">
            <div class="name-row">
              <div>
                <p class="eyebrow">认证律师</p>
                <h1>{{ detail.name || '未命名律师' }}</h1>
              </div>
              <el-tag type="success" effect="light" round>平台认证</el-tag>
            </div>

            <p class="firm">{{ detail.lawFirm || '暂未填写事务所' }}</p>

            <div class="rating-row">
              <el-rate :model-value="rating" disabled />
              <span>{{ rating.toFixed(1) }} 分</span>
            </div>

            <div v-if="specialtyNames.length" class="tag-list">
              <el-tag v-for="specialty in specialtyNames" :key="specialty" size="large">
                {{ specialty }}
              </el-tag>
            </div>
            <p v-else class="muted">暂未填写擅长领域</p>
          </div>
        </section>

        <section class="highlight-grid" aria-label="律师概览">
          <div v-for="item in highlights" :key="item.label" class="highlight-item">
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}<small>{{ item.suffix }}</small></strong>
          </div>
        </section>

        <div class="content-grid">
          <section class="detail-panel">
            <div class="panel-head">
              <h2>个人简介</h2>
            </div>
            <p class="bio">
              {{ detail.description || '该律师暂未填写个人简介，可先通过在线咨询说明你的法律问题。' }}
            </p>
          </section>

          <section class="detail-panel">
            <div class="panel-head">
              <h2>执业信息</h2>
            </div>
            <dl class="info-list">
              <div>
                <dt>所在律所</dt>
                <dd>{{ detail.lawFirm || '暂未填写' }}</dd>
              </div>
              <div>
                <dt>执业年限</dt>
                <dd>{{ practiceYears || '-' }} 年</dd>
              </div>
              <div v-for="item in contactItems" :key="item.label">
                <dt>{{ item.label }}</dt>
                <dd>{{ item.value }}</dd>
              </div>
            </dl>
          </section>
        </div>

        <!-- 用户评价 -->
        <section class="detail-panel evaluations-section">
          <div class="panel-head">
            <h2>用户评价</h2>
          </div>
          <EvaluationList :lawyer-id="detail.lawyerId" :can-report="false" />
        </section>
      </template>

      <el-empty
        v-else-if="!loading && loadFailed"
        class="empty-state"
        description="未找到该律师信息"
      >
        <el-button type="primary" @click="goBack">返回律师列表</el-button>
      </el-empty>
    </section>
  </main>
</template>

<style scoped>
.lawyer-detail-page {
  min-height: 100vh;
  padding: 32px 20px 56px;
  background: #f5f7fb;
  color: #172033;
}

.detail-shell {
  width: min(1120px, 100%);
  min-height: 420px;
  margin: 0 auto;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  margin-bottom: 18px;
}

.toolbar :deep(.el-button:not(.back-order-btn)) {
  border-radius: 8px;
  font-weight: 800;
}

.back-order-btn {
  border-color: #d6e4ff;
  color: #1d4ed8;
  background: #ffffff;
  border-radius: 999px;
  padding: 9px 18px;
  font-weight: 600;
}

.back-order-btn:hover {
  color: #ffffff;
  background: #2563eb;
  border-color: #2563eb;
}

.back-icon {
  font-size: 20px;
  line-height: 1;
  margin-right: 2px;
}

.profile-hero,
.highlight-item,
.detail-panel {
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.profile-hero {
  display: grid;
  grid-template-columns: 168px minmax(0, 1fr);
  gap: 24px;
  padding: 24px;
}

.avatar-wrap {
  width: 168px;
  height: 168px;
  border-radius: 8px;
  overflow: hidden;
  background: #eff6ff;
}

.avatar,
.avatar-placeholder {
  width: 100%;
  height: 100%;
}

.avatar {
  object-fit: cover;
}

.avatar-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    linear-gradient(135deg, rgba(37, 99, 235, 0.14), rgba(20, 184, 166, 0.12)),
    #eff6ff;
  color: #1d4ed8;
  font-size: 36px;
  font-weight: 900;
}

.hero-main {
  min-width: 0;
}

.name-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.eyebrow {
  margin: 0 0 8px;
  color: #2563eb;
  font-size: 13px;
  font-weight: 800;
}

h1,
h2,
.firm,
.bio,
.info-list {
  margin: 0;
}

h1 {
  color: #172033;
  font-size: 32px;
  line-height: 1.2;
}

.firm {
  margin-top: 10px;
  color: #475467;
  font-size: 16px;
  font-weight: 700;
}

.rating-row {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-top: 16px;
  color: #1d4ed8;
  font-weight: 800;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}

.tag-list :deep(.el-tag) {
  border-color: #bfdbfe;
  background: #eff6ff;
  color: #1d4ed8;
  font-weight: 800;
}

.muted {
  margin: 18px 0 0;
  color: #98a2b3;
  font-size: 14px;
}

.highlight-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
  margin-top: 16px;
}

.highlight-item {
  padding: 16px 18px;
}

.highlight-item span {
  display: block;
  color: #667085;
  font-size: 13px;
  font-weight: 700;
}

.highlight-item strong {
  display: block;
  margin-top: 8px;
  color: #172033;
  font-size: 28px;
  line-height: 1;
}

.highlight-item small {
  margin-left: 4px;
  color: #667085;
  font-size: 14px;
}

.content-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.25fr) minmax(320px, 0.75fr);
  gap: 16px;
  margin-top: 16px;
}

.detail-panel {
  padding: 20px;
}

.panel-head {
  margin-bottom: 14px;
  padding-bottom: 12px;
  border-bottom: 1px solid #edf1f7;
}

h2 {
  color: #172033;
  font-size: 18px;
  line-height: 1.35;
}

.bio {
  color: #475467;
  font-size: 15px;
  line-height: 1.9;
  white-space: pre-line;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-list div {
  display: grid;
  grid-template-columns: 88px minmax(0, 1fr);
  gap: 12px;
  align-items: start;
}

.info-list dt {
  color: #667085;
  font-size: 13px;
  font-weight: 700;
}

.info-list dd {
  min-width: 0;
  margin: 0;
  color: #344054;
  font-size: 14px;
  font-weight: 800;
  overflow-wrap: anywhere;
}

.empty-state {
  padding: 96px 0;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
}

.evaluations-section {
  margin-top: 16px;
}

.evaluations-section :deep(.loading),
.evaluations-section :deep(.empty) {
  text-align: center;
  color: #98a2b3;
  padding: 32px 0;
  font-size: 15px;
}

@media (max-width: 820px) {
  .profile-hero,
  .content-grid {
    grid-template-columns: 1fr;
  }

  .avatar-wrap {
    width: 118px;
    height: 118px;
  }

  .highlight-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .lawyer-detail-page {
    padding: 20px 12px 40px;
  }

  .toolbar,
  .name-row,
  .rating-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .toolbar :deep(.el-button) {
    width: 100%;
  }

  .profile-hero,
  .detail-panel {
    padding: 18px 14px;
  }

  h1 {
    font-size: 26px;
  }

  .info-list div {
    grid-template-columns: 1fr;
    gap: 4px;
  }
}
</style>
