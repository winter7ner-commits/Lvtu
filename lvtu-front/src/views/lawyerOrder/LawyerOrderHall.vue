<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAvailableOrders } from '@/api/lawyerOrder'

const router = useRouter()
const orders = ref([])
const loading = ref(false)
const serviceTypeId = ref('')

const serviceTypeMap = {
  101: '在线法律咨询',
  102: '电话法律咨询',
  103: '文书代写',
  104: '合同审核',
  105: '婚姻家事',
  106: '诉讼代理'
}

const orderCount = computed(() => orders.value.length)
const totalAmount = computed(() => orders.value.reduce((sum, item) => sum + Number(item.totalAmount || 0), 0))

const unwrap = (res) => res?.data?.data ?? res?.data ?? []

const formatServiceType = (id) => serviceTypeMap[id] || `服务类型 ${id || '-'}`

const formatMoney = (value) => `¥${Number(value || 0).toFixed(2)}`

const formatTime = (value) => {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 16)
}

const loadOrders = async () => {
  loading.value = true
  try {
    const params = serviceTypeId.value ? { serviceTypeId: serviceTypeId.value } : {}
    const res = await getAvailableOrders(params)
    orders.value = unwrap(res)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '可接订单加载失败')
  } finally {
    loading.value = false
  }
}

const resetFilter = () => {
  serviceTypeId.value = ''
  loadOrders()
}

const goDetail = (orderId) => {
  router.push(`/lawyer/orders/${orderId}`)
}

onMounted(loadOrders)
</script>

<template>
  <div class="order-page">
    <section class="page-head">
      <div>
        <p class="eyebrow">律师接单</p>
        <h1>接单大厅</h1>
        <p class="subtext">查看平台当前待接单服务，进入详情后可确认需求并接单。</p>
      </div>
      <div class="head-actions">
        <el-button @click="router.push('/lawyer/orders/my')">我的订单</el-button>
        <el-button type="primary" @click="loadOrders">刷新列表</el-button>
      </div>
    </section>

    <section class="summary-band">
      <div class="metric">
        <span>待接订单</span>
        <strong>{{ orderCount }}</strong>
      </div>
      <div class="metric">
        <span>订单总额</span>
        <strong>{{ formatMoney(totalAmount) }}</strong>
      </div>
      <div class="filter-row">
        <el-select v-model="serviceTypeId" placeholder="服务类型" clearable @change="loadOrders" @clear="loadOrders">
          <el-option v-for="(label, id) in serviceTypeMap" :key="id" :label="label" :value="Number(id)" />
        </el-select>
        <el-button type="primary" @click="loadOrders">筛选</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </div>
    </section>

    <section class="list-panel" v-loading="loading">
      <el-empty v-if="!loading && orders.length === 0" description="暂无可接订单" />

      <div v-else class="order-list">
        <article v-for="order in orders" :key="order.orderId" class="order-card">
          <div class="order-main">
            <div class="order-title-row">
              <h2>订单 #{{ order.orderId }}</h2>
              <el-tag type="warning" effect="plain">{{ order.status }}</el-tag>
            </div>
            <div class="order-meta">
              <span>{{ formatServiceType(order.serviceTypeId) }}</span>
              <span>用户 {{ order.userId }}</span>
              <span>{{ formatTime(order.createdTime) }}</span>
            </div>
          </div>
          <div class="order-side">
            <strong>{{ formatMoney(order.totalAmount) }}</strong>
            <el-button type="primary" @click="goDetail(order.orderId)">查看详情</el-button>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<style scoped>
.order-page {
  min-height: 100vh;
  padding: 32px 20px 56px;
  background-color: #f5f5f5;
  color: #172033;
}

.page-head,
.summary-band,
.list-panel {
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
  font-weight: 700;
}

.subtext {
  margin: 10px 0 0;
  color: #667085;
}

.head-actions,
.filter-row {
  display: flex;
  gap: 10px;
  align-items: center;
}

.summary-band {
  display: grid;
  grid-template-columns: 180px 180px 1fr;
  gap: 16px;
  align-items: center;
  padding: 18px;
  background: #f6f8fc;
  border: 1px solid #e5eaf3;
  border-radius: 12px;
  margin-bottom: 18px;
}

.metric {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.metric span {
  color: #667085;
  font-size: 13px;
}

.metric strong {
  font-size: 24px;
  color: #1d4ed8;
}

.filter-row {
  justify-content: flex-end;
}

.filter-row :deep(.el-select) {
  width: 180px;
}

.list-panel {
  min-height: 280px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.order-card {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  padding: 20px;
  background: #ffffff;
  border: 1px solid #e5eaf3;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.order-title-row {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 12px;
}

.order-title-row h2 {
  margin: 0;
  font-size: 18px;
}

.order-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  color: #667085;
  font-size: 14px;
}

.order-side {
  min-width: 150px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-end;
  gap: 12px;
}

.order-side strong {
  font-size: 22px;
  color: #dc2626;
}

@media (max-width: 768px) {
  .page-head,
  .order-card {
    flex-direction: column;
    align-items: stretch;
  }

  .summary-band {
    grid-template-columns: 1fr;
  }

  .filter-row,
  .head-actions {
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .order-side {
    align-items: flex-start;
  }
}
</style>
