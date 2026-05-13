<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOrdersByUserId } from '@/api/order'

const router = useRouter()
const loading = ref(false)
const orders = ref([])
const activeStatus = ref('')

const serviceTypeMap = {
  101: '在线法律咨询',
  102: '电话法律咨询',
  103: '文书代写',
  104: '合同审核',
  105: '婚姻家事',
  106: '诉讼代理'
}

const statusOptions = ['待接单', '处理中', '待评价', '已完成', '已取消']

const filteredOrders = computed(() => {
  if (!activeStatus.value) return orders.value
  return orders.value.filter((order) => order.status === activeStatus.value)
})

const currentUserId = computed(() => {
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')
  return currentUser?.userId || Number(localStorage.getItem('userId')) || null
})

const formatServiceType = (id) => serviceTypeMap[id] || `服务类型 ${id || '-'}`
const formatMoney = (value) => `¥${Number(value || 0).toFixed(2)}`
const formatTime = (value) => (value ? String(value).replace('T', ' ').slice(0, 16) : '-')

const getStatusType = (status) => {
  if (status === '待接单') return 'info'
  if (status === '处理中') return 'primary'
  if (status === '待评价') return 'warning'
  if (status === '已完成') return 'success'
  return 'info'
}

const loadOrders = async () => {
  if (!currentUserId.value) {
    ElMessage.warning('请先登录后查看订单')
    return
  }
  loading.value = true
  try {
    const response = await getOrdersByUserId(currentUserId.value)
    orders.value = response?.code === 200 ? response.data || [] : []
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '订单加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadOrders)
</script>

<template>
  <div class="orders-page">
    <section class="page-head">
      <div>
        <p class="eyebrow">我的订单</p>
        <h1>服务订单</h1>
        <p class="subtext">查看已发布的法律服务订单，跟进律师处理进度。</p>
      </div>
      <div class="head-actions">
        <el-button @click="router.push('/order-create')">发布新业务</el-button>
        <el-button type="primary" @click="loadOrders">刷新</el-button>
      </div>
    </section>

    <section class="orders-shell">
      <el-tabs v-model="activeStatus">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane v-for="status in statusOptions" :key="status" :label="status" :name="status" />
      </el-tabs>

      <div v-loading="loading" class="order-table">
        <el-empty v-if="!loading && filteredOrders.length === 0" description="暂无订单" />
        <el-table v-else :data="filteredOrders" style="width: 100%">
          <el-table-column prop="orderId" label="订单编号" min-width="120" />
          <el-table-column label="服务类型" min-width="140">
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
          <el-table-column label="创建时间" min-width="160">
            <template #default="{ row }">{{ formatTime(row.createdTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="130" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="router.push(`/orders/${row.orderId}`)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </section>
  </div>
</template>

<style scoped>
.orders-page {
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
}
</style>
