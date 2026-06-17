<script setup>
import { computed, onMounted, ref } from 'vue'
import { getOrders } from '../../api/admin'

const orders = ref([])
const selected = ref(null)
const statusFilter = ref('')
const keyword = ref('')
const loading = ref(false)

const ORDER_STATUS_CLASSES = {
  待支付: 'badge-warning',
  待接单: 'badge-warning',
  处理中: 'badge-default',
  待客户确认: 'badge-warning',
  待评价: 'badge-warning',
  已完成: 'badge-success',
  已取消: 'badge-danger',
  平台介入: 'badge-danger'
}

const ORDER_STATUS_ORDER = ['待支付', '待接单', '处理中', '待客户确认', '待评价', '已完成', '已取消', '平台介入']

const ORDER_STATUS_CARD_CLASSES = {
  待支付: 'status-waiting',
  待接单: 'status-waiting',
  处理中: 'status-running',
  待客户确认: 'status-review',
  待评价: 'status-review',
  已完成: 'status-done',
  已取消: 'status-closed',
  平台介入: 'status-risk'
}

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

const statusSummary = computed(() =>
  ORDER_STATUS_ORDER.map((status) => ({
    label: status,
    value: status,
    className: ORDER_STATUS_CARD_CLASSES[status] || 'status-default',
    count: orders.value.filter((item) => item.order_status === status).length
  }))
)

const formatMoney = (value) => value == null ? '-' : `￥${Number(value).toFixed(2)}`

const orderStatusClass = (status) => ORDER_STATUS_CLASSES[status] || 'badge-default'
const paymentStatusClass = (status) => {
  if (status === '已支付') return 'badge-success'
  if (status === '已取消') return 'badge-danger'
  if (status) return 'badge-warning'
  return 'badge-default'
}

const evaluationText = (item) => item.total_score ? `${Number(item.total_score).toFixed(1)}分` : '未评价'
const evaluationClass = (item) => item.total_score ? 'badge-success' : 'badge-default'

const parseFormData = (value) => {
  if (!value) return {}
  try {
    return typeof value === 'string' ? JSON.parse(value) : value
  } catch {
    return { raw: value }
  }
}

const loadOrders = async () => {
  loading.value = true
  try {
    const result = await getOrders()
    orders.value = result.code === 200 ? result.data || [] : []
  } finally {
    loading.value = false
  }
}

onMounted(loadOrders)
</script>

<template>
  <main class="page-shell">
    <section class="page-head">
      <div>
        <h1>订单查询</h1>
        <p>查看订单状态、支付情况、评价状态和订单表单详情。</p>
      </div>
      <button class="primary-btn" type="button" :disabled="loading" @click="loadOrders">
        {{ loading ? '刷新中' : '刷新' }}
      </button>
    </section>

    <section class="summary-band order-summary">
      <button
        v-for="item in statusSummary"
        :key="item.label"
        type="button"
        :class="[item.className, { active: statusFilter === item.value }]"
        @click="statusFilter = statusFilter === item.value ? '' : item.value"
      >
        <span>{{ item.label }}</span>
        <strong>{{ item.count }}</strong>
      </button>
    </section>

    <section class="content-card order-card">
      <div class="order-list-head">
        <div class="card-title">订单列表（{{ filteredOrders.length }}个）</div>
        <input v-model="keyword" class="order-search" placeholder="搜索订单、用户、律师、服务" />
      </div>

      <div v-if="loading" class="empty">加载中...</div>
      <div v-else class="order-table-frame">
        <table class="data-table order-table">
          <thead>
            <tr>
              <th class="sticky-col">订单ID</th>
              <th>用户</th>
              <th>律师</th>
              <th>服务</th>
              <th>订单状态</th>
              <th>订单金额</th>
              <th>支付状态</th>
              <th>支付金额</th>
              <th>评价</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="filteredOrders.length === 0">
              <td colspan="11" class="empty">暂无匹配订单</td>
            </tr>
            <tr v-for="item in filteredOrders" :key="item.order_id">
              <td class="sticky-col strong-cell">#{{ item.order_id }}</td>
              <td>
                <div class="entity-cell">
                  <strong>{{ item.user_name || item.user_id || '-' }}</strong>
                  <small>用户ID {{ item.user_id || '-' }}</small>
                </div>
              </td>
              <td>
                <div class="entity-cell">
                  <strong>{{ item.lawyer_name || '-' }}</strong>
                  <small>律师ID {{ item.lawyer_id || '-' }}</small>
                </div>
              </td>
              <td>{{ item.service_name }}</td>
              <td><span :class="['status-badge', orderStatusClass(item.order_status)]">{{ item.order_status || '-' }}</span></td>
              <td>{{ formatMoney(item.total_amount) }}</td>
              <td><span :class="['status-badge', paymentStatusClass(item.payment_status)]">{{ item.payment_status || '-' }}</span></td>
              <td>{{ formatMoney(item.payment_amount) }}</td>
              <td><span :class="['status-badge', evaluationClass(item)]">{{ evaluationText(item) }}</span></td>
              <td>{{ item.created_time }}</td>
              <td><button class="detail-btn" @click="selected = item">详情</button></td>
            </tr>
          </tbody>
        </table>
      </div>
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
.page-head p { margin: 8px 0 0; }
:global(#app .admin-main .summary-band.order-summary) {
  grid-template-columns: repeat(8, minmax(84px, 1fr));
  align-items: stretch;
  gap: 8px;
}
.order-summary {
  gap: 8px;
}
:global(#app .admin-main .summary-band.order-summary button) {
  min-height: 58px;
  min-width: 0;
  padding: 9px 10px;
}
.order-summary button {
  display: grid;
  gap: 4px;
  text-align: left;
  border: 1px solid var(--status-border, #e5eaf3);
  border-radius: 8px;
  background: var(--status-bg, #ffffff);
  color: var(--status-text, #1d4ed8);
  cursor: pointer;
}
.order-summary button.active {
  border-color: var(--status-text, #1d4ed8);
  box-shadow: inset 0 0 0 1px var(--status-text, #1d4ed8);
}
.order-summary span {
  color: var(--status-label, #344054);
  font-size: 12px;
  font-weight: 800;
  line-height: 1.2;
  white-space: nowrap;
}
.order-summary strong {
  color: var(--status-text, #1d4ed8);
  font-size: 20px;
  line-height: 1.1;
}
.status-waiting { --status-bg: #fff7ed; --status-border: #fed7aa; --status-text: #c2410c; --status-label: #7c2d12; }
.status-running { --status-bg: #eff6ff; --status-border: #bfcefa; --status-text: #1d4ed8; --status-label: #1e3a8a; }
.status-review { --status-bg: #fef3c7; --status-border: #fde68a; --status-text: #92400e; --status-label: #78350f; }
.status-done { --status-bg: #f0fdf4; --status-border: #bbf7d0; --status-text: #15803d; --status-label: #14532d; }
.status-closed { --status-bg: #f8fafc; --status-border: #d0d5dd; --status-text: #475467; --status-label: #344054; }
.status-risk { --status-bg: #fff1f0; --status-border: #ffd1cf; --status-text: #b42318; --status-label: #7a271a; }
.order-card {
  overflow: hidden;
}
.order-list-head {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin: -22px -22px 0;
  padding: 18px 22px;
  border-bottom: 1px solid var(--admin-divider);
  background: #ffffff;
}
.order-list-head .card-title {
  margin: 0;
}
.order-search {
  width: min(340px, 100%);
  padding: 10px 12px;
}
.order-table-frame {
  width: 100%;
  max-height: calc(100vh - 390px);
  min-height: 320px;
  overflow: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}
.order-table-frame::-webkit-scrollbar {
  display: none;
  width: 0;
  height: 0;
}
.order-table {
  width: max-content;
  min-width: 1520px;
  table-layout: auto;
}
.order-table thead th {
  position: sticky;
  top: 0;
  z-index: 4;
  min-height: 50px;
  box-sizing: border-box;
  padding: 16px 10px 13px;
  line-height: 1.5;
  vertical-align: middle;
  white-space: nowrap;
}
.order-table td {
  padding: 12px 10px;
  white-space: nowrap;
  word-break: keep-all;
  overflow-wrap: normal;
}
.order-table th:nth-child(1),
.order-table td:nth-child(1) {
  min-width: 110px;
}
.order-table th:nth-child(2),
.order-table td:nth-child(2) {
  min-width: 190px;
}
.order-table th:nth-child(3),
.order-table td:nth-child(3) {
  min-width: 190px;
}
.order-table th:nth-child(4),
.order-table td:nth-child(4) {
  min-width: 150px;
}
.order-table th:nth-child(5),
.order-table td:nth-child(5) {
  min-width: 120px;
}
.order-table th:nth-child(7),
.order-table td:nth-child(7) {
  min-width: 120px;
}
.order-table th:nth-child(6),
.order-table td:nth-child(6) {
  min-width: 116px;
}
.order-table th:nth-child(8),
.order-table td:nth-child(8) {
  min-width: 116px;
}
.order-table th:nth-child(9),
.order-table td:nth-child(9) {
  min-width: 88px;
}
.order-table th:nth-child(10),
.order-table td:nth-child(10) {
  min-width: 184px;
}
.order-table th:nth-child(11),
.order-table td:nth-child(11) {
  min-width: 80px;
}
.sticky-col {
  position: sticky;
  left: 0;
  z-index: 2;
  min-width: 110px;
  background: #ffffff;
  box-shadow: 1px 0 0 #edf1f7;
}
.order-table th.sticky-col {
  z-index: 5;
  background: var(--admin-panel);
}
.strong-cell { color: var(--admin-text); font-weight: 900; }
.entity-cell { display: grid; gap: 2px; min-width: max-content; }
.entity-cell strong { color: var(--admin-text); font-weight: 800; }
.entity-cell small { color: var(--admin-text-muted); font-size: 12px; }
.entity-cell strong,
.entity-cell small {
  white-space: nowrap;
}
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
@media (max-width: 900px) {
  :global(#app .admin-main .summary-band.order-summary) {
    grid-template-columns: repeat(4, minmax(104px, 1fr));
    overflow-x: auto;
  }
  .order-list-head {
    position: static;
    flex-direction: column;
    align-items: stretch;
  }
  .order-table-frame {
    max-height: none;
  }
}
</style>
