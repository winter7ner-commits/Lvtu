<script setup>
import { computed, onMounted, ref } from 'vue'
import { getOrders } from '../../api/admin'

const orders = ref([])
const selected = ref(null)
const statusFilter = ref('')
const keyword = ref('')

const filteredOrders = computed(() => {
  const key = keyword.value.trim().toLowerCase()
  return orders.value.filter((item) => {
    const statusMatched = !statusFilter.value || item.order_status === statusFilter.value
    const keyMatched = !key || [item.order_id, item.user_name, item.lawyer_name, item.service_name].some((field) =>
      String(field || '').toLowerCase().includes(key)
    )
    return statusMatched && keyMatched
  })
})

const statusOptions = computed(() => [...new Set(orders.value.map((item) => item.order_status).filter(Boolean))])

const formatMoney = (value) => value == null ? '-' : `￥${Number(value).toFixed(2)}`

const parseFormData = (value) => {
  if (!value) return {}
  try {
    return typeof value === 'string' ? JSON.parse(value) : value
  } catch {
    return { raw: value }
  }
}

onMounted(async () => {
  const result = await getOrders()
  orders.value = result.code === 200 ? result.data || [] : []
})
</script>

<template>
  <main class="page-shell">
    <div class="page-head">
      <h1>订单查询</h1>
      <div class="filters">
        <select v-model="statusFilter">
          <option value="">全部状态</option>
          <option v-for="item in statusOptions" :key="item" :value="item">{{ item }}</option>
        </select>
        <input v-model="keyword" placeholder="搜索订单、用户、律师、服务" />
      </div>
    </div>

    <section class="content-card">
      <div class="card-title">订单列表（{{ filteredOrders.length }}个）</div>
      <table class="data-table">
        <thead>
          <tr>
            <th>订单ID</th><th>用户</th><th>律师</th><th>服务</th><th>订单状态</th><th>订单金额</th><th>支付状态</th><th>支付金额</th><th>评价</th><th>创建时间</th><th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filteredOrders" :key="item.order_id">
            <td>{{ item.order_id }}</td>
            <td>{{ item.user_name || item.user_id }}</td>
            <td>{{ item.lawyer_name || '-' }}</td>
            <td>{{ item.service_name }}</td>
            <td>{{ item.order_status }}</td>
            <td>{{ formatMoney(item.total_amount) }}</td>
            <td>{{ item.payment_status || '-' }}</td>
            <td>{{ formatMoney(item.payment_amount) }}</td>
            <td>{{ item.total_score ? `${item.total_score}分` : '未评价' }}</td>
            <td>{{ item.created_time }}</td>
            <td><button class="detail-btn" @click="selected = item">详情</button></td>
          </tr>
        </tbody>
      </table>
    </section>

    <div v-if="selected" class="modal-mask" @click.self="selected = null">
      <div class="modal">
        <div class="modal-head">
          <h2>订单详情 #{{ selected.order_id }}</h2>
          <button @click="selected = null">×</button>
        </div>
        <div class="detail-grid">
          <p><b>用户：</b>{{ selected.user_name || selected.user_id }} / {{ selected.user_phone || '-' }}</p>
          <p><b>律师：</b>{{ selected.lawyer_name || '-' }} / {{ selected.law_firm || '-' }}</p>
          <p><b>服务：</b>{{ selected.service_name }} / {{ selected.service_description || '-' }}</p>
          <p><b>状态：</b>{{ selected.order_status }}</p>
          <p><b>订单金额：</b>{{ formatMoney(selected.total_amount) }}</p>
          <p><b>支付：</b>{{ selected.payment_status || '-' }} / {{ formatMoney(selected.payment_amount) }}</p>
          <p><b>支付时间：</b>{{ selected.payment_time || '-' }}</p>
          <p><b>创建时间：</b>{{ selected.created_time }}</p>
          <p><b>更新时间：</b>{{ selected.updated_time }}</p>
        </div>

        <h3>表单信息</h3>
        <table class="mini-table">
          <tbody>
            <tr v-for="(value, key) in parseFormData(selected.form_data)" :key="key">
              <th>{{ key }}</th>
              <td>{{ value }}</td>
            </tr>
          </tbody>
        </table>

        <h3>评价信息</h3>
        <div v-if="selected.evaluation_id" class="detail-grid">
          <p><b>综合评分：</b>{{ selected.total_score }}</p>
          <p><b>专业度：</b>{{ selected.professional_score }}</p>
          <p><b>响应速度：</b>{{ selected.responsiveness_score }}</p>
          <p><b>服务态度：</b>{{ selected.attitude_score }}</p>
          <p><b>评价状态：</b>{{ selected.evaluation_status }}</p>
          <p><b>举报次数：</b>{{ selected.report_count }}</p>
          <p class="wide"><b>评价内容：</b>{{ selected.evaluation_content || '-' }}</p>
        </div>
        <p v-else class="empty">暂无评价</p>
      </div>
    </div>
  </main>
</template>

<style scoped>
.page-shell { padding: 48px 46px; }
.page-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 32px; }
h1 { margin: 0; color: #1d8fd6; font-size: 31px; font-weight: 800; }
.filters { display: flex; gap: 12px; }
.filters input, .filters select { border: 1px solid #d8dee9; border-radius: 4px; padding: 12px 14px; font-size: 15px; }
.filters input { width: 300px; }
.content-card { background: #fff; border-radius: 6px; padding: 34px 42px; box-shadow: 0 5px 18px rgba(30, 55, 90, 0.08); overflow-x: auto; }
.card-title { margin-bottom: 24px; font-size: 23px; font-weight: 800; }
.data-table { min-width: 1420px; width: 100%; border-collapse: collapse; }
.data-table th { background: #f7f7fb; font-size: 16px; text-align: left; padding: 18px; }
.data-table td { border-top: 1px solid #e6e8ef; padding: 16px 18px; font-size: 15px; }
.detail-btn { border: 0; color: #fff; background: #409eff; border-radius: 4px; padding: 9px 16px; cursor: pointer; }
.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,.45); display: flex; align-items: center; justify-content: center; z-index: 200; }
.modal { width: min(920px, 92vw); max-height: 86vh; overflow: auto; background: #fff; border-radius: 8px; padding: 24px; }
.modal-head { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #edf0f5; margin-bottom: 18px; }
.modal-head h2 { margin: 0 0 16px; color: #1d8fd6; }
.modal-head button { border: 0; background: transparent; font-size: 28px; cursor: pointer; }
.detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px 22px; }
.detail-grid p { margin: 0; line-height: 1.8; }
.wide { grid-column: 1 / -1; }
.mini-table { width: 100%; border-collapse: collapse; margin-bottom: 18px; }
.mini-table th, .mini-table td { border: 1px solid #e6e8ef; padding: 10px 12px; text-align: left; }
.mini-table th { width: 180px; background: #f7f7fb; }
.empty { color: #909399; }
</style>
