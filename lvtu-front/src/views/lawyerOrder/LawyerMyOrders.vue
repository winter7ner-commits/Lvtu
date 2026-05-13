<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getLawyerProfileByUserId, getMyLawyerOrders } from '@/api/lawyerOrder'

const router = useRouter()
const loading = ref(false)
const orders = ref([])
const activeStatus = ref('')
const currentLawyerId = ref(null)

const statusOptions = [
  { label: '全部', value: '' },
  { label: '处理中', value: '处理中' },
  { label: '待评价', value: '待评价' },
  { label: '已完成', value: '已完成' }
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
  return orders.value.reduce((acc, item) => {
    acc[item.status] = (acc[item.status] || 0) + 1
    return acc
  }, {})
})

const unwrap = (res) => res?.data?.data ?? res?.data ?? []
const formatServiceType = (id) => serviceTypeMap[id] || `服务类型 ${id || '-'}`
const formatMoney = (value) => `¥${Number(value || 0).toFixed(2)}`
const formatTime = (value) => (value ? String(value).replace('T', ' ').slice(0, 16) : '-')

const getStatusType = (status) => {
  if (status === '处理中') return 'primary'
  if (status === '待评价') return 'warning'
  if (status === '已完成') return 'success'
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
      orders.value = []
      return
    }
    const params = { lawyerId }
    if (activeStatus.value) params.status = activeStatus.value
    const res = await getMyLawyerOrders(params)
    orders.value = unwrap(res)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '我的订单加载失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  loadOrders()
}

const goDetail = (orderId) => {
  router.push(`/lawyer/orders/${orderId}`)
}

onMounted(loadOrders)
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
        <span>处理中</span>
        <strong>{{ statusCount['处理中'] || 0 }}</strong>
      </div>
      <div class="status-item">
        <span>待评价</span>
        <strong>{{ statusCount['待评价'] || 0 }}</strong>
      </div>
      <div class="status-item">
        <span>已完成</span>
        <strong>{{ statusCount['已完成'] || 0 }}</strong>
      </div>
    </section>

    <section class="orders-shell">
      <el-tabs v-model="activeStatus" @tab-change="handleTabChange">
        <el-tab-pane v-for="item in statusOptions" :key="item.value" :label="item.label" :name="item.value" />
      </el-tabs>

      <div class="order-table" v-loading="loading">
        <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />
        <el-table v-else :data="orders" style="width: 100%">
          <el-table-column prop="orderId" label="订单编号" min-width="120" />
          <el-table-column label="服务类型" min-width="130">
            <template #default="{ row }">{{ formatServiceType(row.serviceTypeId) }}</template>
          </el-table-column>
          <el-table-column label="金额" min-width="110">
            <template #default="{ row }">{{ formatMoney(row.totalAmount) }}</template>
          </el-table-column>
          <el-table-column label="状态" min-width="110">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" effect="plain">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" min-width="150">
            <template #default="{ row }">{{ formatTime(row.createdTime) }}</template>
          </el-table-column>
          <el-table-column label="更新时间" min-width="150">
            <template #default="{ row }">{{ formatTime(row.updatedTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="130" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="goDetail(row.orderId)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </section>
  </div>
</template>

<style scoped>
.my-orders-page {
  max-width: 1180px;
  margin: 0 auto;
  padding: 32px 20px 56px;
  color: #172033;
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
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 18px;
}

.status-item {
  padding: 18px;
  background: #f6f8fc;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
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
  border-radius: 8px;
  box-shadow: 0 8px 22px rgba(30, 64, 175, 0.06);
}

.order-table {
  min-height: 280px;
}

@media (max-width: 768px) {
  .page-head {
    flex-direction: column;
    align-items: stretch;
  }

  .status-strip {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
