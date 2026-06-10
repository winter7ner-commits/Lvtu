<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getLawyerProfileByUserId, getMyLawyerOrders } from '@/api/lawyerOrder'

const router = useRouter()
const loading = ref(false)
const allOrders = ref([])
const activeStatus = ref('all')
const currentLawyerId = ref(null)
const refreshTimer = ref(null)

const statusOptions = [
  { label: '全部', value: 'all' },
  { label: '待接单', value: '待接单' },
  { label: '待处理', value: '处理中' },
  { label: '待客户确认', value: '待客户确认' },
  { label: '待评价', value: '待评价' },
  { label: '已完成', value: '已完成' },
  { label: '平台介入', value: '平台介入' }
]

const serviceTypeMap = {
  101: '在线法律咨询',
  102: '电话法律咨询',
  103: '文书代写',
  104: '合同审核',
  105: '婚姻家事',
  106: '诉讼代理'
}

const statusCount = computed(() => {
  return allOrders.value.reduce((acc, item) => {
    const status = normalizeStatus(item.status)
    acc[status] = (acc[status] || 0) + 1
    return acc
  }, {})
})

const filteredOrders = computed(() => {
  if (activeStatus.value === 'all') return allOrders.value
  return allOrders.value.filter((item) => normalizeStatus(item.status) === activeStatus.value)
})

const unwrap = (res) => res?.data?.data ?? res?.data ?? []
const normalizeStatus = (status) => String(status || '').trim()
const formatServiceType = (id) => serviceTypeMap[id] || `服务类型 ${id || '-'}`
const formatMoney = (value) => `¥${Number(value || 0).toFixed(2)}`
const formatTime = (value) => (value ? String(value).replace('T', ' ').slice(0, 16) : '-')
const displayStatus = (status) => status === '处理中' ? '待处理' : status
const isDirectOrder = (row) => row?.assignmentType === 'DIRECT'

const parseFormData = (raw) => {
  if (!raw) return {}
  if (typeof raw === 'object') return raw
  try {
    return JSON.parse(raw)
  } catch {
    return { 需求内容: raw }
  }
}

const getOrderSummary = (row) => {
  const data = parseFormData(row.formData)
  return data.problemTitle ||
    data.summary ||
    data.description ||
    data.problemDescription ||
    data.keyProblem ||
    data.coreDemand ||
    data.eventDescription ||
    data.supplementaryRemarks ||
    '暂无需求摘要'
}

const getActionText = (status) => {
  if (status === '待接单') return '接单处理'
  if (status === '处理中') return '进入处理'
  if (status === '待客户确认') return '查看提交结果'
  if (status === '平台介入') return '查看处理记录'
  return '查看详情'
}

const getStatusType = (status) => {
  if (status === '处理中') return 'primary'
  if (status === '待客户确认') return 'warning'
  if (status === '待评价') return 'warning'
  if (status === '已完成') return 'success'
  if (status === '待接单') return 'info'
  if (status === '平台介入') return 'danger'
  return 'info'
}

const loadCurrentLawyer = async () => {
  if (currentLawyerId.value) return currentLawyerId.value
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')
  if (!currentUser?.userId) {
    ElMessage.warning('请先登录律师账号')
    return null
  }
  const res = await getLawyerProfileByUserId(currentUser.userId)
  const lawyer = unwrap(res)
  currentLawyerId.value = lawyer?.lawyerId || null
  return currentLawyerId.value
}

const loadOrders = async () => {
  loading.value = true
  try {
    const lawyerId = await loadCurrentLawyer()
    if (!lawyerId) {
      allOrders.value = []
      return
    }
    const res = await getMyLawyerOrders({ lawyerId })
    allOrders.value = unwrap(res)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '我的订单加载失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  loadOrders()
}

const handleVisibilityRefresh = () => {
  if (document.visibilityState === 'visible') {
    loadOrders()
  }
}

const goDetail = (orderId) => {
  router.push(`/lawyer/orders/${orderId}`)
}

onMounted(() => {
  loadOrders()
  refreshTimer.value = window.setInterval(loadOrders, 5000)
  document.addEventListener('visibilitychange', handleVisibilityRefresh)
})

onBeforeUnmount(() => {
  if (refreshTimer.value) {
    window.clearInterval(refreshTimer.value)
  }
  document.removeEventListener('visibilitychange', handleVisibilityRefresh)
})
</script>

<template>
  <div class="my-orders-page">
    <section class="page-head">
      <div>
        <p class="eyebrow">律师工作台</p>
        <h1>我的订单</h1>
        <p class="subtext">跟进已接订单状态，处理服务结果与后续查看。</p>
      </div>
      <div class="head-actions">
        <el-button @click="router.push('/lawyer/orders/available')">接单大厅</el-button>
        <el-button type="primary" @click="loadOrders">刷新</el-button>
      </div>
    </section>

    <section class="status-strip">
      <div class="status-item">
        <span>当前律师</span>
        <strong>{{ currentLawyerId }}</strong>
      </div>
      <div class="status-item">
        <span>待接单</span>
        <strong>{{ statusCount['待接单'] || 0 }}</strong>
      </div>
      <div class="status-item">
        <span>待处理</span>
        <strong>{{ statusCount['处理中'] || 0 }}</strong>
      </div>
      <div class="status-item">
        <span>待客户确认</span>
        <strong>{{ statusCount['待客户确认'] || 0 }}</strong>
      </div>
      <div class="status-item">
        <span>待评价</span>
        <strong>{{ statusCount['待评价'] || 0 }}</strong>
      </div>
      <div class="status-item">
        <span>已完成</span>
        <strong>{{ statusCount['已完成'] || 0 }}</strong>
      </div>
      <div class="status-item">
        <span>平台介入</span>
        <strong>{{ statusCount['平台介入'] || 0 }}</strong>
      </div>
    </section>

    <section class="orders-shell">
      <el-tabs v-model="activeStatus" @tab-change="handleTabChange">
        <el-tab-pane v-for="item in statusOptions" :key="item.value" :label="item.label" :name="item.value" />
      </el-tabs>

      <div class="order-list" v-loading="loading">
        <el-empty v-if="!loading && filteredOrders.length === 0" description="暂无订单" />
        <template v-else>
          <article v-for="row in filteredOrders" :key="row.orderId" class="order-card">
            <div class="order-card-main">
              <div class="order-card-head">
                <div>
                  <strong>订单 #{{ row.orderId }}</strong>
                  <span>{{ formatServiceType(row.serviceTypeId) }}</span>
                </div>
              </div>
              <p class="order-summary">{{ getOrderSummary(row) }}</p>
              <div class="order-meta">
                <span>金额 {{ formatMoney(row.totalAmount) }}</span>
                <span>创建 {{ formatTime(row.createdTime) }}</span>
                <span>更新 {{ formatTime(row.updatedTime) }}</span>
              </div>
            </div>
            <div class="order-tags">
              <span v-if="isDirectOrder(row)" class="order-pill direct-pill">用户指定</span>
              <span :class="['order-pill', `status-pill-${getStatusType(row.status)}`]">
                {{ displayStatus(row.status) }}
              </span>
            </div>
            <div class="order-card-action">
              <el-button type="primary" @click="goDetail(row.orderId)">
                {{ getActionText(row.status) }}
              </el-button>
            </div>
          </article>
        </template>
      </div>
    </section>
  </div>
</template>

<style scoped>
.my-orders-page {
  min-height: 100vh;
  padding: 32px 20px 56px;
  background-color: #f5f5f5;
  color: #172033;
}

.page-head,
.status-strip,
.orders-shell {
  width: 100%;
  max-width: 1180px;
  margin-left: auto;
  margin-right: auto;
}

.page-head {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: flex-end;
  margin-bottom: 24px;
}

.eyebrow {
  margin: 0 0 8px;
  color: #2563eb;
  font-weight: 700;
}

h1 {
  margin: 0;
  font-size: 30px;
}

.subtext {
  margin: 10px 0 0;
  color: #667085;
}

.head-actions {
  display: flex;
  gap: 10px;
}

.status-strip {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 14px;
  margin-bottom: 18px;
}

.status-item {
  padding: 18px;
  background: #f6f8fc;
  border: 1px solid #e5eaf3;
  border-radius: 12px;
}

.status-item span {
  display: block;
  color: #667085;
  font-size: 13px;
  margin-bottom: 8px;
}

.status-item strong {
  font-size: 24px;
  color: #1d4ed8;
}

.orders-shell {
  padding: 18px;
  background: #ffffff;
  border: 1px solid #e5eaf3;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.order-list {
  min-height: 280px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-card {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 96px 132px;
  gap: 18px;
  align-items: center;
  min-height: 132px;
  padding: 18px;
  border: 1px solid #e5eaf3;
  border-radius: 12px;
  background: #ffffff;
}

.order-card-main {
  min-width: 0;
}

.order-card-head {
  display: flex;
  gap: 14px;
  align-items: center;
  margin-bottom: 10px;
}

.order-card-head strong {
  display: block;
  color: #172033;
  font-size: 17px;
}

.order-card-head span {
  display: block;
  margin-top: 4px;
  color: #667085;
  font-size: 13px;
}

.order-tags {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 9px;
  height: 100%;
}

.order-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 86px;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 800;
  line-height: 1;
  white-space: nowrap;
}

.direct-pill {
  color: #ffffff;
  background: #f59e0b;
  box-shadow: 0 6px 14px rgba(245, 158, 11, 0.18);
}

.status-pill-primary {
  color: #1d4ed8;
  background: #eff6ff;
  border: 1px solid #bfdbfe;
}

.status-pill-warning {
  color: #a16207;
  background: #fffbeb;
  border: 1px solid #fde68a;
}

.status-pill-success {
  color: #15803d;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
}

.status-pill-danger {
  color: #b42318;
  background: #fff1f0;
  border: 1px solid #fecaca;
}

.status-pill-info {
  color: #475467;
  background: #f2f4f7;
  border: 1px solid #e4e7ec;
}

.order-summary {
  margin: 0 0 12px;
  color: #344054;
  line-height: 1.7;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  word-break: break-word;
}

.order-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.order-meta span {
  padding: 5px 9px;
  border-radius: 999px;
  background: #f6f8fc;
  color: #667085;
  font-size: 12px;
}

.order-card-action {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.order-card-action .el-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 38px;
  border-radius: 10px;
  font-weight: 700;
  line-height: 1;
  padding: 0 14px;
  margin: 0;
}

.order-card-action .el-button :deep(span) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}

@media (max-width: 768px) {
  .page-head {
    flex-direction: column;
    align-items: stretch;
  }

  .status-strip {
    grid-template-columns: repeat(2, 1fr);
  }

  .order-card {
    grid-template-columns: 1fr;
  }

  .order-tags {
    flex-direction: row;
    justify-content: flex-start;
    height: auto;
  }

  .order-card-action {
    justify-content: flex-start;
    align-items: flex-start;
  }

  .order-card-action .el-button {
    width: 132px;
  }
}
</style>
